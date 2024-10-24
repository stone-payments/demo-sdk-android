package br.com.stonesdk.sdkdemo.activities;

import android.widget.Toast;

import stone.application.enums.TransactionStatusEnum;
import stone.providers.TransactionProvider;
import stone.utils.Stone;

public class TransactionActivity extends BaseTransactionActivity<TransactionProvider> {

    @Override
    protected TransactionProvider buildTransactionProvider() {
        return new TransactionProvider(TransactionActivity.this, transactionObject, getSelectedUserModel(), Stone.getPinpadFromListAt(0));
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

    @Override
    public void onError() {
        super.onError();
    }
}
