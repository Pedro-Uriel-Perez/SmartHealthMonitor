plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "mx.edu.utng.smarthealthmonitor.tv"
    compileSdk = 36

    defaultConfig {
        applicationId = "mx.edu.utng.smarthealthmonitor.tv"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "2.0.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Compartir Room + Repository con módulo :data (no con :app, que es otro módulo application)
    implementation(project(":data"))

    // Leanback Library — el estándar de Android TV
    implementation("androidx.leanback:leanback:1.2.0")
    // Glide para cargar imágenes en las cards
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // ViewModel + Coroutines
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")
    implementation(libs.androidx.lifecycle.runtime.ktx)
    // by viewModels() en Fragment
    implementation("androidx.fragment:fragment-ktx:1.8.5")

    // AppCompat (requerido por Theme.Leanback)
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation(libs.androidx.core.ktx)
}
