package br.com.stonesdk.sdkdemo.di

import br.com.stonesdk.sdkdemo.data.BluetoothPreferences
import br.com.stonesdk.sdkdemo.routes.NavigationManager
import br.com.stonesdk.sdkdemo.ui.cancel_transactions.CancelViewModel
import br.com.stonesdk.sdkdemo.ui.display.DisplayMessageViewModel
import br.com.stonesdk.sdkdemo.ui.main.MainViewModel
import br.com.stonesdk.sdkdemo.ui.manage_stone_codes.ManageStoneCodeViewModel
import br.com.stonesdk.sdkdemo.ui.paired_devices.BluetoothDeviceRepository
import br.com.stonesdk.sdkdemo.ui.paired_devices.DevicesViewModel
import br.com.stonesdk.sdkdemo.ui.splashscreen.ValidationViewModel
import br.com.stonesdk.sdkdemo.ui.transactionList.TransactionListViewModel
import br.com.stonesdk.sdkdemo.ui.transactions.TransactionViewModel
import br.com.stonesdk.sdkdemo.utils.AppInfo
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
import co.stone.posmobile.lib.commons.platform.PlatformContext
import co.stone.posmobile.sdk.callback.StoneResultCallback
import co.stone.posmobile.sdk.merchant.domain.model.Merchant
import co.stone.posmobile.sdk.stoneStart.provider.StoneStart
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

expect val targetModule: Module

val commonModule: Module = module {
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
        BluetoothPreferences(
            localStorageCreator = get()
        )
    }

    single {
        BluetoothDeviceRepository(
            bluetoothProviderWrapper = get(),
            bluetoothPreferences = get()
        )
    }

    single {
        NavigationManager()
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
            navigationManager = get(),
            bluetoothDeviceRepository = get()
        )
    }

    viewModel {
        MainViewModel(
            navigationManager = get()
        )
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
            transactionProvider = get(),
            emailProviderWrapper = get(),
        )
    }

    viewModel {
        ValidationViewModel(
            activationProvider = get(),
            navigationManager = get(),
        )
    }
}

fun initializeKoin(
    platformContext: PlatformContext,
    appInfo: AppInfo,
    config: (KoinApplication.() -> Unit)? = null,
) {
    startKoin {
        config?.invoke(this)
        modules(targetModule, commonModule)

        StoneStart.init(
            context = platformContext,
            appName = appInfo.appName,
            appVersion = appInfo.appVersion,
            packageName = appInfo.packageName,
            environment = StoneStart.StoneEnvironment.CERTIFICATION,
            callback = object : StoneResultCallback<List<Merchant>> {
                override fun onSuccess(result: List<Merchant>) {

                }

                override fun onError(
                    stoneStatus: br.com.stone.sdk.android.error.StoneStatus?,
                    throwable: Throwable
                ) {

                }
            }
        )

    }
}