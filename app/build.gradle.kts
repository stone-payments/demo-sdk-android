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

    packagingOptions {
        resources.excludes.add("META-INF/*")
    }

}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))


    // local
    // implementation(project(":error"))
    // implementation(project(":auth:auth"))
    // implementation(project(":auth:auth"))

    // pos android
    api(libs.posandroid.lib.commons)
    implementation(libs.posandroid.datacontainer.api)
    implementation(libs.posandroid.activation)
    implementation(libs.posandroid.hal.provider)
    implementation(libs.posandroid.sdk.core.android)
    implementation(libs.posandroid.sdk.error)
    implementation(libs.posandroid.sdk.auth)
    implementation(libs.posandroid.sdk.auth.gateway)
    implementation(libs.posandroid.sdk.invoice)
    implementation(libs.posandroid.sdk.owl)

    // android core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    implementation("co.touchlab:stately-common:2.1.0")
    implementation(libs.kotlin.logging)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)

    // koin
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)

    // logs
    implementation(libs.slf4j.api)
    implementation(libs.logback.android)

    // libs
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.3")
    implementation ("com.google.accompanist:accompanist-permissions:0.35.0-alpha")
    implementation("com.squareup.okio:okio:3.3.0")
    implementation("io.github.aakira:napier:2.6.1")

    // tests
    testImplementation(libs.androidx.junit)
    testImplementation(libs.junitJupiterApi)
    testImplementation(libs.junitJupiterEngine)
    testImplementation(libs.androidx.ui.test.junit4)
    testImplementation(libs.mockk.android)
    testImplementation(libs.mockk.common)

    // android tests
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.mockk.common)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

configurations.implementation {
    exclude(group = "br.com.stone.posandroid", module = "datacontainer-api")
    exclude(group = "co.stone.posmobile.sdk", module = "auth")
    exclude(group = "co.stone.posmobile.sdk", module = "auth-gateway")
    exclude(group = "co.stone.posmobile.sdk", module = "invoice")
    exclude(group = "co.stone.posmobile.sdk", module = "owl")
}