// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false

    // Google services plugin (if you're using Firebase or Google Play services)
    id("com.google.gms.google-services") version "4.4.2" apply false

}


