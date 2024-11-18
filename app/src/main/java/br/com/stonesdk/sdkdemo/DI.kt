package br.com.stonesdk.sdkdemo

import android.bluetooth.BluetoothAdapter
import br.com.stonesdk.sdkdemo.activities.devices.BluetoothProviderWrapper
import br.com.stonesdk.sdkdemo.activities.devices.DevicesViewModel
import co.stone.posmobile.sdk.provider.BluetoothProvider
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val demoApplicationModule = module {

    factory<BluetoothAdapter> {
        BluetoothAdapter.getDefaultAdapter()
    }

    factory<BluetoothProviderWrapper> {
        BluetoothProviderWrapper(get(), get())
    }

    single<BluetoothProvider> {
        BluetoothProvider.create(get())
    }

    viewModel {
        DevicesViewModel(get())
    }
}