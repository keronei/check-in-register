object Dependencies {
    const val kotlinCore = "androidx.core:core-ktx:${Versions.androidxCoreKtx}"

    const val androidxAppCompat = "androidx.appcompat:appcompat:${Versions.androidxAppCompat}"

    const val androidMaterial = "com.google.android.material:material:${Versions.androidMaterial}"

    const val constrainLayout = "androidx.constraintlayout:constraintlayout:${Versions.constrainLayout}"

    const val navigationFragment = "androidx.navigation:navigation-fragment-ktx:${Versions.navigationFragment}"

    const val navigationUiKtx = "androidx.navigation:navigation-ui-ktx:${Versions.navigationFragment}"

    const val safeArgs = "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.navigationFragment}"

    const val androidExtensionsRuntime = "org.jetbrains.kotlin:kotlin-android-extensions-runtime:${Versions.androidExtensions}"

    //DI
    const val hiltGradlePlugin =  "com.google.dagger:hilt-android-gradle-plugin:${Versions.hilt_version}"

    const val daggerHilt = "com.google.dagger:hilt-android:${Versions.hilt_version}"

    const val hiltCompiler = "com.google.dagger:hilt-android-compiler:${Versions.hilt_version}"

    //Network Dependencies

    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"

    const val okHttpBoM = "com.squareup.okhttp3:okhttp-bom:${Versions.okhttp3}"

    const val okHttp3 = "com.squareup.okhttp3:okhttp"

    const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor"

    //Converters
    const val retrofitToGson = "com.squareup.retrofit2:converter-gson:${Versions.gson}"

    //Testing
    const val junit = "junit:junit:${Versions.junit}"
    const val core_testing = "androidx.arch.core:core-testing:${Versions.lifecycle}"
    const val mockito_kotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.mockito_kotlin}"
    const val mockito_android = "org.mockito:mockito-android:${Versions.mockito_android}"
    const val test_runner = "androidx.test:runner:${Versions.test_runner}"
    const val test_ext_junit = "androidx.test.ext:junit:${Versions.test_ext_junit}"
    const val espresso_core = "androidx.test.espresso:espresso-core:${Versions.espresso_core}"
    const val arch_core_testing = "android.arch.core:core-testing:${Versions.arch_core_testing}"
    const val assertj = "org.assertj:assertj-core:${Versions.assertj}"

    //Kotlin specifics
    //std lib
    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"

    //coroutines
    const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutinesCore}"

    //Helpers
    const val searchableDropDown = "com.github.chivorns:smartmaterialspinner:${Versions.searchable}"

    //Room
    const val room = "androidx.room:room-ktx:${Versions.room_version}"
    const val room_compiler = "androidx.room:room-compiler:${Versions.room_version}"
    const val room_testing = "androidx.room:room-testing:${Versions.room_version}"

    //Circle Image
    const val circle_image = "de.hdodenhof:circleimageview:${Versions.circular_image}"

    //Sweet Alerts
    const val sweetalerts = "com.github.f0ris.sweetalert:library:${Versions.sweetalerts}"


    const val dataStore = "androidx.datastore:datastore-preferences:${Versions.datastore}"
}