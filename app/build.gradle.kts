import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    val xcf = XCFramework("DemoApp")
//    listOf(
//        iosX64(),
//        iosArm64(),
//        iosSimulatorArm64(),
//    ).forEach { iosTarget ->
//        iosTarget.binaries.framework {
//            baseName = "DemoApp"
//            isStatic = true
//            xcf.add(this)
//        }
//    }

    sourceSets {

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.material3.android)
            implementation(libs.koin.android)
            implementation(libs.koin.android.compose)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.koin.core)
            implementation(libs.koin.compose.viewmodel)
            // implementation(libs.androidx.lifecycle.viewmodel)
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:2.8.2")

            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation("org.jetbrains.androidx.navigation:navigation-compose:2.8.0-alpha12")
            //implementation(libs.posmobile.sdk.core)
            //implementation(libs.posmobile.sdk.manufacturer.serial)
            implementation("co.stone.pos.mobile.sdk:sdk-core:6.0.3-dev")
            implementation("co.stone.pos.mobile.sdk.manufacturer:manufacturer-serial:6.0.3-dev")
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
            implementation(libs.platform.tools)
            implementation(libs.kotlin.logging)
        }

        iosMain.dependencies {
        }
    }
}

android {
    namespace = "org.example.project"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.example.project"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/LICENSE.txt"
            excludes += "/META-INF/LICENSE.md"
            excludes += "/META-INF/LICENSE-notice.md"
            excludes += "/META-INF/LICENSE"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
}

configurations.all {
    resolutionStrategy {
        exclude("co.stone.posmobile", "lib-commons-android")
        exclude("br.com.stone.posandroid", "hal-api")
    }
}

dependencies {
    implementation(libs.androidx.lifecycle.viewmodel.android)
    debugImplementation(compose.uiTooling)
    coreLibraryDesugaring(libs.tools.desugar)
}

// //apply from: '../gertec/gertec-signing-config.gradle'
// //apply from: '../positivo/positivo-signing-config.gradle'
//
// android {
//
// //    flavorDimensions = ["manufacturer"]
// //    productFlavors {
// //        standard {
// //            dimension "manufacturer"
// //            signingConfig signingConfigs.debug
// //            isDefault true
// //        }
// //    }
//    compileSdk 34
//
//    defaultConfig {
//        applicationId "br.com.stonesdk.sdkdemo"
//        minSdkVersion 22
//        targetSdkVersion 34
//        versionCode 1
//        versionName "1.0"
//    }
//
//    buildFeatures {
//        compose true
//    }
//
//    testOptions {
//        unitTests.returnDefaultValues = true
//    }
//
//    buildTypes {
//
//        //Necessary for the functioning of Positivo's signature in debug mode.
//        debug {
//            signingConfig signingConfigs.debug
//            matchingFallbacks = ['debug']
//        }
//        release {
//            minifyEnabled false
//            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
//            matchingFallbacks = ['release']
//        }
//    }
//
//    compileOptions {
//        sourceCompatibility JavaVersion.VERSION_17
//        targetCompatibility JavaVersion.VERSION_17
//        coreLibraryDesugaringEnabled true
//    }
//
//
//    kotlinOptions {
//        jvmTarget = JavaVersion.VERSION_17
//    }
//
//    packagingOptions {
//        exclude 'META-INF/api_release.kotlin_module'
//        exclude 'META-INF/client_release.kotlin_module'
//    }
//
//
//    buildFeatures {
//        viewBinding true
//    }
//
//    namespace 'br.com.stonesdk.sdkdemo'
// }
//
// dependencies {
//    implementation platform(libs.compose.bom)
//
//    implementation(libs.lifecycle.runtime)
//    implementation(libs.lifecycle.runtime.compose)
//    implementation(libs.lifecycle.viewmodel.compose)
//
//    implementation (libs.material)
//    implementation(libs.compose.material3)
//    implementation(libs.compose.foundation)
//    implementation(libs.compose.ui)
//    implementation(libs.compose.ui.tooling.preview)
//
//    implementation(libs.appcompat)
//    implementation(libs.activity)
//    implementation(libs.activity.compose)
//    implementation(libs.activity.compose)
//    implementation(libs.constraintlayout)
//    implementation(libs.fragment)
//
//    implementation (libs.permissiondispatcher)
//    annotationProcessor (libs.permissiondispatcher.processor)
//    implementation (libs.accompanist.permissions)
//
//    implementation(libs.koinCore)
//    implementation(libs.koinAndroid)
//    implementation(libs.koinCompose)
//
//    coreLibraryDesugaring(libs.tools.desugar)
//
//    implementation(libs.posmobile.datacontainer.data)
//    implementation(libs.posmobile.sdk.core.android)
//    implementation(libs.posmobile.sdk.debugmode)
//
//    debugImplementation(libs.compose.ui.tooling)
//
//   androidTestImplementation platform(libs.compose.bom)
//
// //    implementation libs.pos.android.sdk.posandroid.ingenico
// //    implementation libs.pos.android.sdk.posandroid.positivo
// //    implementation libs.pos.android.sdk.posandroid.gertec
// //    implementation libs.pos.android.sdk.posandroid.sunmi
//
// //    testImplementation "io.mockk:mockk:1.13.12"
// //    testImplementation "org.junit.platform:junit-platform-launcher:1.10.0"
// //    testImplementation "org.junit.jupiter:junit-jupiter-engine:5.10.0"
// //    testImplementation "org.junit.vintage:junit-vintage-engine:5.10.0"
// //    testImplementation "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0-RC"
// }
