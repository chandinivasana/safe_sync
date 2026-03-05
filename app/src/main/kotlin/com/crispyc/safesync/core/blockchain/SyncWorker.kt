package com.crispyc.safesync.core.blockchain

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.crispyc.safesync.core.db.dao.SafetyEventDao
import javax.inject.Inject

class SyncWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val safetyEventDao: SafetyEventDao,
    private val blockchainManager: BlockchainManager
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val unsyncedEvents = safetyEventDao.getUnsyncedEvents()
        if (unsyncedEvents.isEmpty()) return Result.success()

        unsyncedEvents.forEach { event ->
            // Batch sync to Polygon
            val txHash = blockchainManager.signTransaction(event.eventHash)
            // If tx successful (mocked)
            safetyEventDao.markAsSynced(listOf(event.eventHash))
        }

        return Result.success()
    }
}
