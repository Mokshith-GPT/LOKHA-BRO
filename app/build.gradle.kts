plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.lokhabro.android"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.lokhabro.android"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            isMinifyEnabled = false
            // Release signing is configured from private CI environment variables
            // only when all required signing properties are available.
            val ksPath = System.getenv("LOKHA_KEYSTORE_PATH")
            val ksPassword = System.getenv("LOKHA_KEYSTORE_PASSWORD")
            val keyAliasValue = System.getenv("LOKHA_KEY_ALIAS")
            val keyPasswordValue = System.getenv("LOKHA_KEY_PASSWORD")
            if (!ksPath.isNullOrBlank() && !ksPassword.isNullOrBlank() &&
                !keyAliasValue.isNullOrBlank() && !keyPasswordValue.isNullOrBlank()) {
                signingConfig = signingConfigs.create("lokhaRelease") {
                    storeFile = file(ksPath)
                    storePassword = ksPassword
                    keyAlias = keyAliasValue
                    keyPassword = keyPasswordValue
                }
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.12.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation("androidx.activity:activity-compose:1.10.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    debugImplementation("androidx.compose.ui:ui-tooling")
    testImplementation("junit:junit:4.13.2")
}
