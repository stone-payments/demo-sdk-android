package br.com.stonesdk.sdkdemo.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import br.com.stone.posandroid.providers.PosPrintProvider;
import br.com.stone.posandroid.providers.PosValidateTransactionByCardProvider;
import br.com.stonesdk.sdkdemo.R;
import stone.application.enums.Action;
import stone.application.interfaces.StoneActionCallback;
import stone.application.interfaces.StoneCallbackInterface;
import stone.database.transaction.TransactionObject;
import stone.providers.ActiveApplicationProvider;
import stone.providers.DisplayMessageProvider;
import stone.providers.ReversalProvider;
import stone.utils.Stone;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

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
        // Para cada nova opção na lista, um novo "case" precisa ser inserido aqui.
        switch (v.getId()) {

            case R.id.pairedDevicesOption:
                Intent devicesIntent = new Intent(MainActivity.this, DevicesActivity.class);
                startActivity(devicesIntent);
                break;

            case R.id.transactionOption:
                // Verifica se o bluetooth esta ligado e se existe algum pinpad conectado.
                if (Stone.getPinpadListSize() > 0) {
                    Intent transactionIntent = new Intent(MainActivity.this, TransactionActivity.class);
                    startActivity(transactionIntent);
                    break;
                } else {
                    makeText(getApplicationContext(), "Conecte-se a um pinpad.", LENGTH_SHORT).show();
                    break;
                }

            case R.id.listTransactionOption:
                Intent transactionListIntent = new Intent(MainActivity.this, TransactionListActivity.class);
                startActivity(transactionListIntent);
                break;

            case R.id.displayMessageOption:
                if (Stone.getPinpadListSize() <= 0) {
                    makeText(getApplicationContext(), "Conecte-se a um pinpad.", LENGTH_SHORT).show();
                    break;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Digite a mensagem para mostrar no pinpad");
                final EditText editText = new EditText(MainActivity.this);
                builder.setView(editText);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = editText.getText().toString();
                        DisplayMessageProvider displayMessageProvider =
                                new DisplayMessageProvider(MainActivity.this, text, Stone.getPinpadFromListAt(0));
                        displayMessageProvider.execute();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                break;

            case R.id.cancelTransactionsOption:
                final ReversalProvider reversalProvider = new ReversalProvider(this);
                reversalProvider.useDefaultUI(true);
                reversalProvider.setDialogMessage("Cancelando transações com erro");
                reversalProvider.setConnectionCallback(new StoneCallbackInterface() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(MainActivity.this, "Transações canceladas com sucesso", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(MainActivity.this, "Ocorreu um erro durante o cancelamento das tabelas: " + reversalProvider.getListOfErrors(), Toast.LENGTH_SHORT).show();
                    }
                });
                reversalProvider.execute();
                break;

            case R.id.deactivateOption:
                final ActiveApplicationProvider provider = new ActiveApplicationProvider(MainActivity.this);
                provider.setDialogMessage("Desativando o aplicativo...");
                provider.setDialogTitle("Aguarde");
                provider.useDefaultUI(true);
                provider.setConnectionCallback(new StoneCallbackInterface() {
                    /* Metodo chamado se for executado sem erros */
                    public void onSuccess() {
                        Intent mainIntent = new Intent(MainActivity.this, ValidationActivity.class);
                        startActivity(mainIntent);
                        finish();
                    }

                    /* metodo chamado caso ocorra alguma excecao */
                    public void onError() {
                        makeText(MainActivity.this, "Erro na ativacao do aplicativo, verifique a lista de erros do provider", LENGTH_SHORT).show();
                        /* Chame o metodo abaixo para verificar a lista de erros. Para mais detalhes, leia a documentacao: */
                        Log.e("deactivateOption", "onError: " + provider.getListOfErrors().toString());
                    }
                });
                provider.deactivate();
                break;

            case R.id.disconnectDeviceOption:
                if (Stone.getPinpadListSize() > 0) {
                    Intent closeBluetoothConnectionIntent = new Intent(MainActivity.this, DisconnectPinpadActivity.class);
                    startActivity(closeBluetoothConnectionIntent);
                } else {
                    Toast.makeText(this, "Nenhum device Conectado", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.posTransactionOption:
                startActivity(new Intent(MainActivity.this, PosTransactionActivity.class));
                break;

            case R.id.manageStoneCodeOption:
                startActivity(new Intent(MainActivity.this, ManageStoneCodeActivity.class));
                break;

            case R.id.posValidateCardOption:
                final PosValidateTransactionByCardProvider posValidateTransactionByCardProvider = new PosValidateTransactionByCardProvider(this);
                posValidateTransactionByCardProvider.setConnectionCallback(new StoneActionCallback() {
                    @Override
                    public void onStatusChanged(final Action action) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, action.name(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onSuccess() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final List<TransactionObject> transactionsWithCurrentCard = posValidateTransactionByCardProvider.getTransactionsWithCurrentCard();
                                if (transactionsWithCurrentCard.isEmpty())
                                    Toast.makeText(MainActivity.this, "Cartão não fez transação.", Toast.LENGTH_SHORT).show();
                                Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                Log.i("posValidateCardOption", "onSuccess: " + transactionsWithCurrentCard);
                            }
                        });

                    }

                    @Override
                    public void onError() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                Log.e("posValidateCardOption", "onError: " + posValidateTransactionByCardProvider.getListOfErrors());
                            }
                        });
                    }

                });
                posValidateTransactionByCardProvider.execute();
                break;

            case R.id.posPrinterProvider:
                final PosPrintProvider customPosPrintProvider = new PosPrintProvider(getApplicationContext());
                customPosPrintProvider.addLine("PAN : " + "123");
                customPosPrintProvider.addLine("DATE/TIME : 01/01/1900");
                customPosPrintProvider.addLine("AMOUNT : 200.00");
                customPosPrintProvider.addLine("ATK : 123456789");
                customPosPrintProvider.addLine("Signature");
                customPosPrintProvider.addBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.signature));
                customPosPrintProvider.setConnectionCallback(new StoneCallbackInterface() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(), "Recibo impresso", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(getApplicationContext(), "Erro ao imprimir: " + customPosPrintProvider.getListOfErrors(), Toast.LENGTH_SHORT).show();
                    }
                });
                customPosPrintProvider.execute();

            case R.id.posMifareProvider:
                startActivity(new Intent(MainActivity.this, MifareActivity.class));
                break;

            default:
                break;
        }
    }

}
