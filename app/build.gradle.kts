plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "br.com.ikaro.atividadeavaliativa"
    compileSdk = 33

    defaultConfig {
        applicationId = "br.com.ikaro.atividadeavaliativa"
        minSdk = 21
        targetSdk = 33
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

    configurations.all {
        resolutionStrategy {
            force("androidx.core:core:1.9.0")
            force("androidx.appcompat:appcompat:1.6.1")

            exclude("com.android.support", "support-compat")
            exclude("com.android.support", "support-v4")
            exclude("com.android.support", "support-annotations")
            exclude("com.android.support", "support-fragment")
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // AndroidX + Material
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.0")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("androidx.core:core:1.9.0")

    // Gráficos
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.jjoe64:graphview:4.2.2")

    // OpenStreetMap
    implementation("org.osmdroid:osmdroid-android:6.1.16")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // Testes
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
