package br.com.stonesdk.sdkdemo.utils

import android.content.Context
import android.content.SharedPreferences

object PersistBTState {

    private const val BTState = "BTState"
    private const val KET_BT_NAME = "bt_name"
    private const val KEY_BT_ADDRESS = "bt_address"

    fun saveBTState(context: Context, stringOne: String, stringTwo: String) {
        val prefs: SharedPreferences = context.getSharedPreferences(BTState, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putString(KET_BT_NAME, stringOne)
        editor.putString(KEY_BT_ADDRESS, stringTwo)
        editor.apply()
    }

    fun getBTState(context: Context): Pair<String?, String?> {
        val prefs: SharedPreferences = context.getSharedPreferences(BTState, Context.MODE_PRIVATE)
        val stringOne = prefs.getString(KET_BT_NAME, null)
        val stringTwo = prefs.getString(KEY_BT_ADDRESS, null)
        return Pair(stringOne, stringTwo)
    }
}