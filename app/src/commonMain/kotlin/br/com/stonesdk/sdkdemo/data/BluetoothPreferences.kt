package br.com.stonesdk.sdkdemo.data

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import co.stone.posmobile.sdk.bluetooth.domain.model.BluetoothDevice
import kotlinx.coroutines.flow.first

class BluetoothPreferences(
    private val localStorageCreator: LocalStorageCreator
) {
    suspend fun savePreferences(
        bluetoothName: String, bluetoothAddress: String
    ) {
        localStorageCreator.createDataStore().edit { preferences ->
            preferences[stringPreferencesKey(KET_BT_NAME)] = bluetoothName
            preferences[stringPreferencesKey(KEY_BT_ADDRESS)] = bluetoothAddress
        }
    }

    suspend fun getPreferences(): BluetoothDevice? {
        val preference = localStorageCreator.createDataStore().data.first()

        val bluetoothName = preference[stringPreferencesKey(KET_BT_NAME)]
        val bluetoothAddress = preference[stringPreferencesKey(KEY_BT_ADDRESS)]

        return if (bluetoothName == null || bluetoothAddress == null) {
            null
        } else {
            BluetoothDevice(deviceName = bluetoothName, hardwareAddress = bluetoothAddress)
        }
    }

    companion object {
        private const val KET_BT_NAME = "bt_name"
        private const val KEY_BT_ADDRESS = "bt_address"
    }

}