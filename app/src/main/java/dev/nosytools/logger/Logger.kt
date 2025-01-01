package dev.nosytools.logger

import android.content.Context
import dev.nosytools.logger.rest.Collector
import dev.nosytools.logger.scheduler.Scheduler
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.security.Security
import nosytools.logger.Logger.Level

class Logger(private val context: Context) {

    init {
        Security.removeProvider("BC");
        Security.addProvider(BouncyCastleProvider())
    }

    private val scheduler by lazy { Scheduler(context) }

    private val grpcModule = module {
        single { Collector(getProperty("api-key")) }
    }

    private var initialized = false

    fun init(apiKey: String) {
        if (initialized) {
            "NosyLogger :: Logger :: init - already initialized, skipping".log()
        } else {
            startKoin {
                properties(
                    mapOf("api-key" to apiKey)
                )

                modules(grpcModule)
            }

            initialized = true
        }
    }

    fun debug(message: String) = log(message, Level.LEVEL_DEBUG)

    fun info(message: String) = log(message, Level.LEVEL_INFO)

    fun warning(message: String) = log(message, Level.LEVEL_WARN)

    fun error(message: String) = log(message, Level.LEVEL_ERROR)

    fun exception(throwable: Throwable) = error(throwable.message ?: "$throwable")

    private fun log(message: String, level: Level) {
        if (initialized) {
            scheduler.schedule(TemporaryLog(message, level, now()))
        } else {
            throw IllegalStateException("Not initialized - make sure to call init() before you start logging")
        }
    }
}
