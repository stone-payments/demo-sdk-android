package br.com.stonesdk.sdkdemo.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import androidx.annotation.Nullable;

import br.com.stone.sdk.android.error.StoneStatus;
import br.com.stone.sdk.core.enums.ErrorsEnum;
import br.com.stone.sdk.payment.enums.Action;
import br.com.stone.sdk.payment.enums.ReceiptType;
import br.com.stone.sdk.payment.enums.TransactionStatusEnum;
import br.com.stone.sdk.payment.providers.PosPrintReceiptProvider;
import br.com.stone.sdk.payment.providers.PosTransactionProvider;
import br.com.stonesdk.sdkdemo.controller.PrintController;


public class PosTransactionActivity extends BaseTransactionActivity<PosTransactionProvider> {

    @Override
    protected PosTransactionProvider buildTransactionProvider() {
        return new PosTransactionProvider(this, transactionObject, getSelectedUserModel());
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
    public void onError(@Nullable StoneStatus stoneStatus) {
        super.onError(stoneStatus);
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

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (action == Action.TRANSACTION_WAITING_PASSWORD) {
                    Toast.makeText(
                            PosTransactionActivity.this,
                            "Pin tries remaining to block card: ${transactionProvider?.remainingPinTries}",
                            Toast.LENGTH_LONG
                    ).show();
                }
            }
        });
    }
}
