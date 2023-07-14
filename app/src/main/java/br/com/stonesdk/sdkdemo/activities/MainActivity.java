package br.com.stonesdk.sdkdemo.activities;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import br.com.stone.sdk.activation.providers.ActiveApplicationProvider;
import br.com.stone.sdk.android.error.StoneStatus;
import br.com.stone.sdk.core.providers.interfaces.StoneCallbackInterface;
import br.com.stone.sdk.hardware.providers.PosPrintProvider;
import br.com.stone.sdk.payment.database.models.transaction.TransactionObject;
import br.com.stone.sdk.payment.enums.Action;
import br.com.stone.sdk.payment.providers.DisplayMessageProvider;
import br.com.stone.sdk.payment.providers.PosValidateTransactionByCardProvider;
import br.com.stone.sdk.payment.providers.ReversalProvider;
import br.com.stone.sdk.payment.providers.interfaces.StoneActionCallback;
import br.com.stone.sdk.payment.utils.StonePayment;
import br.com.stonesdk.sdkdemo.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.transactionOption).setOnClickListener(this);
        findViewById(R.id.posTransactionOption).setOnClickListener(this);
        findViewById(R.id.pairedDevicesOption).setOnClickListener(this);
        findViewById(R.id.disconnectDeviceOption).setOnClickListener(this);
        findViewById(R.id.deactivateOption).setOnClickListener(this);
        findViewById(R.id.cancelTransactionsOption).setOnClickListener(this);
        findViewById(R.id.displayMessageOption).setOnClickListener(this);
        findViewById(R.id.listTransactionOption).setOnClickListener(this);
        findViewById(R.id.manageStoneCodeOption).setOnClickListener(this);
        findViewById(R.id.posValidateCardOption).setOnClickListener(this);
        findViewById(R.id.posPrinterProvider).setOnClickListener(this);
        findViewById(R.id.posMifareProvider).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //region Geral

            case R.id.listTransactionOption:
                Intent transactionListIntent = new Intent(MainActivity.this, TransactionListActivity.class);
                startActivity(transactionListIntent);
                break;

            case R.id.cancelTransactionsOption:
                final ReversalProvider reversalProvider = new ReversalProvider(this);
                reversalProvider.setConnectionCallback(new StoneCallbackInterface() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(MainActivity.this, "Transações canceladas com sucesso", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@Nullable StoneStatus stoneStatus) {
                        assert stoneStatus != null;
                        Toast.makeText(MainActivity.this, "Ocorreu um erro durante o cancelamento das tabelas: " + stoneStatus.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                reversalProvider.execute();
                break;

            case R.id.manageStoneCodeOption:
                startActivity(new Intent(MainActivity.this, ManageStoneCodeActivity.class));
                break;

            case R.id.deactivateOption:
                final ActiveApplicationProvider provider = new ActiveApplicationProvider(MainActivity.this);
                provider.deactivate(new StoneCallbackInterface() {
                    @Override
                    public void onSuccess() {
                        Intent mainIntent = new Intent(MainActivity.this, ValidationActivity.class);
                        startActivity(mainIntent);
                        finish();
                    }

                    @Override
                    public void onError(@Nullable StoneStatus stoneStatus) {
                        makeText(MainActivity.this, "Erro na ativacao do aplicativo, verifique a mensagem do Stone Status", LENGTH_SHORT).show();
                        assert stoneStatus != null;
                        Log.e("deactivateOption", "onError: " + stoneStatus.getMessage());
                    }
                });
                break;
            //endregion

            //regionPinpad

            case R.id.pairedDevicesOption:
                Intent devicesIntent = new Intent(MainActivity.this, DevicesActivity.class);
                startActivity(devicesIntent);
                break;

            case R.id.transactionOption:
                if (StonePayment.getPinpadListSize() > 0) {
                    Intent transactionIntent = new Intent(MainActivity.this, TransactionActivity.class);
                    startActivity(transactionIntent);
                } else {
                    makeText(getApplicationContext(), "Conecte-se a um pinpad.", LENGTH_SHORT).show();
                }
                break;

            case R.id.displayMessageOption:
                if (StonePayment.getPinpadListSize() <= 0) {
                    makeText(getApplicationContext(), "Conecte-se a um pinpad.", LENGTH_SHORT).show();
                    break;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Digite a mensagem para mostrar no pinpad");
                final EditText editText = new EditText(MainActivity.this);
                builder.setView(editText);
                builder.setPositiveButton("OK", (dialog, which) -> {
                    String text = editText.getText().toString();
                    DisplayMessageProvider displayMessageProvider = new DisplayMessageProvider(MainActivity.this, text, StonePayment.getPinpadFromListAt(0));
                    displayMessageProvider.execute();
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                builder.show();
                break;

            case R.id.disconnectDeviceOption:
                if (StonePayment.getPinpadListSize() > 0) {
                    Intent closeBluetoothConnectionIntent = new Intent(MainActivity.this, DisconnectPinpadActivity.class);
                    startActivity(closeBluetoothConnectionIntent);
                } else {
                    Toast.makeText(this, "Nenhum device Conectado", Toast.LENGTH_SHORT).show();
                }
                break;

            //endregion

            //region POS Android

            case R.id.posTransactionOption:
                startActivity(new Intent(MainActivity.this, PosTransactionActivity.class));
                break;

            case R.id.posValidateCardOption:
                final PosValidateTransactionByCardProvider posValidateTransactionByCardProvider = new PosValidateTransactionByCardProvider(this);
                posValidateTransactionByCardProvider.setConnectionCallback(new StoneActionCallback() {
                    @Override
                    public void onError(@Nullable StoneStatus stoneStatus) {
                        assert stoneStatus != null;
                        Log.i("posValidateCardOption", "onError: " + stoneStatus.getMessage());
                    }

                    @Override
                    public void onStatusChanged(final Action action) {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, action.name(), Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onSuccess() {
                        runOnUiThread(() -> {
                            final List<TransactionObject> transactionsWithCurrentCard = posValidateTransactionByCardProvider.getTransactionsWithCurrentCard();
                            if (transactionsWithCurrentCard.isEmpty())
                                Toast.makeText(MainActivity.this, "Cartão não fez transação.", Toast.LENGTH_SHORT).show();
                            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            Log.i("posValidateCardOption", "onSuccess: " + transactionsWithCurrentCard);
                        });

                    }
                });
                posValidateTransactionByCardProvider.execute();
                break;

            case R.id.posPrinterProvider:
                final PosPrintProvider posPrintProvider = new PosPrintProvider(getApplicationContext());
                posPrintProvider.addLine("PAN : " + "123");
                posPrintProvider.addLine("DATE/TIME : 01/01/1900");
                posPrintProvider.addLine("AMOUNT : 200.00");
                posPrintProvider.addLine("ATK : 123456789");
                posPrintProvider.addLine("Signature");
                posPrintProvider.addBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.signature));
                posPrintProvider.print(new StoneCallbackInterface() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(), "Recibo impresso", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@Nullable StoneStatus stoneStatus) {
                        assert stoneStatus != null;
                        Toast.makeText(getApplicationContext(), "Erro ao imprimir: " + stoneStatus.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                break;

            case R.id.posMifareProvider:
                startActivity(new Intent(MainActivity.this, MifareActivity.class));
                break;
            //endregion

            default:
                break;
        }
    }

}
