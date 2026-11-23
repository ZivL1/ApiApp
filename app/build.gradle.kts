import java.util.Properties

val localProperties = Properties()

val localPropertiesFile = rootProject.file("local.properties")

if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use {
        localProperties.load(it)
    }
}

val imdbApiKey = localProperties.getProperty("IMDB_API_KEY", "")
val aiApiKey = localProperties.getProperty("AI_API_KEY", "")

plugins {
    alias(libs.plugins.android.application)
}


android {
    namespace = "com.katza.apiapp"
    compileSdk {
        version = release(37)
    }

    buildFeatures {
        buildConfig = true
    }




    defaultConfig {
        applicationId = "com.katza.apiapp"
        minSdk = 29
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "IMDB_API_KEY",
            "\"$imdbApiKey\""
        )
        buildConfigField(
            "String",
            "AI_API_KEY",
            "\"$aiApiKey\""
        )

    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.12.1")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}