// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:7.0.2")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
        classpath (Dependencies.hiltGradlePlugin)
        classpath (Dependencies.safeArgs)
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }

}

allprojects{
    repositories{
        google()
        mavenCentral()
    }
}


tasks.register("clean", Delete::class) {
    delete (rootProject.buildDir)
}