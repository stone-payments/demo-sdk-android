package br.com.stonesdk.sdkdemo.activities

import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import br.com.stone.posandroid.hal.api.mifare.MifareKeyType
import br.com.stone.posandroid.providers.PosMifareProvider
import br.com.stonesdk.sdkdemo.R
import br.com.stonesdk.sdkdemo.databinding.ActivityMifareBinding
import br.com.stonesdk.sdkdemo.databinding.DualInputDialogBinding
import br.com.stonesdk.sdkdemo.databinding.InputDialogBinding
import stone.application.interfaces.StoneCallbackInterface

/**
 * https://sdkandroid.stone.com.br/reference/provider-de-mifare
 */
class MifareActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMifareBinding

    private var logTextView: TextView? = null
    private var mifareProvider: PosMifareProvider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMifareBinding.inflate(layoutInflater)
        setContentView(binding.root)

        logTextView = binding.logTextView
        setCardUUIDText("")
    }


    override fun onStop() {
        super.onStop()
        if (mifareProvider != null) mifareProvider!!.cancelDetection()
    }

    /**
     * Exemplo de detecção de cartão, pegando o UUID.
     */
    fun detectCard(view: View?) {
        mifareProvider = PosMifareProvider(applicationContext)

        mifareProvider!!.connectionCallback = object : StoneCallbackInterface {
            override fun onSuccess() {
                setCardUUIDText(mifareProvider!!.cardUUID.contentToString())
                logTextView!!.append("Cartão detectado: " + mifareProvider!!.cardUUID.contentToString() + "\n")
            }

            override fun onError() {
                runOnUiThread {
                    Toast.makeText(
                        this@MifareActivity,
                        "Erro na detecção",
                        Toast.LENGTH_SHORT
                    ).show()
                    logTextView!!.append(mifareProvider!!.listOfErrors.toString() + "\n")
                }
            }
        }
        mifareProvider!!.execute()
    }

    fun cancelDetectCard(view: View?) {
        if (mifareProvider != null) mifareProvider!!.cancelDetection()
    }

    /**
     * Exemplo de leitura do valor de um bloco.
     * @param sector Número do setor.
     * @param block Número do bloco relativo ao setor (mínimo 0, máximo 3).
     * @param key Chave de autenticação do bloco.
     */
    private fun executeBlockRead(sector: Int, block: Int, key: ByteArray) {
        val mifareProvider = PosMifareProvider(applicationContext)
        mifareProvider.connectionCallback = object : StoneCallbackInterface {
            override fun onSuccess() {
                setCardUUIDText(mifareProvider.cardUUID.contentToString())
                logTextView!!.append("Cartão detectado: " + mifareProvider.cardUUID.contentToString() + "\n")

                // Autentica o setor
                try {
                    mifareProvider.authenticateSector(MifareKeyType.TypeA, key, sector.toByte())
                } catch (e: PosMifareProvider.MifareException) {
                    Toast.makeText(this@MifareActivity, "Erro na autenticação", Toast.LENGTH_SHORT)
                        .show()
                    logTextView!!.append(mifareProvider.listOfErrors.toString() + "\n")
                    return
                }

                // Lê o valor de um bloco no setor
                // O valor lido será escrito no byteArray caso a leitura ocorra com sucesso
                val byteArray = ByteArray(16)
                try {
                    mifareProvider.readBlock(sector.toByte(), block.toByte(), byteArray)
                } catch (e: PosMifareProvider.MifareException) {
                    Toast.makeText(
                        this@MifareActivity,
                        "Erro na leitura do bloco",
                        Toast.LENGTH_SHORT
                    ).show()
                    logTextView!!.append(e.errorEnum.name + "\n")
                    return
                }

                Toast.makeText(
                    this@MifareActivity,
                    "Valor lido: " + byteArray.contentToString(),
                    Toast.LENGTH_SHORT
                ).show()
                logTextView!!.append("Valor lido: " + String(byteArray) + "\n")
            }

            override fun onError() {
                runOnUiThread {
                    Toast.makeText(this@MifareActivity, "Erro na detecção", Toast.LENGTH_SHORT)
                        .show()
                    logTextView!!.append(mifareProvider.listOfErrors.toString() + "\n")
                }
            }
        }

        mifareProvider.execute()
    }

    /**
     * Exemplo de escrita em um bloco.
     * @param sector Número do setor.
     * @param block Número do bloco relativo ao setor (mínimo 0, máximo 3).
     * @param key Chave de autenticação do bloco.
     * @param value Valor que será gravado no bloco. (byte[16]).
     */
    private fun executeBlockWrite(sector: Int, block: Int, key: ByteArray, value: ByteArray) {
        val mifareProvider = PosMifareProvider(applicationContext)
        mifareProvider.connectionCallback = object : StoneCallbackInterface {
            override fun onSuccess() {
                setCardUUIDText(mifareProvider.cardUUID.contentToString())
                logTextView!!.append("Cartão detectado: " + mifareProvider.cardUUID.contentToString() + "\n")

                // Autentica o setor
                try {
                    mifareProvider.authenticateSector(MifareKeyType.TypeA, key, sector.toByte())
                } catch (e: PosMifareProvider.MifareException) {
                    Toast.makeText(this@MifareActivity, "Erro na autenticação", Toast.LENGTH_SHORT)
                        .show()
                    logTextView!!.append(mifareProvider.listOfErrors.toString() + "\n")
                    return
                }

                // Lê o valor de um bloco no setor
                try {
                    mifareProvider.writeBlock(sector.toByte(), block.toByte(), value)
                } catch (e: PosMifareProvider.MifareException) {
                    Toast.makeText(
                        this@MifareActivity,
                        "Erro na escrita do bloco",
                        Toast.LENGTH_SHORT
                    ).show()
                    logTextView!!.append(e.errorEnum.name + "\n")
                    return
                }

                Toast.makeText(this@MifareActivity, "Sucesso", Toast.LENGTH_SHORT).show()
                logTextView!!.append("Valor gravado: " + String(value) + "\n")
            }

            override fun onError() {
                runOnUiThread {
                    Toast.makeText(this@MifareActivity, "Erro na detecção", Toast.LENGTH_SHORT)
                        .show()
                    logTextView!!.append(mifareProvider.listOfErrors.toString() + "\n")
                }
            }
        }

        mifareProvider.execute()
    }


    fun readBlockDialog(view: View?) {

        val binding = DualInputDialogBinding.inflate(layoutInflater)

        val keyDialogBinding = InputDialogBinding.inflate(layoutInflater)

        binding.apply {
            editText.inputType = InputType.TYPE_CLASS_NUMBER
            editText2.inputType = InputType.TYPE_CLASS_NUMBER
            editTextLabel.text = getString(R.string.sector_label)
            editTextLabel2.text = getString(R.string.block_label)
        }

        keyDialogBinding.editText.setText(R.string.default_key_text)


        val keyDialog = AlertDialog.Builder(this)
            .setView(keyDialogBinding.root)
            .setTitle("Chave do setor")
            .setPositiveButton("OK") { _, _ ->
                try {
                    val sector = binding.editText.text.toString().toInt()
                    val block = binding.editText2.text.toString().toInt()
                    val key = hexStringToByteArray(keyDialogBinding.editText.text.toString())
                    executeBlockRead(sector, block, key)
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancelar") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()

        AlertDialog.Builder(this)
            .setView(binding.root)
            .setTitle("Definir bloco")
            .setPositiveButton("OK") { _, _ ->
                try {
                    keyDialog.show()
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancelar") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }


//    fun readBlockDialog(view: View?) {
//        // Olhe o método executeReadBlock para ver a implementação do provider
//
//        val dialogView = layoutInflater.inflate(R.layout.dual_input_dialog, null)
//        val keyDialogView = layoutInflater.inflate(R.layout.input_dialog, null)
//
//
//        val binding = DualInputDialogBinding.inflate(layoutInflater)
//
//        val label1 = binding.editTextLabel
//        val label2 = binding.editTextLabel2
//        val sectorTextView = binding.editText
//        val blockTextView = binding.editText2
//        val keyDialogTextView = keyDialogView.findViewById<TextView>(R.id.editText)
//        sectorTextView.inputType = InputType.TYPE_CLASS_NUMBER
//        blockTextView.inputType = InputType.TYPE_CLASS_NUMBER
//        keyDialogTextView.text = "FFFFFFFFFFFF"
//        label1.text = "Sector"
//        label2.text = "Block"
//
//        val keyDialog = AlertDialog.Builder(this)
//            .setView(keyDialogView)
//            .setTitle("Chave do setor")
//            .setPositiveButton("OK") { dialogInterface: DialogInterface?, i: Int ->
//                try {
//                    val sector = sectorTextView.text.toString().toInt()
//                    val block = blockTextView.text.toString().toInt()
//                    val key = hexStringToByteArray(keyDialogTextView.text.toString())
//                    executeBlockRead(sector, block, key)
//                } catch (e: NumberFormatException) {
//                }
//            }
//            .setNegativeButton("Cancelar") { dialogInterface: DialogInterface?, i: Int -> }.create()
//
//        AlertDialog.Builder(this)
//            .setView(dialogView)
//            .setTitle("Definir bloco")
//            .setPositiveButton("OK") { dialogInterface: DialogInterface?, i: Int ->
//                try {
//                    keyDialog.show()
//                } catch (e: NumberFormatException) {
//                }
//            }
//            .setNegativeButton("Cancelar") { dialogInterface: DialogInterface?, i: Int -> }
//            .show()
//    }


    fun writeCardDialog(view: View?) {
        val dialogView = layoutInflater.inflate(R.layout.dual_input_dialog, null)
        val label1 = dialogView.findViewById<TextView>(R.id.editTextLabel)
        val label2 = dialogView.findViewById<TextView>(R.id.editTextLabel2)
        val sectorTextView = dialogView.findViewById<TextView>(R.id.editText)
        val blockTextView = dialogView.findViewById<TextView>(R.id.editText2)
        sectorTextView.inputType = InputType.TYPE_CLASS_NUMBER
        blockTextView.inputType = InputType.TYPE_CLASS_NUMBER
        label1.text = "Sector"
        label2.text = "Block"

        val keyDialogView = layoutInflater.inflate(R.layout.input_dialog, null)
        val keyDialogTextView = keyDialogView.findViewById<TextView>(R.id.editText)
        keyDialogTextView.inputType = InputType.TYPE_CLASS_NUMBER
        keyDialogTextView.text = "FFFFFFFFFFFF"

        val valueDialogView = layoutInflater.inflate(R.layout.input_dialog, null)
        val valueDialogTextView = valueDialogView.findViewById<TextView>(R.id.editText)


        val valueDialog = AlertDialog.Builder(this)
            .setView(valueDialogView)
            .setTitle("Valor que será escrito")
            .setPositiveButton("OK") { _: DialogInterface?, _: Int ->
                try {
                    val sector = sectorTextView.text.toString().toInt()
                    val block = blockTextView.text.toString().toInt()
                    val key = hexStringToByteArray(keyDialogTextView.text.toString())
                    // Valor que será escrito precisa ter 16 bytes
                    val value =
                        String.format("%-16s", valueDialogTextView.text.toString()).toByteArray()
                    executeBlockWrite(sector, block, key, value)
                } catch (e: NumberFormatException) {
                }
            }.setNegativeButton("Cancelar") { _: DialogInterface?, _: Int -> }
            .create()


        val keyDialog = AlertDialog.Builder(this)
            .setView(keyDialogView)
            .setTitle("Chave do setor")
            .setPositiveButton("OK") { _: DialogInterface?, _: Int ->
                try {
                    val sector = sectorTextView.text.toString().toInt()
                    val block = blockTextView.text.toString().toInt()
                    val key = hexStringToByteArray(keyDialogTextView.text.toString())
                    valueDialog.show()
                } catch (e: NumberFormatException) {
                }
            }
            .setNegativeButton("Cancelar") { _: DialogInterface?, _: Int -> }.create()

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Definir bloco")
            .setPositiveButton("OK") { _: DialogInterface?, _: Int ->
                try {
                    keyDialog.show()
                } catch (e: NumberFormatException) {
                }
            }
            .setNegativeButton("Cancelar") { _: DialogInterface?, _: Int -> }
            .show()
    }


    private fun setCardUUIDText(uuid: String) {
        val textView = findViewById<TextView>(R.id.card_uuid_value)
        val uuidText = getString(R.string.uuid_s, uuid)
        textView.text = uuidText
    }

    companion object {
        fun hexStringToByteArray(s: String): ByteArray {
            val len = s.length
            val data = ByteArray(len / 2)
            var i = 0
            while (i < len) {
                data[i / 2] = ((((s[i].digitToIntOrNull(16)
                    ?: (-1 shl 4)) + s[i + 1].digitToIntOrNull(16)!!) ?: -1)).toByte()
                i += 2
            }
            return data
        }
    }
}