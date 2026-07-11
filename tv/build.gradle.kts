plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "mx.edu.utng.smarthealthmonitor.tv"
    compileSdk = 36

    defaultConfig {
        applicationId = "mx.edu.utng.smarthealthmonitor.tv"
        minSdk = 26
        targetSdk = 36
        versionCode = 2
        versionName = "2.1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // Compartir Room + Repository con módulo :data (no con :app, que es otro módulo application)
    implementation(project(":data"))

    // Compose base
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.activity.compose)

    // Compose for TV — Surface/ClickableSurfaceDefaults con foco D-pad, MaterialTheme
    implementation("androidx.tv:tv-material:1.1.0")

    // Navigation Compose — catalog/detail/playback
    implementation("androidx.navigation:navigation-compose:2.9.8")

    // Media3 / ExoPlayer para TvPlaybackScreen
    implementation("androidx.media3:media3-exoplayer:1.10.0")
    implementation("androidx.media3:media3-ui:1.10.0")

    // ViewModel + Coroutines
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.10.0")

    implementation(libs.androidx.core.ktx)
}
