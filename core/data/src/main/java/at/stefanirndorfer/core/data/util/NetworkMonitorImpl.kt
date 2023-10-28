package at.stefanirndorfer.core.data.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import javax.inject.Inject

class NetworkMonitorImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : NetworkMonitor {

    override val isAvailable = callbackFlow {

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkStatusCallback = object : ConnectivityManager.NetworkCallback() {
            private val networks = mutableSetOf<Network>()

            override fun onUnavailable() {
                if (networks.isEmpty()) {
                    trySend(NetworkStatus.Unavailable)
                }
            }

            override fun onAvailable(network: Network) {
                networks += network
                trySend(NetworkStatus.Available)
            }

            override fun onLost(network: Network) {
                networks -= network
                if (networks.isEmpty()) {
                    trySend(NetworkStatus.Unavailable)
                } else {
                    trySend(NetworkStatus.Available)
                }
            }
        }
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, networkStatusCallback)

        channel.trySend(connectivityManager.mapCurrentConnectionToNetworkStatus())

        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkStatusCallback)
        }
    }.conflate()

    private fun ConnectivityManager.mapCurrentConnectionToNetworkStatus(): NetworkStatus {
        if (activeNetwork?.let(::getNetworkCapabilities)
                ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        ) {
            return NetworkStatus.Available
        }
        return NetworkStatus.Unavailable
    }


}