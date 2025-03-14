@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.appuser"
    compileSdk = 34
    packagingOptions {
        resources {
            excludes += setOf("META-INF/DEPENDENCIES")
        }
    }

    defaultConfig {
        applicationId = "com.example.appuser"
        minSdk = 24
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
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(fileTree(mapOf(
        "dir" to "C:\\HocTap\\zalopay",
        "include" to listOf("*.aar", "*.jar"),
        "exclude" to listOf("")
    )))
    implementation(files("C:/HocTap/zalopay/zpdk-release-v3.1.aar"))
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.androidx.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //Glide
    implementation(libs.glide)
    annotationProcessor(libs.compiler)
    implementation(libs.androidsvg)

    //ImageSlider
    implementation(libs.imageslideshow)
    implementation(libs.glide)

    //RxJava
    implementation (libs.rxandroid)
    implementation(libs.rxjava)

    // Retrofit
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation (libs.rxjava3.retrofit.adapter)

    //paper
    implementation(libs.paperdb)

    //gson
    implementation(libs.gson)

    //eventBus
    implementation(libs.eventbus)

    //viewpager2
    implementation (libs.androidx.viewpager2)

    //Motion Toast
    implementation(libs.motiontoast)

    //Lottie
    implementation (libs.lottie)

    //firebase
    implementation(libs.firebase.auth)

    //firebase cloud messagging
    implementation(libs.firebase.messaging.v2410)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.firestore)
    implementation (libs.play.services.auth)

    //OAuth 2.0
    implementation(libs.google.auth.library.oauth2.http)

    //OkHttp
    implementation(libs.okhttp)

    //commons-codec
    implementation(libs.commons.codec)

    // momo
    implementation(libs.mobile.sdk)

    implementation (libs.material.v1100)
}