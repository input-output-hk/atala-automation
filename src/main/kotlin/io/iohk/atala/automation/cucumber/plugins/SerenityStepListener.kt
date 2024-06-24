package io.iohk.atala.automation.cucumber.plugins

import net.thucydides.model.domain.Story
import net.thucydides.model.domain.TestOutcome
import net.thucydides.model.domain.TestResult
import net.thucydides.model.screenshots.ScreenshotAndHtmlSource
import net.thucydides.model.steps.ExecutedStepDescription
import net.thucydides.model.steps.StepFailure
import net.thucydides.model.steps.StepListener
import java.time.ZonedDateTime

class SerenityStepListener: StepListener {
    data class Entry(
        val keyword: String,
        val stepText: String,
        val arguments: List<String>,
        val isSubStep: Boolean
    )
    companion object {
        val stepList = mutableListOf<Entry>()
    }

    override fun stepStarted(description: ExecutedStepDescription, startTime: ZonedDateTime) {
        val split = description.title.split(" ")
        val keyword = split[0] + " "
        val stepText = split.subList(1, split.size).joinToString(" ")
        val isSubStep = description.stepClass != null
        val arguments = description.arguments
        stepList.add(Entry(keyword, stepText, arguments, isSubStep))
    }

    override fun testSuiteStarted(storyClass: Class<*>?) {}
    override fun testSuiteStarted(story: Story) {}
    override fun testSuiteFinished() {}
    override fun testStarted(description: String) {}
    override fun testStarted(description: String, id: String) {}
    override fun testStarted(description: String, id: String, startTime: ZonedDateTime) {}
    override fun testFinished(result: TestOutcome) {}
    override fun testFinished(result: TestOutcome, isInDataDrivenTest: Boolean, finishTime: ZonedDateTime) {}
    override fun testRetried() {}
    override fun stepStarted(description: ExecutedStepDescription) {}
    override fun skippedStepStarted(description: ExecutedStepDescription) {}
    override fun stepFailed(failure: StepFailure) {}
    override fun stepFailed(failure: StepFailure?, screenshotList: MutableList<ScreenshotAndHtmlSource>?, isInDataDrivenTest: Boolean) {}
    override fun stepFailed(failure: StepFailure?, screenshotList: MutableList<ScreenshotAndHtmlSource>?, isInDataDrivenTest: Boolean, zonedDateTime: ZonedDateTime?) {}

    override fun lastStepFailed(failure: StepFailure) {}
    override fun stepIgnored() {}
    override fun stepPending() {}
    override fun stepPending(message: String) {}
    override fun stepFinished() {}
    override fun stepFinished(screenshotList: List<ScreenshotAndHtmlSource>, time: ZonedDateTime) {}
    override fun testFailed(testOutcome: TestOutcome, cause: Throwable) {}
    override fun testIgnored() {}
    override fun testSkipped() {}
    override fun testPending() {}
    override fun testIsManual() {}
    override fun notifyScreenChange() {}
    override fun useExamplesFrom(table: net.thucydides.model.domain.DataTable?) {}
    override fun addNewExamplesFrom(table: net.thucydides.model.domain.DataTable?) {}
    override fun exampleStarted(data: Map<String, String>) {}
    override fun exampleFinished() {}
    override fun assumptionViolated(message: String) {}
    override fun testRunFinished() {}
    override fun takeScreenshots(screenshots: List<ScreenshotAndHtmlSource>) {}
    override fun takeScreenshots(testResult: TestResult, screenshots: List<ScreenshotAndHtmlSource>) {}
}

