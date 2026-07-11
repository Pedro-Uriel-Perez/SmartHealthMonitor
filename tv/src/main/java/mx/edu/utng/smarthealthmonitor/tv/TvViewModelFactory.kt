package mx.edu.utng.smarthealthmonitor.tv

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mx.edu.utng.smarthealthmonitor.data.models.SmartHealthRepository

/**
 * TvApplication ya llama a SmartHealthRepository.init() al arrancar, pero
 * cada pantalla (catalog/detail) puede resolverse antes de que termine ese
 * onCreate, así que la Factory se asegura de que el Repository esté listo.
 */
class TvViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        SmartHealthRepository.init(context.applicationContext)
        @Suppress("UNCHECKED_CAST")
        return TvViewModel() as T
    }
}
