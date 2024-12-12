plugins {
    kotlin("jvm") version "2.1.0"
    id("org.jetbrains.kotlinx.benchmark") version "0.4.13"
}

sourceSets {
    main {
        kotlin.srcDir("src")
    }
}

tasks {
    wrapper {
        gradleVersion = "8.11.1"
    }
}


dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.13")
}

repositories {
    mavenCentral()
}

benchmark {
    targets {
        register("main")
    }
}
//benchmark {
//    targets {
//        register("jvm")
//    }
//}