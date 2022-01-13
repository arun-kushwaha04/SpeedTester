package com.arun.speedtester.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arun.speedtester.ui.theme.Spacing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
@Composable
fun SpeedTester(
    viewModel: SpeedTesterViewModel = hiltViewModel(),
    scope: CoroutineScope,
    scaffoldState: ScaffoldState
) {

    LaunchedEffect(key1 = viewModel.messageForScaffoldState) {
        if (viewModel.showScaffold) {
            scope.launch {
                scaffoldState.snackbarHostState.showSnackbar(viewModel.messageForScaffoldState)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(10.dp),horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = viewModel.messageOfTest)
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SimBox(
                simRssi = viewModel.sim1RssiValue,
                simName = viewModel.sim1Name,
                uploadSpeed = viewModel.sim1UploadSpeed,
                downloadSpeed = viewModel.sim1DownloadSpeed,
                onClick = { viewModel.runTest("sim1") }
            )
            SimBox(
                simRssi = viewModel.sim2RssiValue,
                simName = viewModel.sim2Name,
                uploadSpeed = viewModel.sim2UploadSpeed,
                downloadSpeed = viewModel.sim2DownloadSpeed,
                onClick = { viewModel.runTest("sim2") }
            )
        }
    }
}

@Composable
fun SimBox(
    simRssi: String?,
    simName: String?,
    uploadSpeed: String?,
    downloadSpeed: String?,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.Spacing.medium)
    ) {
        if (simName != null) {
            Text(text = simName)
        } else {
            Text(text = "-")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Rssi")
                Text(text = if (simRssi != "-") simRssi.toString() else "-")
            }
            Column {
                Text(text = "Download")
                Text(text = if (downloadSpeed != "-") "$downloadSpeed Mbps" else "-")
            }
            Column {
                Text(text = "Upload")
                Text(text = if (uploadSpeed != "-") "$uploadSpeed Mbps" else "-")
            }
        }
        Button(onClick = onClick, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text(text = if(downloadSpeed == "-" && uploadSpeed == "-")"Check" else "Re-Check")
        }
    }
}