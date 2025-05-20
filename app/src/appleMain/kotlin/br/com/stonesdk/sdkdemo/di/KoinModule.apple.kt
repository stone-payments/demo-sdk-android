package br.com.stonesdk.sdkdemo.di

import br.com.stonesdk.sdkdemo.data.LocalStorageCreator
import br.com.stonesdk.sdkdemo.ui.cancel_transactions.CancelViewModel
import br.com.stonesdk.sdkdemo.ui.display.DisplayMessageViewModel
import br.com.stonesdk.sdkdemo.ui.main.MainViewModel
import br.com.stonesdk.sdkdemo.ui.manage_stone_codes.ManageStoneCodeViewModel
import br.com.stonesdk.sdkdemo.ui.paired_devices.BluetoothDeviceRepository
import br.com.stonesdk.sdkdemo.ui.paired_devices.DevicesViewModel
import br.com.stonesdk.sdkdemo.ui.splashscreen.ValidationViewModel
import br.com.stonesdk.sdkdemo.ui.transactions.TransactionViewModel
import br.com.stonesdk.sdkdemo.ui.transactions.transactionList.TransactionListViewModel
import br.com.stonesdk.sdkdemo.wrappers.ActivationProviderWrapper
import br.com.stonesdk.sdkdemo.wrappers.BluetoothProviderWrapper
import br.com.stonesdk.sdkdemo.wrappers.CancelProviderWrapper
import br.com.stonesdk.sdkdemo.wrappers.DeviceInfoProviderWrapper
import br.com.stonesdk.sdkdemo.wrappers.DisplayProviderWrapper
import br.com.stonesdk.sdkdemo.wrappers.EmailProviderWrapper
import br.com.stonesdk.sdkdemo.wrappers.InstallmentProvider
import br.com.stonesdk.sdkdemo.wrappers.MerchantProviderWrapper
import br.com.stonesdk.sdkdemo.wrappers.PaymentProviderWrapper
import br.com.stonesdk.sdkdemo.wrappers.ReversalProviderWrapper
import br.com.stonesdk.sdkdemo.wrappers.TransactionListProviderWrapper
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

actual val targetModule: Module = module {

    single { LocalStorageCreator() }

    factory { ActivationProviderWrapper() }
    factory { BluetoothProviderWrapper() }
    factory { CancelProviderWrapper() }
    factory { DeviceInfoProviderWrapper() }
    factory { DisplayProviderWrapper() }
    factory { EmailProviderWrapper() }
    factory { InstallmentProvider() }
    factory { MerchantProviderWrapper() }
    factory { PaymentProviderWrapper() }
    factory { ReversalProviderWrapper() }
    factory { TransactionListProviderWrapper() }

    single {
        BluetoothDeviceRepository(bluetoothProviderWrapper = get())
    }

    viewModel {
        CancelViewModel(
            transactionProvider = get(),
            cancelProvider = get()
        )
    }

    viewModel {
        DisplayMessageViewModel(
            displayProvider = get()
        )
    }

    viewModel {
        DevicesViewModel(
            bluetoothDeviceRepository = get()
        )
    }

    viewModel {
        MainViewModel()
    }

    viewModel {
        ManageStoneCodeViewModel(
            activationProvider = get(),
            merchantProvider = get()
        )
    }

    viewModel {
        TransactionViewModel(
            activationProviderWrapper = get(),
            bluetoothRepository = get(),
            deviceInfoProviderWrapper = get(),
            installmentProvider = get(),
            paymentProviderWrapper = get(),
        )
    }

    viewModel {
        TransactionListViewModel(
            transactionListProvider = get()
        )
    }

    viewModel {
        ValidationViewModel(
            activationProvider = get()
        )
    }

}
