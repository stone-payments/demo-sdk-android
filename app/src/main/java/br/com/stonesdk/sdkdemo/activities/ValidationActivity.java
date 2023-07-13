package br.com.stonesdk.sdkdemo.activities;

import static android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS;
import static br.com.stonesdk.sdkdemo.activities.ValidationActivityPermissionsDispatcher.initiateAppWithPermissionCheck;

import android.Manifest;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import br.com.stone.sdk.activation.providers.ActiveApplicationProvider;
import br.com.stone.sdk.android.error.StoneStatus;
import br.com.stone.sdk.android.error.sdk.StoneSDKException;
import br.com.stone.sdk.core.application.StoneStart;
import br.com.stone.sdk.core.enums.ErrorsEnum;
import br.com.stone.sdk.core.environment.Environment;
import br.com.stone.sdk.core.model.user.UserModel;
import br.com.stone.sdk.core.providers.interfaces.StoneCallbackInterface;
import br.com.stone.sdk.core.utils.Stone;
import br.com.stonesdk.sdkdemo.R;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;


@RuntimePermissions
public class ValidationActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ValidationActivity";
    private static final int REQUEST_PERMISSION_SETTINGS = 100;
    private EditText stoneCodeEditText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validation);

        initiateAppWithPermissionCheck(this);

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
                Environment environment = Environment.valueOf(adapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        environmentSpinner.setAdapter(adapter);

        Stone.setAppName("Demo SDK");
    }

    @Override
    public void onClick(View v) {
        String stoneCode = stoneCodeEditText.getText().toString();
        final ActiveApplicationProvider provider = new ActiveApplicationProvider(this);

        provider.activate(stoneCode, new StoneCallbackInterface() {
            public void onSuccess() {
                Toast.makeText(ValidationActivity.this, "Ativado com sucesso, iniciando o aplicativo", Toast.LENGTH_SHORT).show();
                continueApplication();
            }

            public void onError(@Nullable StoneStatus stoneStatus) {
                if (stoneStatus != null && stoneStatus.getCode().equals("305")) {
                    Toast.makeText(ValidationActivity.this, "Terminal já está ativado", Toast.LENGTH_SHORT).show();
                    continueApplication();
                } else {
                    Toast.makeText(ValidationActivity.this, "Erro na ativacao do aplicativo, verifique a mensagem do Stone Status", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE})
    public void initiateApp() {
        StoneStart.init(this, "SDK Demo", new StoneStart.StoneStartCallback() {
            @Override
            public void onSuccess(@NonNull List<UserModel> userModelList) {
                if (!userModelList.isEmpty()) {
                    continueApplication();
                }
            }

            @Override
            public void onError(@NonNull StoneSDKException error) {
                Toast.makeText(ValidationActivity.this, "Erro ao inicializar a SDK", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onError: " + error);
            }
        });
    }

    @OnPermissionDenied({Manifest.permission.READ_EXTERNAL_STORAGE})
    void showDenied() {
        buildPermissionDialog((dialog, which) -> initiateAppWithPermissionCheck(ValidationActivity.this));
    }

    @OnNeverAskAgain({Manifest.permission.READ_EXTERNAL_STORAGE})
    void showNeverAskAgain() {
        buildPermissionDialog((dialog, which) -> {
            Intent intent = new Intent(ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, REQUEST_PERMISSION_SETTINGS);
        });
    }

    private void continueApplication() {
        Intent mainIntent = new Intent(ValidationActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    @OnShowRationale({Manifest.permission.READ_EXTERNAL_STORAGE})
    void showRationale(final PermissionRequest request) {
        buildPermissionDialog((dialog, which) -> request.proceed());
    }

    private void buildPermissionDialog(OnClickListener listener) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Android 6.0").setCancelable(false).setMessage("Com a versão do android igual ou superior ao Android 6.0," + " é necessário que você aceite as permissões para o funcionamento do app.\n\n").setPositiveButton("OK", listener).create().show();
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

