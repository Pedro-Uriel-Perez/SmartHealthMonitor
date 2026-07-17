package mx.edu.utng.smarthealthmonitor.sync

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import mx.edu.utng.smarthealthmonitor.data.models.SmartHealthRepository
import java.util.concurrent.TimeUnit

/** Ejecuta la sincronización con Neon en background, incluso con la app cerrada. */
class NeonSyncWorker(
    ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        return try {
            // 1. Enviar pendientes locales a Neon
            SmartHealthRepository.enviarPendientes()
            // 2. Descargar los más recientes de Neon
            SmartHealthRepository.sincronizarDesdeNeon(limite = 100)
            android.util.Log.d("SYNC_WORKER", "✅ Sync completado")
            Result.success()
        } catch (e: Exception) {
            android.util.Log.e("SYNC_WORKER", "❌ Sync fallido: ${e.message}")
            Result.retry() // WorkManager reintentará automáticamente
        }
    }

    companion object {
        const val WORK_NAME = "NeonSyncWork"

        /** Programa el sync periódico (cada 30 min, solo con red disponible). */
        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = PeriodicWorkRequestBuilder<NeonSyncWorker>(30, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 5, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}
