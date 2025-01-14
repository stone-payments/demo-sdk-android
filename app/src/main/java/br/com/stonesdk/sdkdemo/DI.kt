package br.com.stonesdk.sdkdemo

import android.bluetooth.BluetoothAdapter
import br.com.stonesdk.sdkdemo.activities.devices.BluetoothProviderWrapper
import br.com.stonesdk.sdkdemo.activities.devices.DevicesViewModel
import br.com.stonesdk.sdkdemo.activities.main.MainViewModel
import br.com.stonesdk.sdkdemo.activities.manageStoneCode.ActivationProviderWrapper
import br.com.stonesdk.sdkdemo.activities.manageStoneCode.ManageStoneCodeViewModel
import br.com.stonesdk.sdkdemo.activities.transaction.TransactionViewModel
import br.com.stonesdk.sdkdemo.activities.validation.ValidationViewModel
import co.stone.posmobile.sdk.hardware.provider.bluetooth.BluetoothProvider
import co.stone.posmobile.sdk.payment.provider.PaymentProvider
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val demoApplicationModule = module {

    factory<ActivationProviderWrapper> {
        ActivationProviderWrapper()
    }

    factory<BluetoothAdapter> {
        BluetoothAdapter.getDefaultAdapter()
    }

    factory {
        BluetoothProviderWrapper()
    }

    single<BluetoothProvider> {
        BluetoothProvider.create()
    }

    factory<PaymentProvider> {
        PaymentProvider.create()
    }

    viewModel {
        ManageStoneCodeViewModel(activationProviderWrapper = get())
    }
    viewModel {
        ValidationViewModel(activationProviderWrapper = get())
    }
    viewModel {
        DevicesViewModel(bluetoothProviderWrapper = get())
    }
    viewModel {
        TransactionViewModel(
            installmentProvider = get(),
            transactionObject = get(),
            sessionApplication = get()
        )
    }
    viewModel {
        MainViewModel(
            reversalProvider = get(),
            activationProviderWrapper = get()
        )
    }
}