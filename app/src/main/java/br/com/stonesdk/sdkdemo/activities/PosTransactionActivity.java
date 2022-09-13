package br.com.stonesdk.sdkdemo.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import java.util.List;

import br.com.stone.posandroid.providers.PosPrintReceiptProvider;
import br.com.stone.posandroid.providers.PosTransactionProvider;
import br.com.stonesdk.sdkdemo.controller.PrintController;
import stone.application.enums.Action;
import stone.application.enums.ErrorsEnum;
import stone.application.enums.ReceiptType;
import stone.application.enums.TransactionStatusEnum;

public class PosTransactionActivity extends BaseTransactionActivity<PosTransactionProvider> {

    @Override
    protected PosTransactionProvider buildTransactionProvider() {
        return new PosTransactionProvider(this, transactionObject, getSelectedUserModel());
    }

    protected PosTransactionProvider getTransactionProvider() {
        return (PosTransactionProvider) super.getTransactionProvider();
    }

    @Override
    public void onSuccess() {
        if (transactionObject.getTransactionStatus() == TransactionStatusEnum.APPROVED) {

            final PrintController printMerchant =
                    new PrintController(PosTransactionActivity.this,
                            new PosPrintReceiptProvider(this.getApplicationContext(),
                                    transactionObject, ReceiptType.MERCHANT));

            printMerchant.print();

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Transação aprovada! Deseja imprimir a via do cliente?");

            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final PrintController printClient =
                    new PrintController(PosTransactionActivity.this,
                            new PosPrintReceiptProvider(getApplicationContext(),
                                    transactionObject, ReceiptType.CLIENT));
                    printClient.print();
                }
            });

            builder.setNegativeButton(android.R.string.no, null);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    builder.show();

                }
            });


        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(
                            getApplicationContext(),
                            "Erro na transação: \"" + getAuthorizationMessage() + "\"",
                            Toast.LENGTH_LONG
                    ).show();
                }
            });
        }
    }

    @Override
    public void onError() {
        super.onError();
        if (providerHasErrorEnum(ErrorsEnum.DEVICE_NOT_COMPATIBLE)) {
            Toast.makeText(
                    this,
                    "Dispositivo não compatível ou dependência relacionada não está presente",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    public void onStatusChanged(final Action action) {
        super.onStatusChanged(action);

        runOnUiThread(() -> {

            switch (action) {
                case TRANSACTION_WAITING_PASSWORD:
                    Toast.makeText(
                            PosTransactionActivity.this,
                            "Pin tries remaining to block card: ${transactionProvider?.remainingPinTries}",
                            Toast.LENGTH_LONG
                    ).show();
                    break;
                case TRANSACTION_TYPE_SELECTION:
                    List<String> options = getTransactionProvider().getTransactionTypeOptions();
                    showTransactionTypeSelectionDialog(options);
            }

        });
    }


    private void showTransactionTypeSelectionDialog(final List<String> optionsList) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecione o tipo de transação");
        String[] options = new String[optionsList.size()];
        optionsList.toArray(options);
        builder.setItems(
                options,
                (dialog, which) -> getTransactionProvider().setTransactionTypeSelected(which)
        );
        builder.show();
    }
}
