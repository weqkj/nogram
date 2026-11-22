plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "app.nogram"
    compileSdk = 35

    defaultConfig {
        applicationId = "app.nogram"
        minSdk = 26            // Material You динамические цвета: Android 12+, но на 8+ работает fallback
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"

        // Берутся с https://my.telegram.org -> API development tools
        buildConfigField("int", "TG_API_ID", (project.findProperty("TG_API_ID") ?: "0").toString())
        buildConfigField("String", "TG_API_HASH", "\"${project.findProperty("TG_API_HASH") ?: ""}\"")
        buildConfigField("String", "BACKEND_URL", "\"${project.findProperty("BACKEND_URL") ?: "https://nogram.example.com"}\"")
        buildConfigField("long", "OWNER_ID", "7876031245L")
    }

    buildFeatures { compose = true; buildConfig = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.12.01")
    implementation(composeBom)
    implementation("androidx.compose.material3:material3:1.4.0-alpha04") // Expressive API
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.navigation:navigation-compose:2.8.5")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    // JS-движок для плагинов (песочница)
    implementation("app.cash.quickjs:quickjs-android:0.9.2")
    // Клиент Telegram. Подключается как готовая сборка TDLib (см. README).
    // implementation("org.drinkless:tdlib:1.8.x")
}
