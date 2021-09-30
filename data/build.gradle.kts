plugins {
    id ("com.android.library")
    id("kotlin-android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk =AppConfig.compileSdk
    buildToolsVersion = AppConfig.buildToolsVersion

    defaultConfig {
        minSdk = AppConfig.minSdk
        targetSdk = AppConfig.targetSdk
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

}


dependencies {
    implementation(project(LocalModules.domainModule))

    //Dagger Hilt
    implementation(Dependencies.daggerHilt)
    kapt(Dependencies.hiltCompiler)

    //Room
    implementation(Dependencies.room)
    kapt(Dependencies.room_compiler)
    androidTestImplementation(Dependencies.room_testing)

    //Network
    //Retrofit
    api(Dependencies.retrofit)
    api(Dependencies.retrofitToGson)

    api(platform(Dependencies.okHttpBoM))
    api(Dependencies.okHttp3)
    api(Dependencies.loggingInterceptor)

    //Test
    testImplementation(Dependencies.junit)
    androidTestImplementation(Dependencies.test_ext_junit)
    testImplementation(Dependencies.core_testing)
    androidTestImplementation(Dependencies.arch_core_testing)
    androidTestImplementation(Dependencies.assertj)
    androidTestImplementation(Dependencies.test_runner)
    androidTestImplementation(Dependencies.mockito_kotlin)

}
