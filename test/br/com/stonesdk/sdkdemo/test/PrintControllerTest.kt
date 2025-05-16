package br.com.stonesdk.sdkdemo.test

import android.content.Context
import br.com.stone.posandroid.providers.PosPrintReceiptProvider
import br.com.stonesdk.sdkdemo.controller.PrintController
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith


@ExtendWith(MockKExtension::class)
internal class PrintControllerTest {

    private val context = mockk<Context>()

    @RelaxedMockK
    lateinit var provider: PosPrintReceiptProvider

    private lateinit var printer: PrintController

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        printer = PrintController(context, provider)
    }

    @Test
    fun print() {
        printer.print()

        verify { provider.execute() }
    }

}