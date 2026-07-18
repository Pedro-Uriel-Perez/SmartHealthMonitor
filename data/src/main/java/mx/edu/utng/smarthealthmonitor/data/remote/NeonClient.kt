package mx.edu.utng.smarthealthmonitor.data.remote

import kotlinx.serialization.json.Json
import mx.edu.utng.smarthealthmonitor.data.BuildConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.create
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

/** Cliente Retrofit hacia la Neon HTTP API. Singleton, igual que SmartHealthRepository. */
object NeonClient {

    private val json = Json { ignoreUnknownKeys = true }

    private val BASE_URL: String get() = "https://${BuildConfig.NEON_HOST}/"
    val CONN_STRING: String get() = BuildConfig.NEON_CONNECTION_STRING

    val isConfigured: Boolean get() = BuildConfig.NEON_HOST.isNotBlank() && BuildConfig.NEON_CONNECTION_STRING.isNotBlank()

    val api: NeonApiService by lazy {
        val okHttp = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttp)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create()
    }
}
