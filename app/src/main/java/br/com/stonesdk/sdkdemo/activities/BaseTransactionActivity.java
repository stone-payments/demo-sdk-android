package br.com.stonesdk.sdkdemo.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

        spinnerAction();
        radioGroupClick();
        sendTransactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initTransaction();
            }
        });
    }

    private void radioGroupClick() {
        transactionTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioDebit:
                    case R.id.radioVoucher:
                        installmentsTextView.setVisibility(View.GONE);
                        installmentsSpinner.setVisibility(View.GONE);
                        break;
                    case R.id.radioCredit:
                        installmentsTextView.setVisibility(View.VISIBLE);
                        installmentsSpinner.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
    }

    private void spinnerAction() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.installments_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        installmentsSpinner.setAdapter(adapter);

        ArrayAdapter<String> stoneCodeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        stoneCodeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (UserModel userModel : Stone.sessionApplication.getUserModelList()) {
            stoneCodeAdapter.add(userModel.getStoneCode());
        }
        stoneCodeSpinner.setAdapter(stoneCodeAdapter);
    }

    public void initTransaction() {
        // Informa a quantidade de parcelas.
        transactionObject.setInstalmentTransaction(InstalmentTransactionEnum.getAt(installmentsSpinner.getSelectedItemPosition()));

        // Verifica a forma de pagamento selecionada.
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
            default:
                transactionType = TypeOfTransactionEnum.CREDIT;
        }

//        Defina o ITK da sua transação
//        transactionObject.setInitiatorTransactionKey("SEU_IDENTIFICADOR_UNICO_AQUI");

        transactionObject.setTypeOfTransaction(transactionType);
        transactionObject.setCapture(captureTransactionCheckBox.isChecked());
        transactionObject.setAmount(amountEditText.getText().toString());

//        Seleciona o mcc do lojista.
//        transactionObject.setSubMerchantCategoryCode("123");

//        Seleciona o endereço do lojista.
//        transactionObject.setSubMerchantAddress("address");]

        transactionProvider = buildTransactionProvider();
        transactionProvider.useDefaultUI(true);
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
    public void onError() {
        showToastOnUiThread("Erro: " + transactionProvider.getListOfErrors());
        Log.e("BaseTransactionActivity", "onError: " + transactionProvider.getListOfErrors());
    }

    @Override
    public void onStatusChanged(final Action action) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logTextView.append(action.name() + "\n");
            }
        });
    }

    protected UserModel getSelectedUserModel() {
        return Stone.getUserModel(stoneCodeSpinner.getSelectedItemPosition());
    }

    protected void showToastOnUiThread(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BaseTransactionActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
