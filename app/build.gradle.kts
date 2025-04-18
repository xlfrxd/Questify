plugins {
    alias(libs.plugins.android.application)

}

android {
    namespace = "com.example.questifyv1"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.questify.apk"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.recaptcha)
    val fragment_version = "1.8.1"
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.safetynet)

    implementation("com.android.volley:volley:1.2.1")
    implementation("com.google.android.gms:play-services-safetynet")
    implementation("dev.samstevens.totp:totp:1.7.1")  // TOTP library

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}