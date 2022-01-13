package com.arun.speedtester

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun getTelephoneMangerService(
        @ApplicationContext context: Context
    ): TelephonyManager?{
        return context.applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
    }

    @Singleton
    @Provides
    fun getConnectivityManagerService(
        @ApplicationContext context: Context
    ): ConnectivityManager?{
        return context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    @Singleton
    @Provides
    fun getTelephonySubscriptionManager(
        @ApplicationContext context: Context
    ): SubscriptionManager?{
        return context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager?
    }

}