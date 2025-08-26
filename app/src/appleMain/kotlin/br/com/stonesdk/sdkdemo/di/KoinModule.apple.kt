package br.com.stonesdk.sdkdemo.di

import br.com.stonesdk.sdkdemo.data.LocalStorageCreator
import org.koin.core.module.Module
import org.koin.dsl.module

actual val targetModule: Module = module {

    single { LocalStorageCreator() }

}
