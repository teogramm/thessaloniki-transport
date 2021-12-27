import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdkVersion(30)
    buildToolsVersion("30.0.3")

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    defaultConfig {
        applicationId = "xyz.teogramm.thessalonikitransit"
        minSdkVersion(26)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "0.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }

        create("release-gmaps") {
            initWith(getByName("release"))
        }

        create("release-oss") {
            initWith(getByName("release"))
        }
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    /*----------------------------------------
                Default dependencies
     ----------------------------------------*/
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(kotlin("stdlib", KotlinCompilerVersion.VERSION))
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")

    implementation("androidx.fragment:fragment-ktx:1.4.0")


    /*----------------------------------------
                  Room database
     ----------------------------------------*/
    val roomVersion = "2.4.0"
    implementation("androidx.room:room-runtime:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    // optional - Kotlin Extensions and Coroutines support for Room
//    implementation("androidx.room:room-ktx:$roomVersion")
    // optional - Test helpers
//    testImplementation("androidx.room:room-testing:$roomVersion")

    implementation("xyz.teogramm:oasth:0.9.0")
    /*----------------------------------------
              Hilt Dependency Injection
     ----------------------------------------*/
    val hiltVersion = "2.40.5"
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    kapt("com.google.dagger:hilt-compiler:$hiltVersion")

    /*----------------------------------------
            Material Design
    ----------------------------------------*/
    api("com.google.android.material:material:1.4.0")

    /*----------------------------------------
                   Android KTX
    ----------------------------------------*/
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.4.0")

    /*----------------------------------------
             Navigation Components
    ----------------------------------------*/
    val navVersion = "2.3.5"
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")

}
