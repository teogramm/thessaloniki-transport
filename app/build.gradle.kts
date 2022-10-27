import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {

    defaultConfig {
        compileSdk= 33
        buildToolsVersion = "30.0.3"
        applicationId = "xyz.teogramm.thessalonikitransit"
        minSdk = 28
        targetSdk = 33
        versionCode = 1
        versionName = "0.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
        }

        create("gmaps") {
            initWith(getByName("debug"))
        }

        create("oss") {
            initWith(getByName("debug"))
        }
    }

    buildFeatures {
        viewBinding = true
    }
    namespace = "xyz.teogramm.thessalonikitransit"
}

dependencies {
    /*----------------------------------------
                Default dependencies
     ----------------------------------------*/
    // Add jar files from libs directory
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(kotlin("stdlib", KotlinCompilerVersion.VERSION))
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation("androidx.fragment:fragment-ktx:1.5.2")

    /*----------------------------------------
                  Room database
     ----------------------------------------*/
    val roomVersion = "2.4.2"
    implementation("androidx.room:room-runtime:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$roomVersion")
    // optional - Test helpers
//    testImplementation("androidx.room:room-testing:$roomVersion")

    implementation("xyz.teogramm:oasth:0.9.0")
    /*----------------------------------------
              Hilt Dependency Injection
     ----------------------------------------*/
    val hiltVersion = "2.43.2"
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    kapt("com.google.dagger:hilt-compiler:$hiltVersion")

    /*----------------------------------------
            Material Design
    ----------------------------------------*/
    api("com.google.android.material:material:1.6.1")

    /*----------------------------------------
                   Android KTX
    ----------------------------------------*/
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")

    /*----------------------------------------
             Navigation Components
    ----------------------------------------*/
    val navVersion = "2.5.2"
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")

    /*----------------------------------------
             Google Maps Build Dependencies
    ----------------------------------------*/
    implementation("com.google.android.gms:play-services-maps:18.1.0")

    implementation("androidx.recyclerview:recyclerview:1.2.1")
}
