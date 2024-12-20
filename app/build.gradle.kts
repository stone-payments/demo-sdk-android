plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "br.com.stonesdk.sdkdemo"
    compileSdk = 34

    defaultConfig {
        applicationId = "br.com.stonesdk.sdkdemo"
        minSdk = 22
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }

}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))


    // local
    // implementation(project(":error"))
    // implementation(project(":auth:auth"))
    // implementation(project(":auth:auth"))


    // android core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)


    api(libs.lib.commons)
    implementation("co.touchlab:stately-common:2.1.0")
    implementation(libs.kotlin.logging)
    implementation(libs.hal.provider)
    implementation(libs.activation)
    implementation(libs.datacontainer.api)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.koinKotlin)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)

    // koin
    implementation("io.insert-koin:koin-core:3.1.6")
    implementation("io.insert-koin:koin-android:3.1.6")
    implementation("io.insert-koin:koin-androidx-compose:3.1.6")

    // logs
    implementation("org.slf4j:slf4j-api:2.0.7")
    implementation("com.github.tony19:logback-android:3.0.0")

    // libs
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.3")
    implementation ("com.google.accompanist:accompanist-permissions:0.35.0-alpha")
    implementation("io.insert-koin:koin-core:4.0.0")
    implementation("io.insert-koin:koin-android:4.0.0")
    implementation("io.insert-koin:koin-androidx-compose:4.0.0")
    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("com.squareup.okio:okio:3.3.0")
    implementation("io.github.aakira:napier:2.6.1")

    // tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}