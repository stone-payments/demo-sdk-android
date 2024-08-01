package br.com.stonesdk.sdkdemo.activities

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import br.com.stonesdk.sdkdemo.R
import br.com.stonesdk.sdkdemo.databinding.ActivityMainBinding
import br.com.stonesdk.sdkdemo.databinding.ActivityValidationBinding
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnNeverAskAgain
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.OnShowRationale
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.RuntimePermissions
import stone.application.StoneStart.init
import stone.application.interfaces.StoneCallbackInterface
import stone.environment.Environment
import stone.providers.ActiveApplicationProvider
import stone.utils.Stone
import stone.utils.keys.StoneKeyType

@RuntimePermissions
class ValidationActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityValidationBinding

    private var stoneCodeEditText: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityValidationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ValidationActivityPermissionsDispatcher.initiateAppWithPermissionCheck(this)
        //        Stone.setEnvironment(SANDBOX);
        Stone.setAppName("DEMO APP") // Setando o nome do APP (obrigatorio)
        binding.activateButton.setOnClickListener(
            this
        )
        stoneCodeEditText = binding.stoneCodeEditText
        val environmentSpinner = binding.environmentSpinner

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item)
        for (env in Environment.entries) {
            adapter.add(env.name)
        }
        environmentSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                val environment = Environment.valueOf(
                    adapter.getItem(position)!!
                )
                //                Stone.setEnvironment(environment);
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
//                Stone.setEnvironment(PRODUCTION);
            }
        }
        environmentSpinner.adapter = adapter

        Stone.setAppName("Demo SDK")
    }

    override fun onClick(v: View) {
        val stoneCodeList: MutableList<String> = ArrayList()
        // Adicione seu Stonecode abaixo, como string.
        stoneCodeList.add(stoneCodeEditText!!.text.toString())

        val provider = ActiveApplicationProvider(this)
        provider.dialogMessage = "Ativando o aplicativo..."
        provider.dialogTitle = "Aguarde"
        provider.connectionCallback = object : StoneCallbackInterface {
            /* Metodo chamado se for executado sem erros */
            override fun onSuccess() {
                Toast.makeText(
                    this@ValidationActivity,
                    "Ativado com sucesso, iniciando o aplicativo",
                    Toast.LENGTH_SHORT
                ).show()
                continueApplication()
            }

            /* metodo chamado caso ocorra alguma excecao */
            override fun onError() {
                Toast.makeText(
                    this@ValidationActivity,
                    "Erro na ativacao do aplicativo, verifique a lista de erros do provider",
                    Toast.LENGTH_SHORT
                ).show()

                /* Chame o metodo abaixo para verificar a lista de erros. Para mais detalhes, leia a documentacao: */
                Log.e(
                    TAG,
                    "onError: " + provider.listOfErrors.toString()
                )
            }
        }
        provider.activate(stoneCodeList)
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun initiateApp() {
        val keys: MutableMap<StoneKeyType, String> = HashMap()
        keys[StoneKeyType.QRCODE_PROVIDERID] = "xxxx"
        keys[StoneKeyType.QRCODE_AUTHORIZATION] = "xxx"

        /**
         * Este deve ser, obrigatoriamente, o primeiro metodo
         * a ser chamado. E um metodo que trabalha com sessao.
         */
        val user = init(this, keys)

        // se retornar nulo, voce provavelmente nao ativou a SDK
        // ou as informacoes da Stone SDK foram excluidas
        if (user != null) {
            /* caso ja tenha as informacoes da SDK e chamado o ActiveApplicationProvider anteriormente
               sua aplicacao podera seguir o fluxo normal */
            continueApplication()
        }
    }

    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun showDenied() {
        buildPermissionDialog { dialog, which ->
            ValidationActivityPermissionsDispatcher.initiateAppWithPermissionCheck(
                this@ValidationActivity
            )
        }
    }

    @OnNeverAskAgain(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun showNeverAskAgain() {
        buildPermissionDialog { dialog, which ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
            intent.setData(uri)
            startActivityForResult(intent, REQUEST_PERMISSION_SETTINGS)
        }
    }

    private fun continueApplication() {
        val mainIntent = Intent(this@ValidationActivity, MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun showRationale(request: PermissionRequest) {
        buildPermissionDialog { dialog, which -> request.proceed() }
    }

    private fun buildPermissionDialog(listener: DialogInterface.OnClickListener) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Android 6.0")
            .setCancelable(false)
            .setMessage(
                "Com a versão do android igual ou superior ao Android 6.0," +
                        " é necessário que você aceite as permissões para o funcionamento do app.\n\n"
            )
            .setPositiveButton("OK", listener)
            .create().show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_SETTINGS) {
            ValidationActivityPermissionsDispatcher.initiateAppWithPermissionCheck(this)
        }
        ValidationActivityPermissionsDispatcher.onRequestPermissionsResult(
            this,
            requestCode,
            grantResults
        )
    }

    companion object {
        private const val TAG = "ValidationActivity"
        private const val REQUEST_PERMISSION_SETTINGS = 100
    }
}

