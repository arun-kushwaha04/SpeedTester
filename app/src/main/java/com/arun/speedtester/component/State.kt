package com.arun.speedtester.component

data class simState(
    val sim1: String? = null,
    val sim1Rssi: Int? = null,
    val sim2: String? = null,
    val sim2Rssi: String? = null,
    val sim1UploadSpeed: String? = null,
    val sim1DownloadSpeed: String? = null,
    val sim2UploadSpeed: String? = null,
    val sim2DownloadSpeed: String? = null
)