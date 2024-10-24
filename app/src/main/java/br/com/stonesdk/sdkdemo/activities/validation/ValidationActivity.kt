package br.com.stonesdk.sdkdemo.activities.validation

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.core.content.ContextCompat.checkSelfPermission
import br.com.stonesdk.sdkdemo.FeatureFlag
import br.com.stonesdk.sdkdemo.activities.MainActivity
import br.com.stonesdk.sdkdemo.databinding.ActivityValidationBinding
import permissions.dispatcher.RuntimePermissions
import stone.application.StoneStart.init
import stone.application.interfaces.StoneCallbackInterface
import stone.environment.Environment
import stone.providers.ActiveApplicationProvider
import stone.utils.Stone
import stone.utils.keys.StoneKeyType
import java.util.EnumMap

@RuntimePermissions
class ValidationActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityValidationBinding

    private var stoneCodeEditText: EditText? = null
    private var requestPermissionLauncher: ActivityResultLauncher<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (FeatureFlag.composeRefactorEnabled) {
            setContent {
                MaterialTheme {
                    ValidationScreen(
                        navigateToMain = ::continueApplication
                    )
                }
            }
        } else {
            onCreateStart()
        }
    }

    private fun onCreateStart() {
        binding = ActivityValidationBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissionLauncher =
                registerForActivityResult(RequestPermission()) { isGranted: Boolean ->
                    if (isGranted) {
                        initiateApp()
                    } else {
                        showDenied()
                    }
                }

            initiateAppWithPermissionCheck()
        } else {
            initiateApp()
        }

        //        Stone.setEnvironment(SANDBOX);
        binding.activateButton.setOnClickListener(this)
        stoneCodeEditText = binding.stoneCodeEditText
        val environmentSpinner = binding.environmentSpinner

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item)
        for (env in Environment.entries) {
            adapter.add(env.name)
        }
        environmentSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    //val environment = Environment.valueOf(adapter.getItem(position)!!)
                    //                Stone.setEnvironment(environment);
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
//                Stone.setEnvironment(PRODUCTION);
                }
            }
        environmentSpinner.adapter = adapter
    }


    override fun onClick(v: View) {
        val stoneCodeList = listOf(stoneCodeEditText?.text.toString())
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
                    "onError: ${provider.listOfErrors}"
                )
            }
        }
        provider.activate(stoneCodeList)
    }

    private fun initiateApp() {
        val keys: MutableMap<StoneKeyType, String> = EnumMap(StoneKeyType::class.java)
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

    @RequiresApi(Build.VERSION_CODES.M)
    fun showDenied() {
        buildPermissionToast()
        initiateAppWithPermissionCheck()
    }

    private fun continueApplication() {
        val mainIntent = Intent(this@ValidationActivity, MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initiateAppWithPermissionCheck() {
        when {
            checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED -> {
                initiateApp()
            }

            shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE) -> {
                buildPermissionToast()
                requestPermissionLauncher?.launch(READ_EXTERNAL_STORAGE)
            }

            else -> requestPermissionLauncher?.launch(READ_EXTERNAL_STORAGE)
        }
    }

    private var hasShownToast = false

    private fun buildPermissionToast() {

        if (!hasShownToast) {
            Toast.makeText(this, "Android 6 or more needs to give permission", Toast.LENGTH_SHORT)
                .show()
            hasShownToast = true

        }

    }

    companion object {
        private const val TAG = "ValidationActivity"
    }
}
