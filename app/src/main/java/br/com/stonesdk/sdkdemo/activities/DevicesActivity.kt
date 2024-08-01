package br.com.stonesdk.sdkdemo.activities

import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.stonesdk.sdkdemo.R
import br.com.stonesdk.sdkdemo.databinding.ActivityDevicesBinding
import br.com.stonesdk.sdkdemo.databinding.ActivityMainBinding
import stone.application.interfaces.StoneCallbackInterface
import stone.providers.BluetoothConnectionProvider
import stone.utils.PinpadObject

class DevicesActivity : AppCompatActivity(), OnItemClickListener {

    private lateinit var binding: ActivityDevicesBinding

    private val mBluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private var btConnected = false
    private var listView: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDevicesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listView = binding.listDevicesActivity
        binding.listDevicesActivity.onItemClickListener = this
        turnBluetoothOn()
        listBluetoothDevices()
    }





    fun listBluetoothDevices() {
        // Lista de Pinpads para passar para o BluetoothConnectionProvider.

        val btArrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)

        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val pairedDevices = bluetoothAdapter.bondedDevices

        // Lista todos os dispositivos pareados.
        if (pairedDevices.size > 0) {
            for (device in pairedDevices) {
                btArrayAdapter.add(String.format("%s_%s", device.name, device.address))
            }
        }

        // Exibe todos os dispositivos da lista.
        listView!!.adapter = btArrayAdapter
    }

    fun turnBluetoothOn() {
        try {
            mBluetoothAdapter.enable()
            do {
            } while (!mBluetoothAdapter.isEnabled)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }









    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        // Pega o pinpad selecionado do ListView.

        val pinpadInfo = listView!!.adapter.getItem(position).toString().split("_".toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val pinpadSelected = PinpadObject(pinpadInfo[0], pinpadInfo[1], false)

        // Passa o pinpad selecionado para o provider de conexão bluetooth.
        val bluetoothConnectionProvider =
            BluetoothConnectionProvider(this@DevicesActivity, pinpadSelected)
        bluetoothConnectionProvider.dialogMessage =
            "Criando conexao com o pinpad selecionado" // Mensagem exibida do dialog.
        bluetoothConnectionProvider.connectionCallback = object : StoneCallbackInterface {
            override fun onSuccess() {
                Toast.makeText(applicationContext, "Pinpad conectado", Toast.LENGTH_SHORT).show()
                btConnected = true
                finish()
            }

            override fun onError() {
                Toast.makeText(
                    applicationContext,
                    "Erro durante a conexao. Verifique a lista de erros do provider para mais informacoes",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(
                    "DevicesActivity",
                    "onError: " + bluetoothConnectionProvider.listOfErrors
                )
            }
        }
        bluetoothConnectionProvider.execute() // Executa o provider de conexão bluetooth.
    }
}
