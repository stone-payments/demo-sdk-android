package br.com.stonesdk.sdkdemo

import android.bluetooth.BluetoothAdapter
import br.com.stone.posandroid.hal.api.settings.DeviceInfo
import br.com.stonesdk.sdkdemo.activities.devices.BluetoothProviderWrapper
import br.com.stonesdk.sdkdemo.activities.devices.DevicesViewModel
import br.com.stonesdk.sdkdemo.activities.main.MainViewModel
import br.com.stonesdk.sdkdemo.activities.manageStoneCode.ActivationProviderWrapper
import br.com.stonesdk.sdkdemo.activities.manageStoneCode.ManageStoneCodeViewModel
import br.com.stonesdk.sdkdemo.activities.transaction.InstallmentProvider
import br.com.stonesdk.sdkdemo.activities.transaction.TransactionProviderWrapper
import br.com.stonesdk.sdkdemo.activities.transaction.TransactionViewModel
import br.com.stonesdk.sdkdemo.activities.validation.AppInitializer
import br.com.stonesdk.sdkdemo.activities.validation.ValidationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.factory
import org.koin.dsl.module
import stone.application.SessionApplication
import stone.database.transaction.TransactionObject
import stone.utils.Stone

val demoApplicationModule = module {

    factory<SessionApplication> {
        Stone.sessionApplication
    }

    factory<ActivationProviderWrapper> {
        ActivationProviderWrapper(get())
    }
    factory<AppInitializer> {
        AppInitializer(get())
    }

    factory<BluetoothProviderWrapper> {
        BluetoothProviderWrapper(get(), get())
    }
    factory<BluetoothAdapter> {
        Stone.bluetoothAdapter
    }
    factory<TransactionProviderWrapper> {
        TransactionProviderWrapper(get())
    }
    factory<InstallmentProvider> {
        InstallmentProvider()
    }
    factory<TransactionObject> {
        TransactionObject()
    }

    viewModel {
        ManageStoneCodeViewModel(sessionApplication = get(), providerWrapper = get())
    }
    viewModel {
        ValidationViewModel(providerWrapper = get(), appInitializer = get())
    }
    viewModel {
        DevicesViewModel(providerWrapper = get())
    }
    viewModel {
        TransactionViewModel(
            installmentProvider = get(),
            transactionObject = get(),
            sessionApplication = get()
        )
    }
    viewModel {
        MainViewModel()
    }
}