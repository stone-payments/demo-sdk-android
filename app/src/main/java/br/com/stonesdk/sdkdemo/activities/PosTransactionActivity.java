package br.com.stonesdk.sdkdemo.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;

import br.com.stone.posandroid.providers.PosPrintReceiptProvider;
import br.com.stone.posandroid.providers.PosTransactionProvider;
import stone.application.enums.ErrorsEnum;
import stone.application.enums.ReceiptType;
import stone.application.enums.TransactionStatusEnum;
import stone.application.interfaces.StoneCallbackInterface;

public class PosTransactionActivity extends BaseTransactionActivity<PosTransactionProvider> {

    @Override
    protected PosTransactionProvider buildTransactionProvider() {
        return new PosTransactionProvider(this, transactionObject, getSelectedUserModel());
    }

    @Override
    public void onSuccess() {
        if (transactionObject.getTransactionStatus() == TransactionStatusEnum.APPROVED) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final PosPrintReceiptProvider posPrintProvider = new PosPrintReceiptProvider(PosTransactionActivity.this, transactionObject, ReceiptType.MERCHANT);
                    posPrintProvider.setConnectionCallback(new StoneCallbackInterface() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            showToastOnUiThread("Erro ao imprimir: " + posPrintProvider.getListOfErrors());
                        }
                    });
                    posPrintProvider.execute();

                    AlertDialog.Builder builder = new AlertDialog.Builder(PosTransactionActivity.this);
                    builder.setTitle("Transação aprovada! Deseja imprimir a via do Cliente?");
                    builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final PosPrintReceiptProvider posPrintProvider = new PosPrintReceiptProvider(PosTransactionActivity.this, transactionObject, ReceiptType.CLIENT);
                            posPrintProvider.setConnectionCallback(new StoneCallbackInterface() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                    showToastOnUiThread("Erro ao imprimir: " + posPrintProvider.getListOfErrors());
                                }
                            });
                            posPrintProvider.execute();
                        }
                    });
                    builder.setNegativeButton(android.R.string.no, null);
                    builder.show();
                }
            });
        } else {
            showToastOnUiThread("Erro na transação: \"" + getAuthorizationMessage() + "\"");
        }
    }

    @Override
    public void onError() {
        super.onError();
        if (providerHasErrorEnum(ErrorsEnum.DEVICE_NOT_COMPATIBLE)) {
            showToastOnUiThread("Dispositivo não compatível ou dependência relacionada não está presente");
        }
    }
}
