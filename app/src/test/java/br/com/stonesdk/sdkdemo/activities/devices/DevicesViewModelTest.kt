@file:OptIn(ExperimentalCoroutinesApi::class)

package br.com.stonesdk.sdkdemo.activities.devices

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class DevicesViewModelTest {

    private val providerWrapper: BluetoothProviderWrapper = mockk(relaxed = true)

    private lateinit var viewModel: DevicesViewModel
    private val testScheduler = TestCoroutineScheduler()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        viewModel = DevicesViewModel(
            providerWrapper = providerWrapper,
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onEvent DeviceItemClick should connect to pinpad and close screen on success`() = runTest {
        // given
        coEvery { providerWrapper.connectPinpad(pinpad = any()) } returns true
        coEvery { providerWrapper.listBluetoothDevices() } returns listOf(BluetoothInfo("pinpad1", "123:456"))

        viewModel.onEvent(DevicesEvent.Permission)

        // then
        viewModel.onEvent(DevicesEvent.DeviceItemClick(0))
        val sideEffect = viewModel.sideEffects.first()
        advanceUntilIdle()

        // assert
        coVerify { providerWrapper.connectPinpad(pinpad = any()) }
        assertEquals(DeviceEffects.CloseScreen, sideEffect)

    }

    @Test
    fun `onEvent EnableBluetooth should call turnBluetoothOn`() {

        every { providerWrapper.turnBluetoothOn() } just runs

        viewModel.onEvent(DevicesEvent.EnableBluetooth)

        verify { providerWrapper.turnBluetoothOn() }

    }

    @Test
    fun `onEvent Permission should list Bluetooth devices`() {

        every { providerWrapper.listBluetoothDevices() } returns emptyList()

        viewModel.onEvent(DevicesEvent.Permission)

        verify { providerWrapper.listBluetoothDevices() }
    }

}