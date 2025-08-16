package com.michaelbentz.stacksearch.app.di.module

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class NetworkMonitor @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
) {
    private val NetworkCapabilities.hasValidatedInternet: Boolean
        get() = hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

    val isOnlineFlow = callbackFlow {
        val connectivityManager = appContext
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true).isSuccess
            }

            override fun onLost(network: Network) {
                trySend(false).isSuccess
            }

            override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
                trySend(caps.hasValidatedInternet).isSuccess
            }
        }

        connectivityManager.registerDefaultNetworkCallback(networkCallback)
        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }
        .onStart {
            emit(currentStatus())
        }
        .distinctUntilChanged()

    private fun currentStatus(): Boolean {
        val connectivityManager = appContext
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager
            .getNetworkCapabilities(activeNetwork) ?: return false

        return networkCapabilities.hasValidatedInternet
    }
}
