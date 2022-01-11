package com.arun.speedtester.component

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.arun.speedtester.ui.theme.Spacing

@Composable
fun SpeedTester(
    viewModel: SpeedTesterViewModel = hiltViewModel()
){
    val state = viewModel.state.value
    Log.d("State",state.toString())

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(state.sim1 != null)SimBox(
            simRssi = state.sim1Rssi,
            simName = state.sim1,
            uploadSpeed = state.sim1UploadSpeed,
            downloadSpeed = state.sim1DownloadSpeed
        )
        if(state.sim2 != null)SimBox(
            simRssi = state.sim2Rssi,
            simName = state.sim2,
            uploadSpeed = state.sim2UploadSpeed,
            downloadSpeed = state.sim2DownloadSpeed
        )
    }

}

@Composable
fun SimBox(
    simRssi:Int?,
    simName:String?,
    uploadSpeed:String?,
    downloadSpeed:String?
){
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(MaterialTheme.Spacing.medium)) {
        if (simName != null) {
            Text(text = simName)
        }
        else{
            Text(text = "-")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Rssi")
                Text(text = if(simRssi!=null) simRssi.toString() else "-")
            }
            Column {
                Text(text = "Download")
                Text(text = downloadSpeed ?: "-")
            }
            Column {
                Text(text = "Upload")
                Text(text = uploadSpeed ?: "-")
            }
        }
        Button(onClick = { /*TODO*/ }) {
            Text(text = "Check")
        }
    }
}