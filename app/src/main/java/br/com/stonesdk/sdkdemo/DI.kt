package br.com.stonesdk.sdkdemo

import android.bluetooth.BluetoothAdapter
import br.com.stonesdk.sdkdemo.activities.devices.BluetoothProviderWrapper
import br.com.stonesdk.sdkdemo.activities.devices.DevicesViewModel
import br.com.stonesdk.sdkdemo.activities.manageStoneCode.ActivationProviderWrapper
import br.com.stonesdk.sdkdemo.activities.manageStoneCode.ManageStoneCodeViewModel
import br.com.stonesdk.sdkdemo.activities.validation.AppInitializer
import br.com.stonesdk.sdkdemo.activities.validation.ValidationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import stone.application.SessionApplication
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
    factory <BluetoothAdapter>{
        Stone.bluetoothAdapter
    }

    viewModel {
        ManageStoneCodeViewModel(get(), get())
    }
    viewModel {
        ValidationViewModel(get(), get())
    }
    viewModel {
        DevicesViewModel(get())
    }
}