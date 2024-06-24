package io.iohk.atala.automation.cucumber.common

interface Format {
    fun text(text: String): String

    class Color internal constructor(private vararg val escapes: AnsiEscapes) : Format {

        override fun text(text: String): String {
            val sb = java.lang.StringBuilder()
            for (escape in escapes) {
                escape.appendTo(sb)
            }
            sb.append(text)
            if (escapes.isNotEmpty()) {
                AnsiEscapes.RESET.appendTo(sb)
            }
            return sb.toString()
        }
    }

    class Monochrome internal constructor() : Format {
        override fun text(text: String): String {
            return text
        }
    }

    companion object {
        fun color(vararg escapes: AnsiEscapes): Format {
            return Color(*escapes)
        }

        fun monochrome(): Format {
            return Monochrome()
        }
    }
}
