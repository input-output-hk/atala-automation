package io.iohk.atala.automation.cucumber.common

class AnsiEscapes private constructor(private val value: String) {
    override fun toString(): String {
        val sb = java.lang.StringBuilder()
        appendTo(sb)
        return sb.toString()
    }

    fun appendTo(a: java.lang.StringBuilder) {
        a.append(ESC).append(BRACKET).append(
            value
        )
    }

    companion object {
        val RESET = color(0)
        val BLACK = color(30)
        val BRIGHT_BLACK = color(90)

        val RED = color(31)
        val BRIGHT_RED = color(91)

        val GREEN = color(32)
        val BRIGHT_GREEN = color(92)

        val YELLOW = color(33)
        val BLUE = color(34)
        val MAGENTA = color(35)
        val CYAN = color(36)
        val WHITE = color(37)
        val DEFAULT = color(9)
        val GREY = color(90)

        val INTENSITY_BOLD = color(1)
        val UNDERLINE = color(4)
        private const val ESC = 27.toChar()
        private const val BRACKET = '['
        private fun color(code: Int): AnsiEscapes {
            return AnsiEscapes(code.toString() + "m")
        }

        fun up(count: Int): AnsiEscapes {
            return AnsiEscapes(count.toString() + "A")
        }
    }
}
