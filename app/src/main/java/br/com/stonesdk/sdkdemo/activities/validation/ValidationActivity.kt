package br.com.stonesdk.sdkdemo.activities.validation

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import br.com.stonesdk.sdkdemo.activities.MainActivity
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
class ValidationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                ValidationScreen(
                    navigateToMain = ::navigateToMain,
                    navigateToActivation = ::navigateToActivation
                )
            }
        }
    }


    private fun navigateToMain() {
        val mainIntent = Intent(this@ValidationActivity, MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }

    private fun navigateToActivation() {
        val mainIntent = Intent(this@ValidationActivity, MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }

}
