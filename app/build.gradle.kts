import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.spotless)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.ft.architectcoders"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ft.architectcoders"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }

        val properties = Properties()
        val localPropertiesFile = project.rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            properties.load(localPropertiesFile.readText().byteInputStream())
        }

        // TMDB API KEY
        val tmdbApiKey = properties.getProperty("TMDB_API_KEY")
            ?: System.getenv("TMDB_API_KEY")
            ?: ""
        buildConfigField("String", "TMDB_API_KEY", "\"$tmdbApiKey\"")

        // GEMINI API KEY
        val geminiApiKey = properties.getProperty("GEMINI_API_KEY")
            ?: System.getenv("GEMINI_API_KEY")
            ?: ""
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // Project implementation
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":framework"))
    implementation(project(":usecases"))
    implementation(project(":test-shared"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Geolocalización
    implementation(libs.play.services.location)

    // Kotlinx Serialization
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.navigation.compose)

    // Koin
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.core)

    // Coil
    implementation(libs.coil.compose)

    // Material Icons Extended
    implementation(libs.androidx.material.icons.extended)

    // Haze - Glassmorphism effect
    implementation(libs.haze)

    // Lottie
    implementation(libs.lottie.compose)

    // Room
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.turbine)
    testImplementation(project(":test-shared"))
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
