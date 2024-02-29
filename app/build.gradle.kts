import com.google.protobuf.gradle.proto

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.protobuf)

    `maven-publish`
    signing
}

android {
    namespace = "dev.nosytools.logger"
    compileSdk = 34

    defaultConfig {
        minSdk = 28

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            // TODO => true
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildTypes.forEach {
        it.buildConfigField("String", "COLLECTOR_URL", "\"logger-collector.fly.dev\"")
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
                srcDir("../../protos")
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.bouncycastle.pkix)
    implementation(libs.bouncycastle.prov)
    implementation(libs.grpc.okhttp)
    implementation(libs.grpc.protobuf.lite)
    implementation(libs.grpc.stub)
    implementation(libs.javax.annotation.api)

    testImplementation(libs.junit)
}

protobuf {
    protoc {
        artifact = libs.protoc.get().toString()
    }

    plugins {
        create("grpc") {
            artifact = libs.grpc.protoc.get().toString()
        }
    }

    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
            }

            task.plugins {
                create("grpc") {
                    option("lite")
                }
            }
        }
    }
}

publishing {
    publications {
        register("release", MavenPublication::class) {
            groupId = "dev.nosytools"
            artifactId = "logger"
            version = android.defaultConfig.versionName

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
                    url = "https://github.com/kkosobudzki/nosy-android.git"
                }

                developers {
                    developer {
                        name = 'Krzysztof Kosobudzki'
                        email = 'krzysztof.kosobudzki@gmail.com'
                        // TODO update with nosytools.dev email
                    }
                }
            }

            afterEvaluate {
                from components.release
            }
        }
    }

    signing {
        def id = System.getenv("SIGNING_KEY_ID")
        def password = System.getenv("SIGNING_KEY_PASSWORD")
        def key = System.getenv("SIGNING_KEY")

        useInMemoryPgpKeys(id, key, password)

        sign publishing.publications.release
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
