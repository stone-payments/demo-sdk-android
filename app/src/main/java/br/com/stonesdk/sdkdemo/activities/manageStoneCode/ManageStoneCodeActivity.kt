package br.com.stonesdk.sdkdemo.activities.manageStoneCode

import android.os.Bundle
import android.util.Log
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import br.com.stonesdk.sdkdemo.FeatureFlag
import br.com.stonesdk.sdkdemo.R
import br.com.stonesdk.sdkdemo.databinding.ActivityManageStoneCodeBinding
import stone.application.interfaces.StoneCallbackInterface
import stone.providers.ActiveApplicationProvider
import stone.user.UserModel
import stone.utils.Stone

class ManageStoneCodeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageStoneCodeBinding

    private val userModelList: List<UserModel> = Stone.sessionApplication.userModelList
    private var stoneCodeListView: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (FeatureFlag.composeRefactorEnabled) {
            setContent {
                MaterialTheme {
                    ManageStoneCodeScreen()
                }
            }
        } else {
            onCreateConfig()
        }
    }

    private fun onCreateConfig() {
        binding = ActivityManageStoneCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.activateManageStoneCodeButton.setOnClickListener { activateStoneCodeButtonOnClickListener() }

        stoneCodeListView = binding.manageStoneCodeListView
        stoneCodeListView!!.adapter = populateStoneCodeListView()
        stoneCodeListView!!.onItemClickListener =
            OnItemClickListener { adapterView, view, position, l ->
                manageStoneCodeListViewOnItemClickListener(
                    position
                )
            }
    }

    override fun onResume() {
        super.onResume()
        populateStoneCodeListView()
    }

    private fun populateStoneCodeListView(): ArrayAdapter<*> {
        val stoneCodeAdapter =
            ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1)
        for (userModel in userModelList) {
            stoneCodeAdapter.add(userModel.stoneCode)
        }
        return stoneCodeAdapter
    }

    private fun activateStoneCodeButtonOnClickListener() {
        val stoneCodeEditText = findViewById<EditText>(R.id.insertManageStoneCodeEditText)
        val activeApplicationProvider = ActiveApplicationProvider(this@ManageStoneCodeActivity)
        activeApplicationProvider.dialogTitle = "Aguarde"
        activeApplicationProvider.dialogMessage = "Ativando..."

        activeApplicationProvider.connectionCallback = object : StoneCallbackInterface {
            override fun onSuccess() {
                stoneCodeListView!!.adapter = populateStoneCodeListView()
                (stoneCodeListView!!.adapter as ArrayAdapter<*>).notifyDataSetChanged()
                Toast.makeText(applicationContext, "Success", Toast.LENGTH_LONG).show()
            }

            override fun onError() {
                Toast.makeText(applicationContext, "Error", Toast.LENGTH_LONG).show()
                Log.e(
                    "ManageStoneCodeActivity",
                    "onError: " + activeApplicationProvider.listOfErrors
                )
            }
        }

        activeApplicationProvider.activate(stoneCodeEditText.text.toString())

        /*
          : example with list of stone codes :

            List<String> stoneCodeList = Arrays.asList("636852", "357894", "095632");
            activeApplicationProvider.activate(stoneCodeList);

         */
    }

    private fun manageStoneCodeListViewOnItemClickListener(position: Int) {
        val activeApplicationProvider = ActiveApplicationProvider(this@ManageStoneCodeActivity)
        activeApplicationProvider.dialogTitle = "Aguarde"
        activeApplicationProvider.dialogMessage = "Desativando..."

        activeApplicationProvider.connectionCallback = object : StoneCallbackInterface {
            override fun onSuccess() {
                stoneCodeListView!!.adapter = populateStoneCodeListView()
                (stoneCodeListView!!.adapter as ArrayAdapter<*>).notifyDataSetChanged()
                Toast.makeText(applicationContext, "Success", Toast.LENGTH_LONG).show()
            }

            override fun onError() {
                Toast.makeText(applicationContext, "Error", Toast.LENGTH_LONG).show()
                Log.e(
                    "ManageStoneCodeActivity",
                    "onError: " + activeApplicationProvider.listOfErrors
                )
            }
        }
        activeApplicationProvider.deactivate(userModelList[position].stoneCode)
    }
}
