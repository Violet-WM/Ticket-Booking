plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

val darajaConsumerKey = System.getenv("DARAJA_CONSUMER_KEY") ?: "y1gBdlmMm1eMnY0xWcG0tvOAA1ADq0xAd4u9bx2mzP0GsYzg"
val darajaConsumerSecret = System.getenv("DARAJA_CONSUMER_SECRET") ?: "7BReXdLAvzXlin221Ug9zZtSmyLpMXrtrNJtrorrPBy8SU5FbyGwWSmz4vNYCBaA"

android {
    namespace = "com.example.ticketcard"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ticketcard"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }

        // Applying consumer keys to all build types using forEach
        forEach { buildType ->
            buildType.buildConfigField("String", "CONSUMER_KEY", "\"$darajaConsumerKey\"")
            buildType.buildConfigField("String", "CONSUMER_SECRET", "\"$darajaConsumerSecret\"")
        }
    }

    buildFeatures {
        // Enable custom BuildConfig fields
        buildConfig = true
    }

    defaultConfig {
        buildConfigField("String", "CONSUMER_KEY", "\"y1gBdlmMm1eMnY0xWcG0tvOAA1ADq0xAd4u9bx2mzP0GsYzg\"")
        buildConfigField ("String", "CONSUMER_SECRET", "\"7BReXdLAvzXlin221Ug9zZtSmyLpMXrtrNJtrorrPBy8SU5FbyGwWSmz4vNYCBaA\"")
        multiDexEnabled = true
    }

    buildFeatures{
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-database:21.0.0")
    implementation("com.google.firebase:firebase-auth:23.0.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.squareup.picasso:picasso:2.8")
    implementation("androidx.activity:activity:1.8.0")
    implementation("com.google.firebase:firebase-firestore:25.0.0")
    implementation("com.google.firebase:firebase-storage:21.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.google.firebase:firebase-database:20.0.5")
    implementation("com.google.firebase:firebase-auth:21.0.1")
    implementation("com.google.firebase:firebase-firestore:24.0.2")
    implementation("com.github.dhaval2404:imagepicker:2.1")

    //m-pesa integration
    implementation("com.jakewharton.timber:timber:4.7.1")
    implementation("cn.pedant.sweetalert:library:1.3") {
        exclude (group = "com.android.support")
    }
    implementation("com.squareup.retrofit2:retrofit:2.5.0")
    implementation("com.squareup.retrofit2:converter-gson:2.5.0")

    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    implementation("com.google.code.gson:gson:2.8.5")

    implementation("com.squareup.okio:okio:2.1.0")
}