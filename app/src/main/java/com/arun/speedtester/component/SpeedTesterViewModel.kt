package com.arun.speedtester.component

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class SpeedTesterViewModel: ViewModel() {

    private val _state = mutableStateOf(simState())
}