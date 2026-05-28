package br.com.stonesdk.sdkdemo.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import co.stone.posmobile.lib.commons.platform.PlatformContext
import kotlinx.coroutines.runBlocking

actual class LocalStorageCreator(
    private val applicationContext: PlatformContext
) {
    actual fun createDataStore(): DataStore<Preferences> {
        return runBlocking {
            getDataStore(producePath = { applicationContext.filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath })
        }
    }

}