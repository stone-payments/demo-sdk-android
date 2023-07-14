package br.com.stonesdk.sdkdemo.activities;

import android.app.AlertDialog;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.List;

import br.com.stone.sdk.android.error.StoneStatus;
import br.com.stone.sdk.core.enums.ErrorsEnum;
import br.com.stone.sdk.core.providers.interfaces.StoneCallbackInterface;
import br.com.stone.sdk.payment.enums.Action;
import br.com.stone.sdk.payment.enums.ReceiptType;
import br.com.stone.sdk.payment.enums.TransactionStatusEnum;
import br.com.stone.sdk.payment.providers.PosPrintReceiptProvider;
import br.com.stone.sdk.payment.providers.PosTransactionProvider;


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
            PosPrintReceiptProvider printMerchant =
                    new PosPrintReceiptProvider(PosTransactionActivity.this, transactionObject, ReceiptType.MERCHANT);
            printMerchant.print(new StoneCallbackInterface() {
                @Override
                public void onSuccess() {
                    Toast.makeText(PosTransactionActivity.this, "Recibo impresso", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(@Nullable StoneStatus stoneStatus) {
                    assert stoneStatus != null;
                    Toast.makeText(PosTransactionActivity.this, "Erro ao imprimir: " + stoneStatus.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Transação aprovada! Deseja imprimir a via do cliente?");

            builder.setPositiveButton(android.R.string.yes, (dialog, which) -> {
                PosPrintReceiptProvider printClient =
                        new PosPrintReceiptProvider(PosTransactionActivity.this, transactionObject, ReceiptType.CLIENT);
                printClient.print(new StoneCallbackInterface() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(PosTransactionActivity.this, "Recibo impresso", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@Nullable StoneStatus stoneStatus) {
                        assert stoneStatus != null;
                        Toast.makeText(PosTransactionActivity.this, "Erro ao imprimir: " + stoneStatus.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            });

            builder.setNegativeButton(android.R.string.no, null);

            runOnUiThread(builder::show);


        } else {
            runOnUiThread(() -> Toast.makeText(
                    getApplicationContext(),
                    "Erro na transação: \"" + getAuthorizationMessage() + "\"",
                    Toast.LENGTH_LONG
            ).show());
        }
    }

    @Override
    public void onError(@Nullable StoneStatus stoneStatus) {
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
