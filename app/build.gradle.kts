plugins {
    id("com.android.application")
}

android {
    namespace = "br.com.ikaro.atividadeavaliativa"
    compileSdk = 34

    defaultConfig {
        applicationId = "br.com.ikaro.atividadeavaliativa"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/license.txt"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/NOTICE.txt"
            excludes += "META-INF/notice.txt"
            excludes += "META-INF/ASL2.0"
        }
    }
}

repositories {
    google()
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://maven.google.com") }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // AndroidX + Material
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.0")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("androidx.core:core:1.12.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // Retrofit para chamadas de API
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Gson
    implementation("com.google.code.gson:gson:2.10.1")

    // OpenStreetMap
    implementation("org.osmdroid:osmdroid-android:6.1.13")
    implementation("org.osmdroid:osmdroid-wms:6.1.13")
    implementation("org.osmdroid:osmdroid-mapsforge:6.1.13")
    implementation("org.osmdroid:osmdroid-geopackage:6.1.13")
    implementation("org.osmdroid:osmdroid-third-party:6.1.13")

    // Coroutines para operações assíncronas
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // ViewModel e LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata:2.7.0")

    // Glide para carregamento de imagens
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Gráficos
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.jjoe64:graphview:4.2.2")

    // Testes
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
