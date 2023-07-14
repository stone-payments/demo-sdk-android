package br.com.stonesdk.sdkdemo.activities;

import android.app.AlertDialog;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import br.com.stone.sdk.android.error.StoneStatus;
import br.com.stone.sdk.core.providers.interfaces.StoneCallbackInterface;
import br.com.stone.sdk.hardware.providers.PosPrintProvider;
import br.com.stone.sdk.payment.database.dao.transaction.TransactionDAO;
import br.com.stone.sdk.payment.database.models.transaction.TransactionObject;
import br.com.stone.sdk.payment.enums.ReceiptType;
import br.com.stone.sdk.payment.providers.CancellationProvider;
import br.com.stone.sdk.payment.providers.CaptureTransactionProvider;
import br.com.stone.sdk.payment.providers.PosPrintReceiptProvider;
import br.com.stone.sdk.payment.providers.SendEmailTransactionProvider;
import br.com.stone.sdk.payment.providers.model.email.Contact;
import br.com.stonesdk.sdkdemo.R;

public class TransactionListActivity extends AppCompatActivity implements OnItemClickListener {

    ListView listView;
    List<TransactionObject> transactionObjects;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list);

        listView = findViewById(R.id.listTransactionActivity);

        TransactionDAO transactionDAO = new TransactionDAO(getApplicationContext());
        transactionObjects = transactionDAO.getAllTransactionsOrderByIdDesc();
        String[] rowOfList = new String[transactionObjects.size()];
        for (int i = 0; i < transactionObjects.size(); i++) {
            rowOfList[i] = String.format("%s=%s\n%s", transactionObjects.get(i).getIdFromBase(), transactionObjects.get(i).getAmount(), transactionObjects.get(i).getTransactionStatus());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, rowOfList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        final TransactionObject selectedTransaction = transactionObjects.get(position);
        ArrayList<String> optionsList = new ArrayList<String>() {{
            add("[POS] Imprimir via do estabelecimento");
            add("[POS] Imprimir via do cliente");
            add("[POS] Imprimir comprovante customizado");
            add("Cancelar");
            add("Enviar e-mail via do cliente");
            add("Enviar e-mail via do estabelecimento");
        }};
        if (!selectedTransaction.isCapture()) {
            optionsList.add("Capturar Transação");
        }
        String[] options = new String[optionsList.size()];
        optionsList.toArray(options);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.list_dialog_title)
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            printReceipt(ReceiptType.MERCHANT, selectedTransaction);
                            break;

                        case 2:
                            printReceipt(ReceiptType.CLIENT, selectedTransaction);
                            break;

                        case 3:
                            final PosPrintProvider customPosPrintProvider = new PosPrintProvider(TransactionListActivity.this);
                            customPosPrintProvider.addLine("");
                            customPosPrintProvider.addLine("PAN : " + selectedTransaction.getCardHolderNumber());
                            customPosPrintProvider.addLine("DATE/TIME : " + selectedTransaction.getDate() + " " + selectedTransaction.getTime());
                            customPosPrintProvider.addLine("AMOUNT : " + selectedTransaction.getAmount());
                            customPosPrintProvider.addLine("ATK : " + selectedTransaction.getAcquirerTransactionKey());
                            customPosPrintProvider.addLine("");
                            customPosPrintProvider.addBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.signature));
                            customPosPrintProvider.print(new StoneCallbackInterface() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(TransactionListActivity.this, "Recibo impresso", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onError(@Nullable StoneStatus stoneStatus) {
                                    assert stoneStatus != null;
                                    Toast.makeText(TransactionListActivity.this, "Erro ao imprimir: " + stoneStatus.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;

                        case 4:
                            final CancellationProvider cancellationProvider = new CancellationProvider(TransactionListActivity.this, selectedTransaction);
                            cancellationProvider.setConnectionCallback(new StoneCallbackInterface() {
                                public void onSuccess() {
                                    Toast.makeText(getApplicationContext(),
                                            cancellationProvider.getMessageFromAuthorize(),
                                            Toast.LENGTH_SHORT).show();
                                    finish();
                                }

                                public void onError(@Nullable StoneStatus stoneStatus) {
                                    Toast.makeText(getApplicationContext(), "Um erro ocorreu durante o cancelamento com a transacao de id: " + selectedTransaction.getIdFromBase(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            cancellationProvider.execute();
                            break;
                        case 5:
                            sendReceipt(selectedTransaction, ReceiptType.CLIENT);
                            break;
                        case 6:
                            sendReceipt(selectedTransaction, ReceiptType.MERCHANT);
                            break;
                        case 7:
                            final CaptureTransactionProvider provider = new CaptureTransactionProvider(TransactionListActivity.this, selectedTransaction);
                            provider.setConnectionCallback(new StoneCallbackInterface() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(TransactionListActivity.this, "Transação Capturada com sucesso!",
                                            Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onError(@Nullable StoneStatus stoneStatus) {
                                    assert stoneStatus != null;
                                    Toast.makeText(TransactionListActivity.this, "Ocorreu um erro captura da transacao: " +
                                                    stoneStatus.getMessage(),
                                            Toast.LENGTH_SHORT).show();

                                }
                            });

                            provider.execute();
                            break;
                    }
                });
        builder.create();
        builder.show();
    }

    private void sendReceipt(TransactionObject selectedTransaction, ReceiptType receiptType) {
        SendEmailTransactionProvider sendEmailProvider = new SendEmailTransactionProvider(TransactionListActivity.this, selectedTransaction);
        sendEmailProvider.setReceiptType(receiptType);
        sendEmailProvider.addTo(new Contact("cliente@gmail.com", "Nome do Cliente"));
        sendEmailProvider.setFrom(new Contact("loja@gmail.com", "Nome do Estabelecimento"));
        sendEmailProvider.setConnectionCallback(new StoneCallbackInterface() {
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "Enviado com sucesso", Toast.LENGTH_LONG).show();
            }

            public void onError(@Nullable StoneStatus stoneStatus) {
                Toast.makeText(getApplicationContext(), "Nao enviado", Toast.LENGTH_LONG).show();
            }
        });
        sendEmailProvider.execute();
    }

    private void printReceipt(ReceiptType receiptType, TransactionObject transactionObject) {
        PosPrintReceiptProvider posPrintReceiptProvider = new PosPrintReceiptProvider(this, transactionObject, receiptType);
        posPrintReceiptProvider.print(new StoneCallbackInterface() {
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
    }
}
