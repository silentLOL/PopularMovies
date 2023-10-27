package at.stefanirndorfer.core.data.util

import kotlinx.coroutines.flow.Flow

/**
 * Util to report network status
 */
interface NetworkMonitor {
    val isAvailable: Flow<NetworkStatus>
}