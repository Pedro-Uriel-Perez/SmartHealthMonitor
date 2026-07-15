import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

// Credenciales de HiveMQ Cloud — NUNCA hardcodeadas en el código.
// Se leen de local.properties (gitignored) y se exponen vía BuildConfig.
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}
fun mqttProp(key: String): String = localProperties.getProperty(key, "")

android {
    namespace = "mx.edu.utng.smarthealthmonitor.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        buildConfigField("String", "MQTT_BROKER_URL", "\"${mqttProp("MQTT_BROKER_URL")}\"")
        buildConfigField("String", "MQTT_USERNAME", "\"${mqttProp("MQTT_USERNAME")}\"")
        buildConfigField("String", "MQTT_PASSWORD", "\"${mqttProp("MQTT_PASSWORD")}\"")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // MQTT (HiveMQ Cloud) + serialización JSON — compartido por app/tv/wear
    implementation(libs.paho.mqttv3)
    implementation(libs.kotlinx.serialization.json)
}
