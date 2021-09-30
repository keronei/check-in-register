plugins {
    id ("java-library")
    id ("kotlin")
}


dependencies {
    implementation(Dependencies.kotlinStdLib)
    implementation(Dependencies.coroutinesCore)
}
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}