import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

val configFile = rootProject.file("config.properties")
val configProperties = Properties()
if (configFile.exists()) {
    configProperties.load(FileInputStream(configFile))
}

android {
    namespace = "com.example.mazady"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mazady"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "BASE_URL", configProperties["BASE_URL"] as String)
        buildConfigField("String", "API_KEY", configProperties["API_KEY"] as String)
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    implementation(libs.koin)
    implementation(libs.koinCore)
    implementation(libs.retrofit)
    implementation(libs.moshiConverterFactory)
    implementation(libs.moshKotlin)
    implementation(libs.loggingInterceptor)
    implementation (libs.glide)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}