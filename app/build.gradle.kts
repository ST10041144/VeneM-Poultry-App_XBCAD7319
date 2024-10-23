plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)

    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")



}

android {
    namespace = "com.example.venempoultry"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.venempoultry"
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
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.database.ktx)
    //implementation(libs.androidx.biometric.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //superbase
    //implementation("io.github.jan-tennert.supabase:supabase-kt:1.5.4")


    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    //OKHttp
    implementation (libs.okhttp)
    implementation (libs.logging.interceptor)

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.2.3"))


    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics-ktx")

    // Firebase Authentication
    implementation ("com.google.firebase:firebase-auth:23.0.0")

    //bcrypt
    implementation(libs.jbcrypt)

    // Add the dependencies for any other desired Firebase products
    // https://firebase.google.com/docs/android/setup#available-libraries

    //database declaration
    implementation (libs.firebase.database)

    implementation (libs.androidx.constraintlayout)



}