plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "ru.mirea.ilmetoviv.mireaproject"
    compileSdk = 34  // ← стабильная версия, или 35 если есть

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "ru.mirea.ilmetoviv.mireaproject"
        minSdk = 26
        targetSdk = 34  // ← согласуем с compileSdk
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Core и Activity — версии, совместимые с API 34
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.activity:activity-ktx:1.8.2")

    // WorkManager — версия для API 34
    implementation("androidx.work:work-runtime:2.9.0")

    // Остальные из catalog (или явные версии)
    implementation(libs.appcompat) // проверьте, что не тянет новые core
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    // Тесты
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}