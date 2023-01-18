package br.com.stonesdk.sdkdemo.activities;

import android.widget.Toast;

import androidx.annotation.Nullable;

import br.com.stone.sdk.android.error.StoneStatus;
import br.com.stone.sdk.payment.enums.TransactionStatusEnum;
import br.com.stone.sdk.payment.providers.TransactionProvider;
import br.com.stone.sdk.payment.utils.StonePayment;

public class TransactionActivity extends BaseTransactionActivity<TransactionProvider> {

    @Override
    protected TransactionProvider buildTransactionProvider() {
        return new TransactionProvider(TransactionActivity.this, transactionObject, getSelectedUserModel(), StonePayment.getPinpadFromListAt(0));
    }

    @Override
    public void onSuccess() {
        if (transactionObject.getTransactionStatus() == TransactionStatusEnum.APPROVED) {
            Toast.makeText(getApplicationContext(), "Transação enviada com sucesso e salva no banco. Para acessar, use o TransactionDAO.", Toast.LENGTH_SHORT).show();
        } else {

            String msg = "Erro na transação";

            if (getAuthorizationMessage() != null) {
                msg += ":" + getAuthorizationMessage();
            } else {
                msg += "!";
            }

            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        }
    }
}
