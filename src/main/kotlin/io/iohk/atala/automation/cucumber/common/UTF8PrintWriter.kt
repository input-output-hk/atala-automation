package io.iohk.atala.automation.cucumber.common

import java.io.Closeable
import java.io.Flushable
import java.io.IOException
import java.io.OutputStream
import java.io.OutputStreamWriter

/**
 * A "good enough" PrintWriter implementation that writes UTF-8 and rethrows all
 * exceptions as runtime exceptions.
 */
class UTF8PrintWriter(out: OutputStream) : Appendable, Closeable, Flushable {
    private val out: OutputStreamWriter = UTF8OutputStreamWriter(out)

    fun println() {
        try {
            out.write(System.lineSeparator())
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    fun println(s: String) {
        try {
            out.write(s)
            out.write(System.lineSeparator())
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    override fun flush() {
        try {
            out.flush()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    override fun close() {
        try {
            out.close()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    override fun append(csq: CharSequence): Appendable {
        try {
            return out.append(csq)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    override fun append(csq: CharSequence, start: Int, end: Int): Appendable {
        try {
            return out.append(csq, start, end)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    override fun append(c: Char): Appendable {
        try {
            return out.append(c)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}
