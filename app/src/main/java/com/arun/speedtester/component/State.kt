package com.arun.speedtester.component

data class simState(
    var sim1: String? = null,
    var sim1Rssi: Int? = null,
    var sim2: String? = null,
    var sim2Rssi: Int? = null,
    var sim1UploadSpeed: String? = null,
    var sim1DownloadSpeed: String? = null,
    val sim2UploadSpeed: String? = null,
    val sim2DownloadSpeed: String? = null,
    val hasPermission:Boolean = false
)