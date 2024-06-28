package io.iohk.atala.automation.cucumber.common

import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

class UTF8OutputStreamWriter(out: OutputStream) : OutputStreamWriter(out, StandardCharsets.UTF_8)
