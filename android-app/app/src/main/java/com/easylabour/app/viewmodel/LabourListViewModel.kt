package com.easylabour.app.viewmodel

import androidx.lifecycle.ViewModel
import com.easylabour.app.data.api.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LabourListViewModel @Inject constructor(
    val userService: UserService
) : ViewModel()
