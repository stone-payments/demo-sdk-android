package br.com.stonesdk.sdkdemo.activities;

import static android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS;
import static br.com.stonesdk.sdkdemo.activities.ValidationActivityPermissionsDispatcher.initiateAppWithPermissionCheck;
import static stone.environment.Environment.valueOf;

import android.Manifest;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import br.com.stonesdk.sdkdemo.R;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import stone.application.StoneStart;
import stone.application.interfaces.StoneCallbackInterface;
import stone.environment.Environment;
import stone.providers.ActiveApplicationProvider;
import stone.user.UserModel;
import stone.utils.Stone;

@RuntimePermissions
public class ValidationActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ValidationActivity";
    private static final int REQUEST_PERMISSION_SETTINGS = 100;
    private EditText stoneCodeEditText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validation);
        initiateAppWithPermissionCheck(this);
//        Stone.setEnvironment(SANDBOX);
        Stone.setAppName("DEMO APP"); // Setando o nome do APP (obrigatorio)
        findViewById(R.id.activateButton).setOnClickListener(this);
        stoneCodeEditText = findViewById(R.id.stoneCodeEditText);
        Spinner environmentSpinner = findViewById(R.id.environmentSpinner);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        for (Environment env : Environment.values()) {
            adapter.add(env.name());
        }
        environmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Environment environment = valueOf(adapter.getItem(position));
//                Stone.setEnvironment(environment);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//                Stone.setEnvironment(PRODUCTION);
            }
        });
        environmentSpinner.setAdapter(adapter);

        Stone.setAppName("Demo SDK");

    }

    @Override
    public void onClick(View v) {
        List<String> stoneCodeList = new ArrayList<>();
        // Adicione seu Stonecode abaixo, como string.
        stoneCodeList.add(stoneCodeEditText.getText().toString());

        final ActiveApplicationProvider provider = new ActiveApplicationProvider(this);
        provider.setDialogMessage("Ativando o aplicativo...");
        provider.setDialogTitle("Aguarde");
        provider.useDefaultUI(false);
        provider.setConnectionCallback(new StoneCallbackInterface() {
            /* Metodo chamado se for executado sem erros */
            public void onSuccess() {
                Toast.makeText(ValidationActivity.this, "Ativado com sucesso, iniciando o aplicativo", Toast.LENGTH_SHORT).show();
                continueApplication();
            }

            /* metodo chamado caso ocorra alguma excecao */
            public void onError() {
                Toast.makeText(ValidationActivity.this, "Erro na ativacao do aplicativo, verifique a lista de erros do provider", Toast.LENGTH_SHORT).show();

                /* Chame o metodo abaixo para verificar a lista de erros. Para mais detalhes, leia a documentacao: */
                Log.e(TAG, "onError: " + provider.getListOfErrors().toString());

            }
        });
        provider.activate(stoneCodeList);
    }

    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE})
    public void initiateApp() {
        /**
         * Este deve ser, obrigatoriamente, o primeiro metodo
         * a ser chamado. E um metodo que trabalha com sessao.
         */
        List<UserModel> user = StoneStart.init(this);

        // se retornar nulo, voce provavelmente nao ativou a SDK
        // ou as informacoes da Stone SDK foram excluidas
        if (user != null) {
            /* caso ja tenha as informacoes da SDK e chamado o ActiveApplicationProvider anteriormente
               sua aplicacao podera seguir o fluxo normal */
            continueApplication();

        }
    }

    @OnPermissionDenied({Manifest.permission.READ_EXTERNAL_STORAGE})
    void showDenied() {
        buildPermissionDialog(new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                initiateAppWithPermissionCheck(ValidationActivity.this);
            }
        });
    }

    @OnNeverAskAgain({Manifest.permission.READ_EXTERNAL_STORAGE})
    void showNeverAskAgain() {
        buildPermissionDialog(new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, REQUEST_PERMISSION_SETTINGS);
            }
        });
    }

    private void continueApplication() {
        Intent mainIntent = new Intent(ValidationActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    @OnShowRationale({Manifest.permission.READ_EXTERNAL_STORAGE})
    void showRationale(final PermissionRequest request) {
        buildPermissionDialog(new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                request.proceed();
            }
        });
    }

    private void buildPermissionDialog(OnClickListener listener) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Android 6.0")
                .setCancelable(false)
                .setMessage("Com a versão do android igual ou superior ao Android 6.0," +
                        " é necessário que você aceite as permissões para o funcionamento do app.\n\n")
                .setPositiveButton("OK", listener)
                .create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_SETTINGS) {
            initiateAppWithPermissionCheck(this);
        }
        ValidationActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}

