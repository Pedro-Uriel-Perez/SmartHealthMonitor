package mx.edu.utng.smarthealthmonitor.data.mqtt

import mx.edu.utng.smarthealthmonitor.data.BuildConfig

/**
 * Configuración del broker HiveMQ Cloud. Las credenciales NO están aquí:
 * vienen de local.properties (gitignored) vía BuildConfig — ver data/build.gradle.kts.
 */
object MqttConfig {
    val BROKER_URL: String get() = BuildConfig.MQTT_BROKER_URL
    val USERNAME: String get() = BuildConfig.MQTT_USERNAME
    val PASSWORD: String get() = BuildConfig.MQTT_PASSWORD

    // Topics del proyecto (convención UTNG)
    const val TOPIC_FC = "utng/smarthealthmonitor/fc"
    const val TOPIC_TV = "utng/smarthealthmonitor/tv"
    const val TOPIC_ALERT = "utng/smarthealthmonitor/alerta"

    // QoS: 0=best effort, 1=at least once, 2=exactly once
    const val QOS = 1

    // Client IDs únicos por dispositivo
    const val CLIENT_WEAR = "smarthealthmonitor-wear"
    const val CLIENT_APP = "smarthealthmonitor-app"
    const val CLIENT_TV = "smarthealthmonitor-tv"

    val isConfigured: Boolean get() = BROKER_URL.isNotBlank()
}
