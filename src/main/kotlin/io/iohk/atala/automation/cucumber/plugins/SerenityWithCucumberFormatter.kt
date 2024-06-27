package io.iohk.atala.automation.cucumber.plugins

import io.cucumber.core.exception.CucumberException
import io.cucumber.core.exception.ExceptionUtils
import io.cucumber.core.gherkin.DataTableArgument
import io.cucumber.datatable.DataTable
import io.cucumber.datatable.DataTableFormatter
import io.cucumber.plugin.ColorAware
import io.cucumber.plugin.ConcurrentEventListener
import io.cucumber.plugin.event.Argument
import io.cucumber.plugin.event.EmbedEvent
import io.cucumber.plugin.event.EventPublisher
import io.cucumber.plugin.event.PickleStepTestStep
import io.cucumber.plugin.event.Result
import io.cucumber.plugin.event.Step
import io.cucumber.plugin.event.TestCase
import io.cucumber.plugin.event.TestCaseStarted
import io.cucumber.plugin.event.TestRunFinished
import io.cucumber.plugin.event.TestStep
import io.cucumber.plugin.event.TestStepFinished
import io.cucumber.plugin.event.WriteEvent
import io.iohk.atala.automation.cucumber.common.Format
import io.iohk.atala.automation.cucumber.common.Formats.Companion.ansi
import io.iohk.atala.automation.cucumber.common.Formats.Companion.monochrome
import io.iohk.atala.automation.cucumber.common.UTF8PrintWriter
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.io.StringReader
import java.net.URI
import java.net.URISyntaxException
import java.util.Locale
import java.util.UUID
import kotlin.math.max

/**
 * Adapted from PrettyFormatter to add messages from SerenityListener.
 *
 * Original source: [PrettyFormatter](https://github.com/cucumber/cucumber-jvm/blob/release/v7.17.0/cucumber-core/src/main/java/io/cucumber/core/plugin/PrettyFormatter.java)
 */

class SerenityWithCucumberFormatter(out: OutputStream?) : ConcurrentEventListener, ColorAware {
    private val commentStartIndex: MutableMap<UUID, Int> = HashMap()

    private val out = UTF8PrintWriter(out!!)
    private var formats = ansi()

    override fun setEventPublisher(publisher: EventPublisher) {
        publisher.registerHandlerFor(TestCaseStarted::class.java) { event: TestCaseStarted ->
            this.handleTestCaseStarted(
                event
            )
        }
        publisher.registerHandlerFor(TestStepFinished::class.java) { event: TestStepFinished ->
            this.handleTestStepFinished(
                event
            )
        }
        publisher.registerHandlerFor(WriteEvent::class.java) { event: WriteEvent -> this.handleWrite(event) }
        publisher.registerHandlerFor(EmbedEvent::class.java) { event: EmbedEvent -> this.handleEmbed(event) }
        publisher.registerHandlerFor(TestRunFinished::class.java) { event: TestRunFinished ->
            this.handleTestRunFinished(
                event
            )
        }
    }

    private fun handleTestCaseStarted(event: TestCaseStarted) {
        out.println()
        preCalculateLocationIndent(event)
        printTags(event)
        printScenarioDefinition(event)
        out.flush()
    }

    private fun handleTestStepFinished(event: TestStepFinished) {
        printStep(event)
        printError(event)
        out.flush()
    }

    private fun handleWrite(event: WriteEvent) {
        out.println()
        printText(event)
        out.println()
        out.flush()
    }

    private fun handleEmbed(event: EmbedEvent) {
        out.println()
        printEmbedding(event)
        out.println()
        out.flush()
    }

    private fun handleTestRunFinished(event: TestRunFinished) {
        printError(event)
        out.close()
    }

    private fun preCalculateLocationIndent(event: TestCaseStarted) {
        val testCase = event.testCase
        val longestStep = testCase.testSteps.stream()
            .filter { obj: TestStep? -> PickleStepTestStep::class.java.isInstance(obj) }
            .map { obj: TestStep? -> PickleStepTestStep::class.java.cast(obj) }
            .map { obj: PickleStepTestStep -> obj.step }
            .map { step: Step -> formatPlainStep(step.keyword, step.text).length }
            .max(Comparator.naturalOrder())
            .orElse(0)

        val scenarioLength = formatScenarioDefinition(testCase).length
        commentStartIndex[testCase.id] = (max(longestStep.toDouble(), scenarioLength.toDouble()) + 1).toInt()
    }

    private fun printTags(event: TestCaseStarted) {
        val tags = event.testCase.tags
        if (tags.isNotEmpty()) {
            out.println(SCENARIO_INDENT + java.lang.String.join(" ", tags))
        }
    }

    private fun printScenarioDefinition(event: TestCaseStarted) {
        val testCase = event.testCase
        val definitionText = formatScenarioDefinition(testCase)
        val path = relativize(testCase.uri).schemeSpecificPart
        val locationIndent = calculateLocationIndent(event.testCase, SCENARIO_INDENT + definitionText)
        out.println(
            SCENARIO_INDENT + definitionText + locationIndent
                + formatLocation(path + ":" + testCase.location.line)
        )
    }


