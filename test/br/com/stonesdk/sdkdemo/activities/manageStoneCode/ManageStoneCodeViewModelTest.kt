package br.com.stonesdk.sdkdemo.activities.manageStoneCode

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import stone.application.SessionApplication
import stone.user.UserModel

@RunWith(JUnit4::class)
class ManageStoneCodeViewModelTest {

    private val sessionApplication: SessionApplication = mockk(relaxed = true)
    private val providerWrapper: ActivationProviderWrapper = mockk(relaxed = true)
    private val testScheduler = TestCoroutineScheduler()

    private val viewModel = ManageStoneCodeViewModel(
        sessionApplication = sessionApplication,
        providerWrapper = providerWrapper
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun before() {
        every { sessionApplication.userModelList } returns listOf()
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun after() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given UserInput should return the exactly inserted valued`() {
        //given
        val inserted = "8888888"

        //then
        viewModel.onEvent(ManageStoneCodeEvent.UserInput(inserted))

        //assert
        assert(viewModel.viewState.stoneCodeToBeActivated == inserted)
    }

    @Test
    fun `given OnDismiss should dismiss bottomSheet`() {

        //then
        viewModel.onEvent(ManageStoneCodeEvent.OnDismiss)

        //assert
        assert(!viewModel.viewState.showBottomSheet)
    }

    @Test
    fun `given AddStoneCode should show bottomSheet to insert stone code`() {

        //then
        viewModel.onEvent(ManageStoneCodeEvent.AddStoneCode)
        //assert
        assert(viewModel.viewState.showBottomSheet)
    }

    @Test
    fun `given ActivateStoneCode with success should list stone codes activated`() = runTest {
        //given
        viewModel.onEvent(ManageStoneCodeEvent.UserInput("8888888"))
        coEvery { providerWrapper.activate(any()) } returns true
        every {
            sessionApplication.userModelList
        } returns listOf(UserModel().apply { stoneCode = "8888888" })

        //then
        viewModel.onEvent(ManageStoneCodeEvent.ActivateStoneCode)

        //assert
        assert(viewModel.viewState.stoneCodesActivated.isNotEmpty())
    }

    @Test
    fun `given ActivateStoneCode with error should show error`() = runTest {
        //given
        viewModel.onEvent(ManageStoneCodeEvent.AddStoneCode)
        viewModel.onEvent(ManageStoneCodeEvent.UserInput("8888888"))
        coEvery { providerWrapper.activate(any()) } returns false

        //then
        viewModel.onEvent(ManageStoneCodeEvent.ActivateStoneCode)

        //assert
        assert(viewModel.viewState.stoneCodesActivated.isEmpty())
        assert(viewModel.viewState.error)
    }

    @Test
    fun `given StoneCodeItemClick should show removal of stone code`() = runTest {
        //given
        every { sessionApplication.userModelList }.returnsMany(
            listOf(
                UserModel().apply { stoneCode = "111" },
                UserModel().apply { stoneCode = "222" }
            ),
            listOf(UserModel().apply { stoneCode = "111" })
        )
        coEvery { providerWrapper.deactivate("222") } returns true

        val viewModel = ManageStoneCodeViewModel(sessionApplication, providerWrapper)

        //then
        viewModel.onEvent(ManageStoneCodeEvent.StoneCodeItemClick(position = 1))

        //assert
        assert(viewModel.viewState.stoneCodesActivated == listOf("111"))
    }
}