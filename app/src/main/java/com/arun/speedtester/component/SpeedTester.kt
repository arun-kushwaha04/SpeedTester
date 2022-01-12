package com.arun.speedtester.component

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.arun.speedtester.ui.theme.Spacing
import fr.bmartel.speedtest.SpeedTestSocket
import kotlinx.coroutines.flow.collect

@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
@Composable
fun SpeedTester(
    viewModel: SpeedTesterViewModel = hiltViewModel()
){
    var text by remember { mutableStateOf("-")}
    var text2 by remember { mutableStateOf("-")}
    var state by remember { mutableStateOf(simState()) }


    Log.d("Current State", state.toString())
    LaunchedEffect(key1 = true){
        viewModel.state.collect{
            state = it
            text = if(it.sim1DownloadSpeed == null) "-" else it.sim1DownloadSpeed.toString()
            text2 = if(it.sim1UploadSpeed == null) "-" else it.sim1UploadSpeed.toString()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(state.sim1 != null)SimBox(
            simRssi = state.sim1Rssi,
            simName = state.sim1,
            uploadSpeed = text2,
            downloadSpeed = text,
            newFun = {
                viewModel.runTest("sim1")
            }
        )
        if(state.sim2 != null)SimBox(
            simRssi = state.sim2Rssi,
            simName = state.sim2,
            uploadSpeed = state.sim2UploadSpeed,
            downloadSpeed = state.sim2DownloadSpeed,
            newFun = { viewModel.runTest("sim2") }
        )
    }

}

@Composable
fun SimBox(
    simRssi:Int?,
    simName:String?,
    uploadSpeed:String?,
    downloadSpeed:String?,
    newFun: () -> Unit
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
                Text(text = if(downloadSpeed != null) "$downloadSpeed Mbps" else "-")
            }
            Column {
                Text(text = "Upload")
                Text(text = if(uploadSpeed != null) "$uploadSpeed Mbps" else "-")
            }
        }
        Button(onClick = newFun) {
            Text(text = "Check")
        }
    }
}