// Archivo raíz: solo se DECLARAN los plugins con versión y apply false.
// Cada plugin se APLICA (sin versión) en app/build.gradle.kts cuando se necesita.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.google.services) apply false
}
