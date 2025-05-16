package br.com.stonesdk.sdkdemo.utils

object PersistBTState {
    private const val BT_STATE_SHARED_NAME = "BTState"
    private const val KET_BT_NAME = "bt_name"
    private const val KEY_BT_ADDRESS = "bt_address"

    fun saveBTState(bluetoothName: String, bluetoothAddress: String) {
//        val prefs: SharedPreferences = context.getSharedPreferences(BT_STATE_SHARED_NAME, Context.MODE_PRIVATE)
//        prefs.edit {
//            putString(KET_BT_NAME, bluetoothName)
//            putString(KEY_BT_ADDRESS, bluetoothAddress)
//        }
    }

    fun getBTState(): BluetoothState {
//        val prefs: SharedPreferences = context.getSharedPreferences(BT_STATE_SHARED_NAME, Context.MODE_PRIVATE)
//        val name = prefs.getString(KET_BT_NAME, null)
//        val address = prefs.getString(KEY_BT_ADDRESS, null)
//        if (name == null || address == null) {
//            return BluetoothState(null, null)
//        }
        return BluetoothState(name = "name", address = "address")
    }

}

data class BluetoothState(
    val name: String?,
    val address: String?,
)
