import java.util.Properties
import kotlin.apply

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}

val localProperties = Properties().apply {
    val localPropertiesFile = file("config.properties")
    if (localPropertiesFile.exists()) {
        load(localPropertiesFile.inputStream())
    }
}

allprojects {
    extra.set("ftpServer", localProperties.getProperty("ftp.server"))
    extra.set("ftpPort", localProperties.getProperty("ftp.port"))
    extra.set("ftpUser", localProperties.getProperty("ftp.user"))
    extra.set("ftpPassword", localProperties.getProperty("ftp.password"))
}