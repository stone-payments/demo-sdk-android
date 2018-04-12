package br.com.stonesdk.sdkdemo.activities;

import stone.application.enums.ErrorsEnum;
import stone.application.enums.TransactionStatusEnum;
import stone.application.interfaces.StoneCallbackInterface;
import stone.providers.LoadTablesProvider;
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
            showToastOnUiThread("Transação enviada com sucesso e salva no banco. Para acessar, use o TransactionDAO.");
        } else {
            showToastOnUiThread("Erro na transação: \"" + getAuthorizationMessage() + "\"");
        }
    }

    @Override
    public void onError() {
        super.onError();
        if (providerHasErrorEnum(ErrorsEnum.NEED_LOAD_TABLES)) { // code 20
            LoadTablesProvider loadTablesProvider = new LoadTablesProvider(TransactionActivity.this, Stone.getPinpadFromListAt(0));
            loadTablesProvider.setDialogMessage("Subindo as tabelas");
            loadTablesProvider.useDefaultUI(true); // para dar feedback ao usuario ou nao.
            loadTablesProvider.execute();
            loadTablesProvider.setConnectionCallback(new StoneCallbackInterface() {
                public void onSuccess() {
                    initTransaction(); // reinicia a transação
                }

                public void onError() {
                    showToastOnUiThread("Sucesso.");
                }
            });
        }
    }
}