    private fun printStep(event: TestStepFinished) {
        if (event.testStep is PickleStepTestStep) {
            val testStep = event.testStep as PickleStepTestStep
            val keyword = testStep.step.keyword
            val stepText = testStep.step.text
            val status = event.result.status.name.lowercase(Locale.ROOT)
            val formattedStepText = formatStepText(
                keyword, stepText, formats[status],
                formats[status + "_arg"], testStep.definitionArgument
            )
            val locationComment = formatLocationComment(event, testStep, keyword, stepText)
            out.println(STEP_INDENT + formattedStepText + locationComment)
            val stepArgument = testStep.step.argument
            if (DataTableArgument::class.java.isInstance(stepArgument)) {
                val tableFormatter = DataTableFormatter
                    .builder()
                    .prefixRow(STEP_SCENARIO_INDENT)
                    .escapeDelimiters(false)
                    .build()
                val dataTableArgument = stepArgument as DataTableArgument
                try {
                    tableFormatter.formatTo(DataTable.create(dataTableArgument.cells()), out)
                } catch (e: IOException) {
                    throw CucumberException(e)
                }
            }
        }

        SerenityStepListener.stepList.forEach {
            if (it.isSubStep) {
                printSubStep(event, it.keyword, it.stepText)
            }
        }
        SerenityStepListener.stepList.clear()
    }

    private fun printSubStep(event: TestStepFinished, actor: String, stepText: String) {
        if (event.testStep is PickleStepTestStep) {
            val status = event.result.status.name.lowercase(Locale.ROOT)
            val formattedStepText = formatStepText(
                actor, stepText, formats[status], formats[status + "_arg"], emptyList()
            )
            out.println(SUB_STEP_INDENT + formattedStepText)
        }
    }

    private fun formatLocationComment(
        event: TestStepFinished, testStep: PickleStepTestStep, keyword: String, stepText: String
    ): String {
        val codeLocation = testStep.codeLocation ?: return ""
        val locationIndent = calculateLocationIndent(event.testCase, formatPlainStep(keyword, stepText))
        return locationIndent + formatLocation(codeLocation)
    }

    private fun printError(event: TestStepFinished) {
        val result = event.result
        printError(result)
    }

    private fun printError(event: TestRunFinished) {
        val result = event.result
        printError(result)
    }

    private fun printError(result: Result) {
        val error = result.error
        if (error != null) {
            val name = result.status.name.lowercase(Locale.ROOT)
            val format = formats[name]
            val text = ExceptionUtils.printStackTrace(error)
            out.println("      " + format.text(text))
        }
    }

    private fun printText(event: WriteEvent) {
        // Prevent interleaving when multiple threads write to System.out
        val builder = StringBuilder()
        try {
            BufferedReader(StringReader(event.text)).use { lines ->
                var line: String?
                while ((lines.readLine().also { line = it }) != null) {
                    builder.append(STEP_SCENARIO_INDENT)
                        .append(line) // Add system line separator - \n won't do it!
                        .append(System.lineSeparator())
                }
            }
        } catch (e: IOException) {
            throw CucumberException(e)
        }
        out.append(builder)
    }

    private fun printEmbedding(event: EmbedEvent) {
        val line = ("Embedding " + event.getName() + " [" + event.mediaType + " " + event.data.size
            + " bytes]")
        out.println(STEP_SCENARIO_INDENT + line)
    }

    private fun formatPlainStep(keyword: String, stepText: String): String {
        return STEP_INDENT + keyword + stepText
    }

    private fun formatScenarioDefinition(testCase: TestCase): String {
        return testCase.keyword + ": " + testCase.name
    }

    private fun calculateLocationIndent(testStep: TestCase, prefix: String): String {
        val commentStartAt = commentStartIndex.getOrDefault(testStep.id, 0)
        val padding = commentStartAt - prefix.length

        if (padding < 0) {
            return " "
        }
        val builder = StringBuilder(padding)
        repeat(padding) {
            builder.append(" ")
        }
        return builder.toString()
    }

    private fun formatLocation(location: String): String {
        return formats["comment"].text("# $location")
    }

    private fun formatStepText(
        keyword: String?, stepText: String, textFormat: Format, argFormat: Format, arguments: List<Argument>
    ): String {
        var beginIndex = 0
        val result = StringBuilder(textFormat.text(keyword!!))
        for (argument in arguments) {
            // can be null if the argument is missing.
            if (argument.value != null) {
                val argumentOffset = argument.start
                // a nested argument starts before the enclosing argument ends;
                // ignore it when formatting
                if (argumentOffset < beginIndex) {
                    continue
                }
                val text = stepText.substring(beginIndex, argumentOffset)
                result.append(textFormat.text(text))
            }
            // val can be null if the argument isn't there, for example
            // @And("(it )?has something")
            if (argument.value != null) {
                val text = stepText.substring(argument.start, argument.end)
                result.append(argFormat.text(text))
                // set beginIndex to end of argument
                beginIndex = argument.end
            }
        }
        if (beginIndex != stepText.length) {
            val text = stepText.substring(beginIndex)
            result.append(textFormat.text(text))
        }
        return result.toString()
    }

    override fun setMonochrome(monochrome: Boolean) {
        formats = if (monochrome) monochrome() else ansi()
    }

    companion object {
        private const val SCENARIO_INDENT = ""
        private const val STEP_INDENT = "  "
        private const val SUB_STEP_INDENT = "    "
        private const val STEP_SCENARIO_INDENT = "    "
        const val PLUGIN: String = "io.iohk.atala.automation.cucumber.plugins.SerenityWithCucumberFormatter"

        fun relativize(uri: URI): URI {
            if ("file" != uri.scheme || !uri.isAbsolute) {
                return uri
            }

            try {
                val root = File("").toURI()
                val relative = root.relativize(uri)
                // Scheme is lost by relativize
                return URI("file", relative.schemeSpecificPart, relative.fragment)
            } catch (e: URISyntaxException) {
                throw IllegalArgumentException(e.message, e)
            }
        }
    }
}
