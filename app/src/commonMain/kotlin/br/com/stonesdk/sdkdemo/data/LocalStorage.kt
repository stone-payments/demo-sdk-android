package br.com.stonesdk.sdkdemo.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.internal.synchronized
import okio.Path.Companion.toPath

// code from https://github.com/Atif-09/DataStoreKMPTemplate
// https://readmedium.com/en/https:/medium.com/@stevdza-san/the-correct-way-to-inject-an-android-context-in-a-kmp-project-83d9dda855cf

expect class LocalStorageCreator {
    fun createDataStore(): DataStore<Preferences>
}

private lateinit var dataStore: DataStore<Preferences>

@OptIn(InternalCoroutinesApi::class)
private val lock = SynchronizedObject()

@OptIn(InternalCoroutinesApi::class)
fun getDataStore(producePath: () -> String): DataStore<Preferences> =
    synchronized(lock) {
        if (::dataStore.isInitialized) {
            dataStore
        } else {
            PreferenceDataStoreFactory.createWithPath(produceFile = { producePath().toPath() })
                .also { dataStore = it }
        }
    }

internal const val DATA_STORE_FILE_NAME = "data_store_file_name.preferences_pb"

