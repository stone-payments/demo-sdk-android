package br.com.stonesdk.sdkdemo.activities;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import br.com.stonesdk.sdkdemo.R;
import stone.application.enums.Action;
import stone.application.enums.ErrorsEnum;
import stone.application.enums.InstalmentTransactionEnum;
import stone.application.enums.TypeOfTransactionEnum;
import stone.application.interfaces.StoneActionCallback;
import stone.database.transaction.TransactionObject;
import stone.providers.BaseTransactionProvider;
import stone.user.UserModel;
import stone.utils.Stone;

/**
 * Created by felipe on 05/03/18.
 */
public abstract class BaseTransactionActivity<T extends BaseTransactionProvider> extends AppCompatActivity implements StoneActionCallback {
    private BaseTransactionProvider transactionProvider;
    protected final TransactionObject transactionObject = new TransactionObject();
    RadioGroup transactionTypeRadioGroup;
    Spinner installmentsSpinner;
    Spinner stoneCodeSpinner;
    TextView installmentsTextView;
    CheckBox captureTransactionCheckBox;
    EditText amountEditText;
    TextView logTextView;
    Button sendTransactionButton;
    Button cancelTransactionButton;
    Dialog builder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        transactionTypeRadioGroup = findViewById(R.id.transactionTypeRadioGroup);
        installmentsTextView = findViewById(R.id.installmentsTextView);
        installmentsSpinner = findViewById(R.id.installmentsSpinner);
        stoneCodeSpinner = findViewById(R.id.stoneCodeSpinner);
        captureTransactionCheckBox = findViewById(R.id.captureTransactionCheckBox);
        amountEditText = findViewById(R.id.amountEditText);
        logTextView = findViewById(R.id.logTextView);
        sendTransactionButton = findViewById(R.id.sendTransactionButton);
        cancelTransactionButton = findViewById(R.id.cancelTransactionButton);
        spinnerAction();
        radioGroupClick();
        sendTransactionButton.setOnClickListener(v -> initTransaction());
        cancelTransactionButton.setOnClickListener(v -> {
            if (transactionProvider != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        transactionProvider.abortPayment();
                    }
                }).start();
            }
        });
        builder = new Dialog(this);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    private void radioGroupClick() {
        transactionTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioPix || checkedId == R.id.radioDebit || checkedId == R.id.radioVoucher) {
                installmentsTextView.setVisibility(View.GONE);
                installmentsSpinner.setVisibility(View.GONE);
            } else if (checkedId == R.id.radioCredit) {
                installmentsTextView.setVisibility(View.VISIBLE);
                installmentsSpinner.setVisibility(View.VISIBLE);
            }
        });
    }

    private void spinnerAction() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.installments_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        installmentsSpinner.setAdapter(adapter);
        ArrayAdapter<String> stoneCodeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        stoneCodeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (Stone.sessionApplication != null && Stone.sessionApplication.getUserModelList() != null) {
            for (UserModel userModel : Stone.sessionApplication.getUserModelList()) {
                stoneCodeAdapter.add(userModel.getStoneCode());
            }
        }
        stoneCodeSpinner.setAdapter(stoneCodeAdapter);
    }

    public void initTransaction() {
        InstalmentTransactionEnum installmentsEnum = InstalmentTransactionEnum.getAt(installmentsSpinner.getSelectedItemPosition());
        // Informa a quantidade de parcelas.
        transactionObject.setInstalmentTransaction(InstalmentTransactionEnum.getAt(installmentsSpinner.getSelectedItemPosition()));
        // Verifica a forma de pagamento selecionada.
        TypeOfTransactionEnum transactionType;
        int checkedRadioButtonId = transactionTypeRadioGroup.getCheckedRadioButtonId();
        if (checkedRadioButtonId == R.id.radioCredit) {
            transactionType = TypeOfTransactionEnum.CREDIT;
        } else if (checkedRadioButtonId == R.id.radioDebit) {
            transactionType = TypeOfTransactionEnum.DEBIT;
        } else if (checkedRadioButtonId == R.id.radioVoucher) {
            transactionType = TypeOfTransactionEnum.VOUCHER;
        } else if (checkedRadioButtonId == R.id.radioPix) {
            transactionType = TypeOfTransactionEnum.PIX;
        } else {
            transactionType = TypeOfTransactionEnum.CREDIT;
        }
        transactionObject.setInitiatorTransactionKey(null);
        transactionObject.setTypeOfTransaction(transactionType);
        transactionObject.setCapture(captureTransactionCheckBox.isChecked());
        transactionObject.setAmount(amountEditText.getText().toString());
        transactionProvider = buildTransactionProvider();
        transactionProvider.setConnectionCallback(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                transactionProvider.execute();
            }
        }).start();
    }

    protected String getAuthorizationMessage() {
        return transactionProvider.getMessageFromAuthorize();
    }

    protected abstract T buildTransactionProvider();

    protected boolean providerHasErrorEnum(ErrorsEnum errorsEnum) {
        return transactionProvider.theListHasError(errorsEnum);
    }

    @Override
    public void onError() {
        runOnUiThread(() -> Toast.makeText(BaseTransactionActivity.this, "Erro: " + transactionProvider.getListOfErrors(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onStatusChanged(final Action action) {
        runOnUiThread(() -> logTextView.append(action.name() + "\n"));
        if (action == Action.TRANSACTION_WAITING_QRCODE_SCAN) {
            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(transactionObject.getQRCode());

            runOnUiThread(() -> {
                builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                builder.show();
            });
        } else {
            runOnUiThread(() -> builder.dismiss());
        }
    }

    protected BaseTransactionProvider getTransactionProvider() {
        return transactionProvider;
    }

    protected UserModel getSelectedUserModel() {
        if (Stone.sessionApplication != null && Stone.sessionApplication.getUserModelList() != null
                && !Stone.sessionApplication.getUserModelList().isEmpty()) {
            return Stone.getUserModel(stoneCodeSpinner.getSelectedItemPosition());
        }
        return null;
    }
}
