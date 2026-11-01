package com.easylabour.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easylabour.app.data.model.UserDto
import com.easylabour.app.data.repository.SessionManager
import com.easylabour.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LabourViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<LabourUiState>(LabourUiState.Idle)
    val uiState: StateFlow<LabourUiState> = _uiState

    val currentUser = sessionManager.currentUser

    fun updateSkills(skills: Set<String>) {
        val userId = currentUser?.id ?: return
        viewModelScope.launch {
            _uiState.value = LabourUiState.Loading
            try {
                val updatedUser = userRepository.userService.updateSkills(userId, skills)
                sessionManager.currentUser = updatedUser
                _uiState.value = LabourUiState.Success
            } catch (e: Exception) {
                _uiState.value = LabourUiState.Error(e.message ?: "Failed to update skills")
            }
        }
    }
}

sealed class LabourUiState {
    object Idle : LabourUiState()
    object Loading : LabourUiState()
    object Success : LabourUiState()
    data class Error(val message: String) : LabourUiState()
}
