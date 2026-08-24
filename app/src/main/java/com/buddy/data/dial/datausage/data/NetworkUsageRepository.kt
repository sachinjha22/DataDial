package com.buddy.data.dial.datausage.data

import android.app.AppOpsManager
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.net.ConnectivityManager
import android.os.Process
import android.os.RemoteException
import com.buddy.data.dial.datausage.model.NetworkUsage
import com.buddy.data.dial.datausage.model.UsageResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Reads on-device network statistics via [NetworkStatsManager]. Everything here is local:
 * no network call is ever made and the app declares no INTERNET permission.
 */
class NetworkUsageRepository(context: Context) {

    private val appContext = context.applicationContext

    /** PACKAGE_USAGE_STATS is a special-access permission the user grants from Settings. */
    fun hasUsageAccess(): Boolean {
        val appOps = appContext.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            appContext.packageName,
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    suspend fun queryUsage(startMillis: Long, endMillis: Long): Result<UsageResult> =
        withContext(Dispatchers.IO) {
            if (!hasUsageAccess()) {
                return@withContext Result.failure(
                    SecurityException("Usage access permission is not granted")
                )
            }
            try {
                val statsManager =
                    appContext.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
                // subscriberId = null matches all mobile subscribers without needing
                // READ_PHONE_STATE; Wi-Fi has no subscriber concept so "" is used.
                val mobile = querySummary(statsManager, ConnectivityManager.TYPE_MOBILE, null, startMillis, endMillis)
                val wifi = querySummary(statsManager, ConnectivityManager.TYPE_WIFI, "", startMillis, endMillis)
                Result.success(UsageResult(mobile = mobile, wifi = wifi))
            } catch (e: SecurityException) {
                Result.failure(e)
            } catch (e: RemoteException) {
                Result.failure(e)
            }
        }

    private fun querySummary(
        manager: NetworkStatsManager,
        networkType: Int,
        subscriberId: String?,
        startMillis: Long,
        endMillis: Long,
    ): NetworkUsage = try {
        val bucket = manager.querySummaryForDevice(networkType, subscriberId, startMillis, endMillis)
        NetworkUsage(rxBytes = bucket.rxBytes, txBytes = bucket.txBytes)
    } catch (e: RemoteException) {
        NetworkUsage.ZERO
    } catch (e: IllegalStateException) {
        // Thrown on devices with no data recorded for this transport in the window.
        NetworkUsage.ZERO
    }
}
