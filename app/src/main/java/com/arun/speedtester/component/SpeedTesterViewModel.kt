package com.arun.speedtester.component

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Build
import android.telephony.*
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.arun.speedtester.checkHasAllPermission
import com.arun.speedtester.requestPermission
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import fr.bmartel.speedtest.SpeedTestReport
import fr.bmartel.speedtest.SpeedTestSocket
import fr.bmartel.speedtest.inter.ISpeedTestListener
import fr.bmartel.speedtest.model.SpeedTestError
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.concurrent.thread

@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
@HiltViewModel
class SpeedTesterViewModel @Inject constructor(
    telephonyManager: TelephonyManager?,
    telephonySubscriptionManager: SubscriptionManager?
) : ViewModel() {
    private var _state = mutableStateOf(simState())
    val state = _state

    val hasPermission = true

    init {
        getSimRssiValue(telephonyManager, hasPermission)
        getSimOperatorName(telephonySubscriptionManager, hasPermission)
    }

    @SuppressLint("MissingPermission")
    fun getSimRssiValue(telephonyManager: TelephonyManager?,hasPermission: Boolean){
        if(hasPermission){
            val simInfo = telephonyManager?.allCellInfo as ArrayList
            if (simInfo.isNotEmpty()) {
                for (i in simInfo.indices) {
                    if(i == 0 && simInfo[0].isRegistered){
                        val result = getRssiValue(simInfo[0])
                        _state.value.sim1Rssi = result
//                        _state.value = (simState(sim1Rssi = result))
                    }else if(simInfo[1].isRegistered){
                        val result = getRssiValue(simInfo[1])
                        _state.value.sim2Rssi = result
//                        _state.value = (simState(sim2Rssi = result))
                    }
                }
            }

        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    fun getSimOperatorName(telephonySubscriptionManager: SubscriptionManager?, hasPermission: Boolean){
        if(hasPermission){
            val localList: List<*> = telephonySubscriptionManager?.activeSubscriptionInfoList ?: listOf<Any>()
            if(localList.isNotEmpty()){
                for(i in localList.indices){
                    if(i == 0){
//                        _state.value = (simState(sim1 = (localList[0] as SubscriptionInfo).carrierName.toString()))
                        _state.value.sim1 = (localList[0] as SubscriptionInfo).carrierName.toString()
                    }
                    else{
//                        _state.value = (simState(sim2 = (localList[1] as SubscriptionInfo).carrierName.toString()))
                        _state.value.sim2 = (localList[1] as SubscriptionInfo).carrierName.toString()
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

    private val speedTestSocket = SpeedTestSocket()

    fun test(){
        speedTestSocket.addSpeedTestListener(object : ISpeedTestListener {
            override fun onCompletion(report: SpeedTestReport) {
                // called when download/upload is complete
                Log.d("Completed Test", "[COMPLETED] rate in octet/s : " + report.transferRateOctet)
                Log.d("Completed test", "[COMPLETED] rate in bit/s   : " + report.transferRateBit)
            }

            override fun onError(speedTestError: SpeedTestError, errorMessage: String) {
                // called when a download/upload error occur
                Log.d("Download Error", errorMessage)
                Log.d("Download Error2", speedTestError.toString())
            }

            override fun onProgress(percent: Float, report: SpeedTestReport) {
                // called to notify download/upload progress
                Log.d("Progress", "[PROGRESS] progress : $percent%")
                Log.d("Progress", "[PROGRESS] rate in octet/s : " + report.transferRateOctet)
                Log.d("Progress", "[PROGRESS] rate in bit/s   : " + report.transferRateBit)
            }
        })

        CoroutineScope(Dispatchers.Default).launch {
            speedTestSocket.startDownload("http://ipv4.ikoula.testdebit.info/1M.iso");
        }
    }




}