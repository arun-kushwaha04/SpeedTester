package com.arun.speedtester.component

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.*
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.arun.speedtester.checkHasAllPermission
import com.arun.speedtester.requestPermission
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SpeedTesterViewModel @Inject constructor(
    @ActivityContext context: Context,
    telephonyManager: TelephonyManager?,
    telephonySubscriptionManager: SubscriptionManager?
) : ViewModel() {

    private val _state = mutableStateOf(simState())
    val state: State<simState> = _state

    private val hasPermission = checkHasAllPermission(context)
    private val getPermission = requestPermission(context)

    private val telephonyManager = telephonyManager
    val telephonySubscriptionManager = telephonySubscriptionManager

    fun hasPermission(context:Context){
        if(!hasPermission){
            requestPermission(context)
        }
        _state.value = simState(hasPermission = hasPermission)
    }


    @SuppressLint("MissingPermission")
    fun getSimRssiValue(){
        if(hasPermission){
            val simInfo = telephonyManager?.allCellInfo as ArrayList
            if (simInfo.isNotEmpty()) {
                for (i in simInfo.indices) {
                    if (simInfo[i].isRegistered) {
                        if(i == 0){
                            _state.value = (simState(sim1Rssi = getRssiValue(simInfo[i])))
                        }else{
                            _state.value = (simState(sim2Rssi = getRssiValue(simInfo[i])))
                        }
                    }
                }
            }

        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    fun getSimOperatorName(){
        if(hasPermission){
            val localList: List<*> = telephonySubscriptionManager?.activeSubscriptionInfoList ?: listOf<Any>()
            if(localList.isNotEmpty()){
                for(i in localList.indices){
                    if(i == 0){
                        _state.value = (simState(sim1 = (localList[i] as SubscriptionInfo).carrierName.toString()))
                    }
                    else{
                        _state.value = (simState(sim2 = (localList[i] as SubscriptionInfo).carrierName.toString()))
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


}