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

import br.com.stone.sdk.android.error.StoneStatus;
import br.com.stone.sdk.core.enums.ErrorsEnum;
import br.com.stone.sdk.core.model.user.UserModel;
import br.com.stone.sdk.core.utils.Stone;
import br.com.stone.sdk.payment.database.models.transaction.TransactionObject;
import br.com.stone.sdk.payment.enums.Action;
import br.com.stone.sdk.payment.enums.EntryMode;
import br.com.stone.sdk.payment.enums.TypeOfTransactionEnum;
import br.com.stone.sdk.payment.providers.interfaces.BaseTransactionProvider;
import br.com.stone.sdk.payment.providers.interfaces.StoneActionCallback;
import br.com.stone.sdk.payment.utils.InstalmentTransaction;
import br.com.stonesdk.sdkdemo.R;

/**
 * Created by felipe on 05/03/18.
 */

public abstract class BaseTransactionActivity<T extends BaseTransactionProvider> extends AppCompatActivity implements StoneActionCallback {
    private BaseTransactionProvider transactionProvider;
    protected final TransactionObject transactionObject = new TransactionObject();
    RadioGroup transactionTypeRadioGroup;
    Spinner installmentTypeSpinner;
    Spinner stoneCodeSpinner;
    TextView installmentTypeTextView;
    TextView installmentNumberTextView;
    TextView infoTextView;
    EditText installmentNumberEditText;
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
        installmentTypeTextView = findViewById(R.id.installmentTypeTextView);
        installmentTypeSpinner = findViewById(R.id.installmentTypeSpinner);
        installmentNumberTextView = findViewById(R.id.installmentNumberTextView);
        infoTextView = findViewById(R.id.infoTextView);
        installmentNumberEditText = findViewById(R.id.installmentNumberEditText);
        stoneCodeSpinner = findViewById(R.id.stoneCodeSpinner);
        captureTransactionCheckBox = findViewById(R.id.captureTransactionCheckBox);
        amountEditText = findViewById(R.id.amountEditText);
        logTextView = findViewById(R.id.logTextView);
        sendTransactionButton = findViewById(R.id.sendTransactionButton);
        cancelTransactionButton = findViewById(R.id.cancelTransactionButton);

        spinnerAction();
        radioGroupClick();
        sendTransactionButton.setOnClickListener(v -> initTransaction());
        cancelTransactionButton.setOnClickListener(v -> transactionProvider.abortPayment());

        builder = new Dialog(this);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    private InstalmentTransaction getInstalmentTransaction() {
        switch (installmentTypeSpinner.getSelectedItemPosition()) {
            case 0: {
                return new InstalmentTransaction.None();
            }
            case 1: {
                return new InstalmentTransaction.Issuer(
                        Integer.parseInt(
                                installmentNumberEditText.getText().toString()
                        ));
            }
            case 2: {
                return new InstalmentTransaction.Merchant(
                        Integer.parseInt(
                                installmentNumberEditText.getText().toString()
                        ));
            }
        }
        return null;
    }

    private void radioGroupClick() {
        transactionTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radioPix:
                case R.id.radioDebit:
                case R.id.radioVoucher:
                    installmentTypeTextView.setVisibility(View.GONE);
                    installmentTypeSpinner.setVisibility(View.GONE);
                    installmentNumberTextView.setVisibility(View.GONE);
                    installmentNumberEditText.setVisibility(View.GONE);
                    infoTextView.setVisibility(View.GONE);
                    break;
                case R.id.radioCredit:
                    installmentTypeTextView.setVisibility(View.VISIBLE);
                    installmentTypeSpinner.setVisibility(View.VISIBLE);
                    installmentNumberTextView.setVisibility(View.VISIBLE);
                    installmentNumberEditText.setVisibility(View.VISIBLE);
                    infoTextView.setVisibility(View.VISIBLE);
                    break;
            }
        });
    }

    private void spinnerAction() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.installments_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        installmentTypeSpinner.setAdapter(adapter);

        ArrayAdapter<String> stoneCodeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        stoneCodeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (UserModel userModel : Stone.getSessionApplication().getUserModelList()) {
            stoneCodeAdapter.add(userModel.getStoneCode());
        }
        stoneCodeSpinner.setAdapter(stoneCodeAdapter);
    }

    public void initTransaction() {
        transactionObject.setInstalmentTransaction(getInstalmentTransaction());

        TypeOfTransactionEnum transactionType;
        switch (transactionTypeRadioGroup.getCheckedRadioButtonId()) {
            case R.id.radioCredit:
                transactionType = TypeOfTransactionEnum.CREDIT;
                break;
            case R.id.radioDebit:
                transactionType = TypeOfTransactionEnum.DEBIT;
                break;
            case R.id.radioVoucher:
                transactionType = TypeOfTransactionEnum.VOUCHER;
                break;
            case R.id.radioPix: {
                transactionType = TypeOfTransactionEnum.PIX;
                transactionObject.setEntryMode(EntryMode.QRCODE);
                break;
            }
            default:
                transactionType = TypeOfTransactionEnum.CREDIT;
        }

        transactionObject.setInitiatorTransactionKey(null);
        transactionObject.setTypeOfTransaction(transactionType);
        transactionObject.setCapture(captureTransactionCheckBox.isChecked());
        transactionObject.setAmount(amountEditText.getText().toString());

        transactionProvider = buildTransactionProvider();
        transactionProvider.setConnectionCallback(this);
        transactionProvider.execute();
    }

    protected String getAuthorizationMessage() {
        return transactionProvider.getMessageFromAuthorize();
    }

    protected abstract T buildTransactionProvider();

    protected boolean providerHasErrorEnum(ErrorsEnum errorsEnum) {
        return transactionProvider.theListHasError(errorsEnum);
    }

    @Override
    public void onError(@Nullable StoneStatus stoneStatus) {
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
        return Stone.getUserModel(stoneCodeSpinner.getSelectedItemPosition());
    }
}
