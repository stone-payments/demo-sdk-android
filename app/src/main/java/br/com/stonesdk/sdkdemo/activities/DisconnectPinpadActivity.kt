package br.com.stonesdk.sdkdemo.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import br.com.stonesdk.sdkdemo.databinding.ActivityDisconnectPinpadBinding
import stone.utils.Stone

class DisconnectPinpadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDisconnectPinpadBinding
    
    var pinpadsSpinner: Spinner? = null
    var disconnectButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        
        
        super.onCreate(savedInstanceState)
        binding = ActivityDisconnectPinpadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pinpadsSpinner = binding.pinpadsSpinner
        disconnectButton = binding.disconnectButton

        setPinpadsToSpinner()

        binding.disconnectButton.setOnClickListener {
            val pinpadSelected = binding.pinpadsSpinner.selectedItemPosition
            Stone.removePinpadAtIndex(Stone.getPinpadFromListAt(pinpadSelected))
            setPinpadsToSpinner()
        }
    }

    private fun setPinpadsToSpinner() {
        val pinpads: MutableList<String> = ArrayList()
        for (i in 0 until Stone.getPinpadListSize()) pinpads.add(Stone.getPinpadFromListAt(i).name)


        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, pinpads)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        pinpadsSpinner!!.adapter = adapter
    }
}
