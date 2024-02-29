package dev.nosytools.logger

data class Config(val url: String, val apiKey: String) {
    companion object {
        fun create(apiKey: String): Config = Config(
            url = "todo",
            apiKey
        )
    }
}
