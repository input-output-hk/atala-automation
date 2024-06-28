package io.iohk.atala.automation.cucumber.common


interface Formats {
    operator fun get(key: String): Format
    fun up(n: Int): String

    companion object {
        @JvmStatic
        fun monochrome(): Formats {
            return Monochrome()
        }

        @JvmStatic
        fun ansi(): Formats {
            return Ansi()
        }
    }

    class Monochrome internal constructor() : Formats {
        override fun get(key: String): Format {
            return Format.monochrome()
        }

        override fun up(n: Int): String {
            return ""
        }
    }

    class Ansi internal constructor() : Formats {
        override fun get(key: String): Format {
            val format: Format = formats[key] ?: throw NullPointerException("No format for key $key")
            return format
        }

        override fun up(n: Int): String {
            return AnsiEscapes.up(n).toString()
        }

        companion object {
            private val formats: Map<String, Format> = object : java.util.HashMap<String, Format>() {
                init {
                    // Never used, but avoids NPE in formatters.
                    put("undefined", Format.color(AnsiEscapes.YELLOW))
                    put("undefined_arg", Format.color(AnsiEscapes.YELLOW, AnsiEscapes.INTENSITY_BOLD))
                    put("unused", Format.color(AnsiEscapes.YELLOW))
                    put("unused_arg", Format.color(AnsiEscapes.YELLOW, AnsiEscapes.INTENSITY_BOLD))
                    put("pending", Format.color(AnsiEscapes.YELLOW))
                    put("pending_arg", Format.color(AnsiEscapes.YELLOW, AnsiEscapes.INTENSITY_BOLD))
                    put("executing", Format.color(AnsiEscapes.GREY))
                    put("executing_arg", Format.color(AnsiEscapes.GREY, AnsiEscapes.INTENSITY_BOLD))
                    put("failed", Format.color(AnsiEscapes.RED))
                    put("failed_arg", Format.color(AnsiEscapes.RED, AnsiEscapes.INTENSITY_BOLD))
                    put("ambiguous", Format.color(AnsiEscapes.RED))
                    put("ambiguous_arg", Format.color(AnsiEscapes.RED, AnsiEscapes.INTENSITY_BOLD))
                    put("passed", Format.color(AnsiEscapes.GREEN))
                    put("passed_arg", Format.color(AnsiEscapes.BRIGHT_GREEN, AnsiEscapes.INTENSITY_BOLD))
                    put("outline", Format.color(AnsiEscapes.CYAN))
                    put("outline_arg", Format.color(AnsiEscapes.CYAN, AnsiEscapes.INTENSITY_BOLD))
                    put("skipped", Format.color(AnsiEscapes.CYAN))
                    put("skipped_arg", Format.color(AnsiEscapes.CYAN, AnsiEscapes.INTENSITY_BOLD))
                    put("comment", Format.color(AnsiEscapes.GREY))
                    put("tag", Format.color(AnsiEscapes.CYAN))
                    put("output", Format.color(AnsiEscapes.BLUE))
                }
            }
        }
    }
}
