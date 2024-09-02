package br.com.stonesdk.sdkdemo.activities.validation

import android.content.Context
import stone.application.StoneStart.init
import stone.utils.keys.StoneKeyType
import java.util.EnumMap

class AppInitializer(
    private val context: Context
) {
     fun initiateApp(): Boolean {
        val keys: MutableMap<StoneKeyType, String> = EnumMap(StoneKeyType::class.java)
        keys[StoneKeyType.QRCODE_PROVIDERID] = "xxxx"
        keys[StoneKeyType.QRCODE_AUTHORIZATION] = "xxx"

        /**
         * Este deve ser, obrigatoriamente, o primeiro metodo
         * a ser chamado. E um metodo que trabalha com sessao.
         */
        val user = init(context, keys)

        return user != null
    }
}