plugins {
    id("org.jetbrains.kotlin.android")
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.bodysculptor2"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.bodysculptor2"
        minSdk = 26
        targetSdk = 35
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

    dependencies {
        implementation("androidx.appcompat:appcompat:1.7.0")
        implementation("com.google.android.material:material:1.12.0")
        implementation("androidx.constraintlayout:constraintlayout:2.2.0")
        implementation(libs.androidx.activity)
        implementation("androidx.fragment:fragment-ktx:1.6.1")
        implementation("androidx.core:core-ktx:1.15.0")

        testImplementation("junit:junit:4.13.2")
        androidTestImplementation("androidx.test.ext:junit:1.1.5")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

        // Firebase BoM
        implementation("com.google.firebase:firebase-messaging:24.0.3")
        implementation("com.google.firebase:firebase-database:21.0.0")
        implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
        implementation("com.google.firebase:firebase-auth-ktx:21.0.5")
        implementation("com.google.firebase:firebase-firestore-ktx:24.8.1")

        //calendar
        implementation("com.applandeo:material-calendar-view:1.9.2")

        implementation ("androidx.compose.ui:ui:1.5.0")
        implementation ("androidx.compose.material:material:1.5.0")
        implementation ("androidx.compose.ui:ui-tooling-preview:1.5.0")
        implementation ("androidx.activity:activity-compose:1.7.2")
        implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

        debugImplementation ("androidx.compose.ui:ui-tooling:1.5.0")
        debugImplementation ("androidx.compose.ui:ui-test-manifest:1.5.0")
        androidTestImplementation ("androidx.compose.ui:ui-test-junit4:1.5.0")

    }