plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = AppConfig.compileSdk
    buildToolsVersion = AppConfig.buildToolsVersion

    defaultConfig {
        applicationId = "com.keronei.koregister"
        minSdk = AppConfig.minSdk
        targetSdk = AppConfig.targetSdk
        versionCode = AppConfig.versionCode
        versionName = AppConfig.versionName

        testInstrumentationRunner = AppConfig.androidTestInstrumentation
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        getByName("debug") {
            isMinifyEnabled = false
        }
    }
    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    viewBinding {
        android.buildFeatures.viewBinding = true
    }

    dataBinding {
        android.buildFeatures.dataBinding = true
    }
}

dependencies {

    //Local Modules
    implementation(project(LocalModules.dataModule))
    implementation(project(LocalModules.domainModule))

    //Dagger Hilt
    implementation(Dependencies.daggerHilt)
    kapt(Dependencies.hiltCompiler)

    implementation(Dependencies.kotlinCore)
    implementation(Dependencies.androidxAppCompat)
    implementation (Dependencies.androidMaterial)
    implementation (Dependencies.constrainLayout)
    implementation (Dependencies.navigationFragment)
    implementation (Dependencies.navigationUiKtx)

    testImplementation("junit:junit:4.0.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.4.0")
}