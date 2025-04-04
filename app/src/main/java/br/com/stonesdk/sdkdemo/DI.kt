package br.com.stonesdk.sdkdemo

import android.bluetooth.BluetoothAdapter
import br.com.stonesdk.sdkdemo.activities.devices.BluetoothProviderWrapper
import br.com.stonesdk.sdkdemo.activities.devices.DeviceInfoProviderWrapper
import br.com.stonesdk.sdkdemo.activities.devices.DevicesViewModel
import br.com.stonesdk.sdkdemo.activities.display.DisplayMessageProviderWrapper
import br.com.stonesdk.sdkdemo.activities.display.DisplayMessageViewModel
import br.com.stonesdk.sdkdemo.activities.main.MainViewModel
import br.com.stonesdk.sdkdemo.activities.manageStoneCode.ActivationProviderWrapper
import br.com.stonesdk.sdkdemo.activities.manageStoneCode.ManageStoneCodeViewModel
import br.com.stonesdk.sdkdemo.activities.transaction.InstallmentProvider
import br.com.stonesdk.sdkdemo.activities.transaction.PaymentProviderWrapper
import br.com.stonesdk.sdkdemo.activities.transaction.ReversalProviderWrapper
import br.com.stonesdk.sdkdemo.activities.transaction.TransactionViewModel
import br.com.stonesdk.sdkdemo.activities.transaction.list.TransactionListProviderWrapper
import br.com.stonesdk.sdkdemo.activities.transaction.list.TransactionListViewModel
import br.com.stonesdk.sdkdemo.activities.transaction.revert.TransactionRevertViewModel
import br.com.stonesdk.sdkdemo.activities.validation.ValidationViewModel
import co.stone.posmobile.sdk.bluetooth.provider.BluetoothProvider
import co.stone.posmobile.sdk.payment.provider.PaymentProvider
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val demoApplicationModule =
    module {

        factory {
            ActivationProviderWrapper()
        }

        factory {
            BluetoothProviderWrapper()
        }

        factory {
            DeviceInfoProviderWrapper()
        }

        factory {
            DisplayMessageProviderWrapper()
        }

        factory {
            InstallmentProvider()
        }

        factory {
            PaymentProviderWrapper()
        }

        factory {
            ReversalProviderWrapper()
        }

        factory {
            TransactionListProviderWrapper()
        }

        factory<BluetoothAdapter> {
            BluetoothAdapter.getDefaultAdapter()
        }

        single<BluetoothProvider> {
            BluetoothProvider.create()
        }

        single<PaymentProvider> {
            PaymentProvider.create()
        }

        viewModel {
            ManageStoneCodeViewModel(activationProviderWrapper = get())
        }
        viewModel {
            ValidationViewModel(activationProviderWrapper = get())
        }
        viewModel {
            DevicesViewModel(bluetoothProviderWrapper = get(), context = androidApplication())
        }
        viewModel {
            DisplayMessageViewModel(displayMessageProviderWrapper = get())
        }

        viewModel {
            TransactionViewModel(
                activationProviderWrapper = get(),
                deviceInfoProviderWrapper = get(),
                installmentProvider = get(),
                paymentProviderWrapper = get(),
                context = androidApplication(),
            )
        }

        viewModel {
            MainViewModel(
                deviceInfoProviderWrapper = get(),
            )
        }

        viewModel {
            TransactionListViewModel(
                transactionProvider = get(),
            )
        }

        viewModel {
            TransactionRevertViewModel(
                reversalProviderWrapper = get(),
            )
        }
    }
