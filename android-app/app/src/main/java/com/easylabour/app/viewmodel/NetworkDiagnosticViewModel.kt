package com.easylabour.app.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easylabour.app.data.remote.UrlProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NetworkDiagnosticViewModel @Inject constructor(
    private val urlProvider: UrlProvider,
    private val okHttpClient: OkHttpClient
) : ViewModel() {

    private val _currentUrl = mutableStateOf(urlProvider.getBaseUrl())
    val currentUrl: State<String> = _currentUrl

    private val _diagnosticResult = mutableStateOf<String?>(null)
    val diagnosticResult: State<String?> = _diagnosticResult

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    fun updateUrl(newUrl: String) {
        _currentUrl.value = newUrl
        urlProvider.setBaseUrl(newUrl)
    }

    fun resetToDefault() {
        urlProvider.resetToDefault()
        _currentUrl.value = urlProvider.getBaseUrl()
    }

    fun autoDiscover() {
        viewModelScope.launch {
            _isLoading.value = true
            _diagnosticResult.value = "Scanning subnet for backend..."
            
            val deviceIp = getIPAddress()
            if (deviceIp == null) {
                _diagnosticResult.value = "Failed to get device IP. Ensure you are connected to Wi-Fi."
                _isLoading.value = false
                return@launch
            }

            val subnet = deviceIp.substringBeforeLast(".")
            val foundIp = withContext(Dispatchers.IO) {
                // Scan common IPs first (1, 100-110, then the rest)
                val ipsToScan = mutableListOf<String>()
                ipsToScan.add("$subnet.1")
                for (i in 100..110) ipsToScan.add("$subnet.$i")
                for (i in 2..254) if (!ipsToScan.contains("$subnet.$i")) ipsToScan.add("$subnet.$i")

                ipsToScan.chunked(10).forEach { chunk ->
                    val jobs = chunk.map { ip ->
                        async {
                            val url = "http://$ip:8080/"
                            try {
                                val request = Request.Builder().url(url).get().build()
                                // Short timeout for scanning
                                val client = okHttpClient.newBuilder()
                                    .connectTimeout(500, java.util.concurrent.TimeUnit.MILLISECONDS)
                                    .build()
                                
                                client.newCall(request).execute().use { response ->
                                    if (response.code < 500) ip else null
                                }
                            } catch (e: Exception) {
                                null
                            }
                        }
                    }
                    val result = jobs.awaitAll().firstOrNull { it != null }
                    if (result != null) return@withContext result
                }
                null
            }

            if (foundIp != null) {
                val newUrl = "http://$foundIp:8080/"
                updateUrl(newUrl)
                _diagnosticResult.value = "SUCCESS! Found backend at $newUrl. URL has been updated."
            } else {
                _diagnosticResult.value = "Auto-discovery failed. Could not find any server listening on port 8080 in subnet $subnet.x.\n\nPlease check your laptop firewall."
            }
            _isLoading.value = false
        }
    }

    private fun getIPAddress(): String? {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            for (networkInterface in Collections.list(interfaces)) {
                val addresses = networkInterface.inetAddresses
                for (address in Collections.list(addresses)) {
                    if (!address.isLoopbackAddress && address is InetAddress) {
                        val host = address.hostAddress
                        if (!host.contains(":")) return host
                    }
                }
            }
        } catch (e: Exception) {}
        return null
    }

    fun testConnection() {
        viewModelScope.launch {
            _isLoading.value = true
            _diagnosticResult.value = "Testing connection to ${_currentUrl.value}..."
            
            val result = withContext(Dispatchers.IO) {
                try {
                    // We just try to reach the root or any endpoint to see if we get a response
                    val request = Request.Builder()
                        .url(_currentUrl.value)
                        .get()
                        .build()
                    
                    okHttpClient.newCall(request).execute().use { response ->
                        "Success! Server reached.\nStatus Code: ${response.code}\nMessage: ${response.message}\n\nNote: A 404 is still a 'Success' in terms of connectivity!"
                    }
                } catch (e: Exception) {
                    val errorType = e.javaClass.simpleName
                    val errorMessage = e.message ?: "Unknown error"
                    
                    val troubleshooting = when {
                        errorType.contains("Timeout") -> "TIMEOUT: The device can't see the server. Check if both are on the same Wi-Fi and if your laptop firewall is OFF for port 8080."
                        errorType.contains("Refused") -> "CONNECTION REFUSED: The device reached the laptop, but the backend isn't listening on port 8080. Check if the API Gateway is running."
                        else -> "ERROR: $errorMessage"
                    }
                    
                    "Failed to connect:\n$errorType: $errorMessage\n\n$troubleshooting"
                }
            }
            
            _diagnosticResult.value = result
            _isLoading.value = false
        }
    }
}
