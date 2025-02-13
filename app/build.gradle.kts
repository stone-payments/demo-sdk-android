plugins {
    id("com.android.application")
    alias(libs.plugins.compose.compiler)
    kotlin("multiplatform")
}

kotlin {
    androidTarget()
}

android {
    compileSdk = 34

    defaultConfig {
        applicationId = "br.com.stonesdk.sdkdemo"
        minSdk = 22
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }

    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("debug")
        }
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            matchingFallbacks += listOf("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

//    kotlinOptions {
//        jvmTarget = "17"
//    }

    packagingOptions {
        exclude("META-INF/api_release.kotlin_module")
        exclude("META-INF/client_release.kotlin_module")
    }

    namespace = "br.com.stonesdk.sdkdemo"
}

dependencies {
    implementation(platform(libs.compose.bom))

    implementation(libs.lifecycle.runtime)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.viewmodel.compose)

    implementation(libs.material)
    implementation(libs.compose.material3)
    implementation(libs.compose.foundation)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)

    implementation(libs.appcompat)
    implementation(libs.activity)
    implementation(libs.activity.compose)
    implementation(libs.constraintlayout)
    implementation(libs.fragment)

    implementation(libs.permissiondispatcher)
    annotationProcessor(libs.permissiondispatcher.processor)
    implementation(libs.accompanist.permissions)

    implementation(libs.koinCore)
    implementation(libs.koinAndroid)
    implementation(libs.koinCompose)

    coreLibraryDesugaring(libs.tools.desugar)

    implementation(libs.posmobile.datacontainer.data)
    implementation(libs.posmobile.sdk.core.android)
    implementation(libs.posmobile.sdk.debugmode)

    debugImplementation(libs.compose.ui.tooling)

    androidTestImplementation(platform(libs.compose.bom))
}