package com.arun.speedtester.component

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.*
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.bmartel.speedtest.SpeedTestReport
import fr.bmartel.speedtest.SpeedTestSocket
import fr.bmartel.speedtest.inter.ISpeedTestListener
import fr.bmartel.speedtest.model.SpeedTestError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt
import android.telephony.SubscriptionInfo

import android.content.pm.PackageManager
import android.net.ConnectivityManager

import androidx.core.app.ActivityCompat

import android.telephony.SubscriptionManager

import android.telephony.TelephonyManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.lang.Exception
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import android.net.NetworkInfo
import kotlinx.coroutines.delay


@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
@HiltViewModel
class SpeedTesterViewModel @Inject constructor(
    telephonyManager: TelephonyManager?,
    telephonySubscriptionManager: SubscriptionManager?,
    cm: ConnectivityManager?
) : ViewModel() {
    val telephonySubscriptionManager = telephonySubscriptionManager
    //setting up the channel to
//    var simState = simState()
//    private var _state = Channel<simState>()
//    val state = _state.receiveAsFlow()


    var sim1DownloadSpeed by mutableStateOf("-")
    fun updateSim1DownloadSpeed(speed: String) {
        sim1DownloadSpeed = speed
    }

    var sim1UploadSpeed by mutableStateOf("-")
    fun updateSim1UploadSpeed(speed: String) {
        sim1UploadSpeed = speed
    }

    var sim2DownloadSpeed by mutableStateOf("-")
    fun updateSim2DownloadSpeed(speed: String) {
        sim2DownloadSpeed = speed
    }

    var sim2UploadSpeed by mutableStateOf("-")
    fun updateSim2UploadSpeed(speed: String) {
        sim2UploadSpeed = speed
    }

    var sim1Name by mutableStateOf("Not Detected")
    fun updateSim1Name(name: String) {
        sim1Name = name
    }

    var sim2Name by mutableStateOf("Not Detected")
    fun updateSim2Name(name: String) {
        sim2Name = name
    }

    var sim1RssiValue by mutableStateOf("-")
    fun updateSim1RssiValue(value: String) {
        sim1RssiValue = value
    }

    var sim2RssiValue by mutableStateOf("-")
    fun updateSim2RssiValue(value: String) {
        sim2RssiValue = value
    }

    private var testRunning = ""
    private var simUsedInTest = "sim1"
    var showScaffold by mutableStateOf(false)
    var messageForScaffoldState by mutableStateOf("")
    var messageOfTest by mutableStateOf("")

    /**
     * spedd examples server uri.
     */
    private val SPEED_TEST_SERVER_URI_DL = "http://ipv4.ikoula.testdebit.info/10M.iso"

    /**
     * set socket timeout to 3s.
     */
    private val SOCKET_TIMEOUT = 5000

    /**
     * speed test socket.
     */
    private val speedTestSocket = SpeedTestSocket()

    /**
     * speed examples server uri.
     */
    private val SPEED_TEST_SERVER_URI_UL = "http://ipv4.ikoula.testdebit.info/"

    /**
     * upload 2Mo file size.
     */
    private val FILE_SIZE = 2000000

    private var chainCount = 1

    private val hasPermission = true


    init {
        viewModelScope.launch {
            getSimOperatorName(telephonySubscriptionManager, hasPermission)
            getSimRssiValue(telephonyManager, hasPermission)
            test(speedTestSocket)
            getNetworkInfo(cm)
        }
    }

//    fun updateSimName(name: String) = viewModelScope.launch{
//        simState.sim1DownloadSpeed = name
//        _state.send(simState)
//        Log.d("State In VM", _state.toString())
//    }

    fun runTest(name: String, subscriptionManager: SubscriptionManager?) = CoroutineScope(Dispatchers.Default).launch {
        if (testRunning.isNotBlank()) {
            showScaffold = true
            messageForScaffoldState = "A network test Is already running."
            showScaffold = false
        } else {
            Log.d("Run Test", name)
            Log.d("Download Test Started", "Download Test Running")
            testRunning = "Download"
            showScaffold = true
            val message = if(simUsedInTest == "sim1") "A network test started for $sim1Name."
            else "A network test started for $sim2Name."
            messageForScaffoldState = message
            speedTestSocket.startDownload(SPEED_TEST_SERVER_URI_DL)
            showScaffold = false
        }
    }

    fun hideScaffold() {
        showScaffold = false
    }

    @SuppressLint("MissingPermission")
    fun getSimRssiValue(telephonyManager: TelephonyManager?, hasPermission: Boolean) {
        if (hasPermission) {
            val simInfo = telephonyManager?.allCellInfo as ArrayList
            if (simInfo.isNotEmpty()) {
                for (i in simInfo.indices) {
                    if (i == 0 && simInfo[0].isRegistered) {
                        sim1RssiValue = getRssiValue(simInfo[0]).toString()
                    } else if (simInfo[1].isRegistered) {
                        sim2RssiValue = getRssiValue(simInfo[1]).toString()
                    }
                }
            }

        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    fun getSimOperatorName(
        telephonySubscriptionManager: SubscriptionManager?,
        hasPermission: Boolean
    ) {
        if (hasPermission) {
            val localList: List<*> =
                telephonySubscriptionManager?.activeSubscriptionInfoList ?: listOf<Any>()
            if (localList.isNotEmpty()) {
                for (i in localList.indices) {
                    val index = (localList[i] as SubscriptionInfo).simSlotIndex
                    Log.d("Slot of sim",index.toString())
                    val simName = (localList[0] as SubscriptionInfo).carrierName.toString()
                    if(index == 1){
                        sim1Name = simName
                    }
                    else{
                        sim2Name = simName
                    }

                }
            }
        }
    }

    private fun getRssiValue(cellInfo: CellInfo): Int {
        val strength1 = when (cellInfo) {
            is CellInfoLte -> cellInfo.cellSignalStrength.dbm
            is CellInfoGsm -> cellInfo.cellSignalStrength.dbm
            is CellInfoCdma -> cellInfo.cellSignalStrength.dbm
            is CellInfoWcdma -> cellInfo.cellSignalStrength.dbm
            else -> 0
        }
        return strength1
    }

    private fun test(speedTestSocket: SpeedTestSocket) {
        speedTestSocket.addSpeedTestListener(object : ISpeedTestListener {
            override fun onCompletion(report: SpeedTestReport) {
//                Log.d("Completed Test", "[COMPLETED] rate in octet/s : " + report.transferRateOctet)
//                Log.d("Completed test", "[COMPLETED] rate in bit/s   : " + report.transferRateBit)

                val result = ((report.transferRateBit / (10000).toBigDecimal()).toFloat()
                    .roundToInt() / 100F).toString()

                if (testRunning == "Download")
                    viewModelScope.launch {
                        Log.d("Download Test Ended", "Download Test Ended")
                        if(simUsedInTest == "sim1") sim1DownloadSpeed = result
                        else sim2DownloadSpeed = result

                    }
                else {
                    viewModelScope.launch {
                        Log.d("Upload Test Ended", "Upload Test Ended")
                        if(simUsedInTest == "sim1") sim1UploadSpeed = result
                        else sim2UploadSpeed = result
                    }
                }
                if (chainCount > 0) {
                    if (chainCount % 2 != 0) {
                        Log.d("Upload Test Started", "Upload Test Running")
                        testRunning = "Upload"
                        speedTestSocket.startUpload(SPEED_TEST_SERVER_URI_UL, FILE_SIZE);
                    } else {
                        speedTestSocket.startDownload(SPEED_TEST_SERVER_URI_DL);
                    }
                    chainCount--;
                } else {
                    testRunning = ""
                    showScaffold = true
                    val message = if(simUsedInTest == "sim1") "Network test ended for $sim1Name"
                    else "Network test ended for $sim2Name"
                    messageForScaffoldState = "Network test ended for $message. "
                    showScaffold = false
                    Log.d("Test Ended", "Test Ended Yahoo")
                }

            }

            override fun onError(speedTestError: SpeedTestError, errorMessage: String) {
                // called when a download/upload error occur
                val value = if(simUsedInTest == "sim1") sim1Name else sim2Name
                messageForScaffoldState = "It looks like $value don't have a active network connection. Please switch on mobile data before running test"
                showScaffold = true
                showScaffold = false
                testRunning = ""
            }

            override fun onProgress(percent: Float, report: SpeedTestReport) {
                // called to notify download/upload progress
//                Log.d("Progress", "[PROGRESS] progress : $percent%")
//                Log.d("Progress", "[PROGRESS] rate in octet/s : " + report.transferRateOctet)
//                Log.d("Progress", "[PROGRESS] rate in bit/s   : " + report.transferRateBit)
                val result = ((report.transferRateBit / (10000).toBigDecimal()).toFloat()
                    .roundToInt() / 100F).toString()

                if (testRunning == "Download")
                    if(simUsedInTest == "sim1") {
                        messageOfTest = "Download test running on $sim1Name - $percent"
                        sim1DownloadSpeed = result
                    }
                    else {
                        messageOfTest = "Download test running on $sim2Name - $percent"
                        sim2DownloadSpeed = result
                    }
                else {
                    if(simUsedInTest == "sim1") {
                        messageOfTest = "Upload test running on $sim1Name - $percent"
                        sim1UploadSpeed = result
                    }
                    else {
                        messageOfTest = "Upload test running on $sim2Name - $percent"
                        sim2UploadSpeed = result
                    }
                }
            }
        })
    }



    private fun getDefaultDataSubscriptionId(subscriptionManager: SubscriptionManager?): Int? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val nDataSubscriptionId = SubscriptionManager.getDefaultDataSubscriptionId()
            if (nDataSubscriptionId != SubscriptionManager.INVALID_SUBSCRIPTION_ID) {
                return nDataSubscriptionId
            }
        }
        return try {
            val subscriptionClass = Class.forName(subscriptionManager!!.javaClass.name)
            try {
                val getDefaultDataSubscriptionId =
                    subscriptionClass.getMethod("getDefaultDataSubId")
                try {
                    getDefaultDataSubscriptionId.invoke(subscriptionManager) as Int
                } catch (e: IllegalAccessException) {
                    return null
                } catch (e: InvocationTargetException) {
                    return null
                }
            } catch (e: NoSuchMethodException) {
                return null
            }
        } catch (e: ClassNotFoundException) {
            return null
        }
    }
}


fun getNetworkInfo(cm: ConnectivityManager?): String{
    return if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1){
        Log.d("Network manager",cm!!.activeNetwork.toString())
        cm.activeNetwork.toString()
    }else{
        Log.d("Network manager",cm!!.activeNetworkInfo.toString())
        cm.activeNetworkInfo.toString()
    }
}