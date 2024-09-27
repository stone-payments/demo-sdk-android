@file:OptIn(ExperimentalCoroutinesApi::class)

package br.com.stonesdk.sdkdemo.activities.validation

import br.com.stonesdk.sdkdemo.activities.manageStoneCode.ActivationProviderWrapper
import br.com.stonesdk.sdkdemo.activities.validation.ValidationStoneCodeEffects.NavigateToMain
import br.com.stonesdk.sdkdemo.activities.validation.ValidationStoneCodeEvent.Activate
import br.com.stonesdk.sdkdemo.activities.validation.ValidationStoneCodeEvent.UserInput
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import stone.environment.Environment
import stone.utils.Stone

@RunWith(JUnit4::class)
class ValidationViewModelTest {
    private val providerWrapper: ActivationProviderWrapper = mockk(relaxed = true)
    private val appInitializer: AppInitializer = mockk(relaxed = true)

    private lateinit var viewModel: ValidationViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockkStatic(Stone::class)
        every { Stone.getEnvironment() } returns Environment.SANDBOX

        viewModel = ValidationViewModel(
            providerWrapper = providerWrapper,
            appInitializer = appInitializer
        )
        every { appInitializer.initiateApp() } returns false

    }

    @After
    fun tearDown() {
        unmockkStatic(Stone::class)
        Dispatchers.resetMain()
    }

    @Test
    fun `given UserInput should return the exactly inserted valued`() = runTest {

        val stoneCode = "44444444"

        viewModel.onEvent(UserInput(stoneCode))

        assert(viewModel.viewState.stoneCodeToBeValidated == stoneCode)
    }

    @Test
    fun `given ActivateStoneCode with success should navigate to Main`() = runTest {

        viewModel.onEvent(UserInput("44444444"))
        coEvery { providerWrapper.activate(any()) } returns true

        val emittedEffects = mutableListOf<ValidationStoneCodeEffects?>()
        val sideEffectJob = launch { viewModel.sideEffects.toList(emittedEffects) }

        viewModel.onEvent(Activate)
        advanceUntilIdle()

        coVerify { providerWrapper.activate(any()) }
        assertTrue(emittedEffects.contains(NavigateToMain))

        sideEffectJob.cancel()

    }

    @Test
    fun `given EnvironmentReturned event, test getEnvironment should returns correct environment`() =
        runTest {

            assertEquals(Environment.SANDBOX, viewModel.viewState.getEnvironment)

        }

}