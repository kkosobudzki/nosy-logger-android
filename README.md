# Nosy Logger Android

Plugin that allows logging to Nosy Logger with Android native apps.

Nosy Logger is security focused logger yet simple in usage. Every message is encrypted before it is send. Messages are decrypted in dashboard right before they are shown.

## Integration

Add maven dependency to app `build.gradle`:

```gradle
implementation 'dev.nosytools:logger:1.6.4'
```

or when using Version Catalog:

```toml
[versions]
nosyLogger = "1.6.4"

[libraries]
nosy-logger = { group = "dev.nosytools", name = "logger", version.ref = "nosyLogger" }
```

and then to `build.gradle`:

```gradle
implementation(libs.nosy.logger)
```

## Initialization

Firstly, you will need to initialize logger. Do this as soon as possible, best in your custom application class.

```kotlin
class MyApp : Application() {

  internal val logger by lazy { Logger(applicationContext) }

  override fun onCreate() {
    super.onCreate()

    logger.init("api key for your project environment")
  }
}
```

It is safe to provide and initialize logger with dependency injection (Koin, Dagger, Hilt).

### Koin

TODO create sample

### Dagger

TODO create sample

### Hilt

TODO create sample

## Usage

### debug

Logs message with `debug` level.

```kotlin
logger.debug("my debug message")
```

### info

Logs message with `info` level.

```kotlin
logger.info("my info message")
```

### warning

Logs message with `warning` level.

```kotlin
logger.warning("my warning message")
```

### error

Logs message with `error` level.

```kotlin
logger.error("my error message")
```

### exception

Logs throwable / exception with `error` level.

```kotlin
logger.exception(IllegalStateException("some illegal state exception"))
```

## Scheduling

First logs will be logged in 15 seconds after initialization, then every 15 minutes.

Nosy Logger for Android utilizes Work Manager, thus it sends collected logs only where it is convenient for user. It will not try to do so when user is having low storage or low battery level.
