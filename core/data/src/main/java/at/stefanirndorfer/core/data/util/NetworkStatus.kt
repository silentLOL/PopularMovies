package at.stefanirndorfer.core.data.util

/**
 * state class to determine if the device is online
 */
sealed class NetworkStatus {
    object Available : NetworkStatus()
    object Unavailable : NetworkStatus()
}
