package br.com.stonesdk.sdkdemo

import br.com.stonesdk.sdkdemo.activities.manageStoneCode.ManageStoneCodeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import stone.application.SessionApplication
import stone.providers.ActiveApplicationProvider
import stone.utils.Stone

val demoApplicationModule = module {


    factory<SessionApplication> {
        Stone.sessionApplication
    }

    factory<ActiveApplicationProvider> {
        ActiveApplicationProvider(get())
    }

    viewModel {
        ManageStoneCodeViewModel(
            get(), get()
        )
    }
}