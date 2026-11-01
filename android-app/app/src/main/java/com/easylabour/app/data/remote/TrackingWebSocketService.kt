package com.easylabour.app.data.remote

import com.easylabour.app.data.model.LocationUpdate
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONObject
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.dto.LifecycleEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackingWebSocketService @Inject constructor() {
    private val gson = Gson()
    private val _locationFlow = MutableStateFlow<LocationUpdate?>(null)
    val locationFlow: StateFlow<LocationUpdate?> = _locationFlow

    private var stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://10.0.2.2:8080/ws-tracking/websocket")

    fun connect(bookingId: String) {
        stompClient.lifecycle().subscribe { event ->
            when (event.type) {
                LifecycleEvent.Type.OPENED -> println("WebSocket Opened")
                LifecycleEvent.Type.ERROR -> println("WebSocket Error: ${event.exception}")
                else -> {}
            }
        }

        stompClient.topic("/topic/tracking/$bookingId").subscribe { topicMessage ->
            val json = JSONObject(topicMessage.payload)
            val update = gson.fromJson(json.toString(), LocationUpdate::class.java)
            _locationFlow.value = update
        }

        stompClient.connect()
    }

    fun disconnect() {
        stompClient.disconnect()
    }
}
