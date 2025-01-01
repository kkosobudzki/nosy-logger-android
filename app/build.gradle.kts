import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import com.google.protobuf.gradle.proto
import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.protobuf)

    `maven-publish`
    signing
}

android {
    namespace = "dev.nosytools.logger"
    compileSdk = 35

    defaultConfig {
        minSdk = 28
    }

    signingConfigs {
        create("release") {
            System.getenv("KEYSTORE")?.let {
                storeFile = file(System.getenv("KEYSTORE"))
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
            }
        }
    }

    buildFeatures {
        buildConfig = true
    }

    val props = Properties().apply {
        load(FileInputStream(File(rootProject.rootDir, "local.properties")))
    }

    buildTypes.forEach {
        it.buildConfigField("String", "API_URL", "\"${props.getProperty("apiUrl")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        java {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    sourceSets {
        getByName("main") {
            proto {
                srcDir("src/main/protos")
            }
        }
    }

    libraryVariants.all {
        val variant = name

        outputs.all {
            val output = this as BaseVariantOutputImpl;
            output.outputFileName = "nosy-logger-${variant}.aar"
        }
    }

    packaging {
        resources {
            excludes.add("logger.proto")
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.work.ktx)
    implementation(libs.bouncycastle.pkix)
    implementation(libs.bouncycastle.prov)
    implementation(libs.protobuf)
    implementation(libs.javax.annotation.api)
    implementation(libs.koin.core)
    implementation(libs.okhttp)
}

protobuf {
    protoc {
        artifact = libs.protoc.get().toString()
    }

    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "dev.nosytools"
            artifactId = "logger"
            version = System.getenv("VERSION")

            pom {
                name = "logger"
                description = "Nosy Logger android plugin"
                url = "https://logger.nosytools.dev/"

                licenses {
                    license {
                        name = "GNU GPL v3"
                        url = "https://www.gnu.org/licenses/gpl-3.0.html"
                    }
                }

                scm {
                    url = "https://github.com/kkosobudzki/nosy-logger-android.git"
                }

                developers {
                    developer {
                        name = "Krzysztof Kosobudzki"
                        email = "krzysztof.kosobudzki@gmail.com"
                        // TODO update with nosytools.dev email
                    }
                }
            }

            afterEvaluate {
                from(components["release"])
            }
        }
    }

    signing {
        val id = System.getenv("SIGNING_KEY_ID")
        val password = System.getenv("SIGNING_KEY_PASSWORD")
        val key = System.getenv("SIGNING_KEY")

        useInMemoryPgpKeys(id, key, password)

        sign(publishing.publications.getByName("maven"))
    }

    repositories {
        maven {
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")

            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }
}
