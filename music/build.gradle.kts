plugins {
    `mikbot-plugin`
    `mikbot-module`
    kotlin("plugin.serialization")
}

group = "dev.schlaubi.mikbot"
version = "1.0-SNAPSHOT"

dependencies {
    compileOnly("kord", "core") {
        version {
            branch = "feature/autocomplete-mikmusic"
        }
    }
    api("dev.schlaubi.lavakord", "kord", "3.0.1")

    // Plattform support
    implementation("com.google.apis", "google-api-services-youtube", "v3-rev205-1.25.0")
    api("se.michaelthelin.spotify", "spotify-web-api-java", "6.5.4")

    // SponsorBlock Client
    implementation("dev.nycode", "sponsorblock-kt", "1.0-SNAPSHOT")

    // Scheduling
    implementation("dev.inmo", "krontab", "0.6.5")

    // redeploy support
    compileOnly(project(":core:redeploy-hook"))

    // GDPR support
    compileOnly(project(":core:gdpr"))
}