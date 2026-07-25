package com.cinemateca.connectivity.adapter

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.cinemateca.domain.connectivity.repository.InternetConnectionRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate

class AndroidInternetConnectionLocalImpl(
    private val connectivityManager: ConnectivityManager,
) : InternetConnectionRepository.Local {

    override fun observeAvailability(): Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(connectivityManager.hasValidatedInternetConnection())
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities,
            ) {
                trySend(networkCapabilities.hasValidatedInternetConnection())
            }

            override fun onLost(network: Network) {
                trySend(connectivityManager.hasValidatedInternetConnection())
            }

            override fun onUnavailable() {
                trySend(false)
            }
        }

        connectivityManager.registerDefaultNetworkCallback(callback)
        trySend(connectivityManager.hasValidatedInternetConnection())

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.conflate()
}

private fun ConnectivityManager.hasValidatedInternetConnection(): Boolean {
    val defaultNetwork = activeNetwork ?: return false
    val capabilities = getNetworkCapabilities(defaultNetwork) ?: return false
    return capabilities.hasValidatedInternetConnection()
}

private fun NetworkCapabilities.hasValidatedInternetConnection(): Boolean {
    return hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
        hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}
