package com.codemate.brewflop.ui.main

import android.os.Handler
import com.codemate.brewflop.data.BrewingProgressUpdater
import org.hamcrest.core.IsEqual.equalTo
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import java.util.concurrent.TimeUnit

class BrewingProgressUpdaterTest {
    private lateinit var mockHandler: Handler

    @Before
    fun setUp() {
        mockHandler = mock(Handler::class.java)
    }

    @Test
    fun calculatesCorrectUpdateIntervals() {
        val sutOne = createUpdater(TimeUnit.SECONDS.toMillis(5), 10)
        assertThat(sutOne.updateInterval, equalTo(500L))

        val sutTwo = createUpdater(TimeUnit.SECONDS.toMillis(9), 4)
        assertThat(sutTwo.updateInterval, equalTo(2250L))
    }

    @Test
    fun reset_ShouldCleanStateAndRemoveCallbacks() {
        val sut = createUpdater(1, 1)

        sut.startUpdating({}, {})
        verify(mockHandler, times(1)).postDelayed(sut, 1)

        sut.reset()

        assertNull(sut.updateListener)
        assertNull(sut.completeListener)
        assertFalse(sut.isUpdating)
        assertThat(sut.currentStep, equalTo(0))

        verify(mockHandler, times(1)).removeCallbacks(sut)
        verifyNoMoreInteractions(mockHandler)
    }

    @Test
    fun startUpdating_CallsPostDelayed() {
        val sut = createUpdater(1, 1)
        sut.startUpdating({}, {})

        verify(mockHandler).postDelayed(sut, 1)
        verifyNoMoreInteractions(mockHandler)
    }

    @Test
    fun startUpdating_WhenCalledMultipleTimes_CallsPostDelayedOnlyOnce() {
        val sut = createUpdater(1, 1)

        sut.startUpdating({}, {})
        sut.startUpdating({}, {})
        sut.startUpdating({}, {})

        verify(mockHandler, times(1)).postDelayed(sut, 1)
        verifyNoMoreInteractions(mockHandler)
    }

    /**
     * If you come up with a better name, just send a PR ¯\_(ツ)_/¯
     */
    @Test
    fun shouldRunProperly() {
        val sut = createUpdater(TimeUnit.SECONDS.toMillis(2), 4)
        sut.startUpdating({}, {})

        sut.run()
        assertThat(sut.currentStep, equalTo(1))
        assertThat(sut.calculateCurrentProgress(), equalTo(25))

        sut.run()
        sut.run()
        assertThat(sut.currentStep, equalTo(3))
        assertThat(sut.calculateCurrentProgress(), equalTo(75))

        sut.run()
        assertThat(sut.currentStep, equalTo(0))
        assertThat(sut.calculateCurrentProgress(), equalTo(0))

        verify(mockHandler, times(4)).postDelayed(sut, 500)
        verify(mockHandler, times(1)).removeCallbacks(sut)
        verifyNoMoreInteractions(mockHandler)
    }

    @Test
    fun run_WhenIsUpdatingEqualsFalse_DoesNothing() {
        val sut = createUpdater(TimeUnit.SECONDS.toMillis(5), 15)

        sut.startUpdating({}, {})
        sut.isUpdating = false

        sut.run()
        sut.run()
        sut.run()

        assertThat(sut.currentStep, equalTo(0))
    }

    fun createUpdater(totalTimeMillis: Long, totalSteps: Int) : BrewingProgressUpdater {
        val updater = BrewingProgressUpdater(totalTimeMillis, totalSteps)
        updater.updateHandler = mockHandler

        return updater
    }
}