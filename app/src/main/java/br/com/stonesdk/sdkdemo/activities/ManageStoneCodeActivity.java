package br.com.stonesdk.sdkdemo.activities;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import br.com.stone.sdk.activation.providers.ActiveApplicationProvider;
import br.com.stone.sdk.android.error.StoneStatus;
import br.com.stone.sdk.core.model.user.UserModel;
import br.com.stone.sdk.core.providers.interfaces.StoneCallbackInterface;
import br.com.stone.sdk.core.utils.Stone;
import br.com.stonesdk.sdkdemo.R;


/**
 * @author tiago.barbosa
 * @since 10/04/2018
 */
public class ManageStoneCodeActivity extends AppCompatActivity {

    private final List<UserModel> userModelList = Stone.getSessionApplication().getUserModelList();
    private ListView stoneCodeListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_stone_code);

        findViewById(R.id.activateManageStoneCodeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activateStoneCodeButtonOnClickListener();
            }
        });

        stoneCodeListView = findViewById(R.id.manageStoneCodeListView);
        stoneCodeListView.setAdapter(populateStoneCodeListView());
        stoneCodeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                manageStoneCodeListViewOnItemClickListener(position);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        populateStoneCodeListView();
    }

    private ArrayAdapter populateStoneCodeListView() {
        ArrayAdapter<String> stoneCodeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        for (UserModel userModel : userModelList) {
            stoneCodeAdapter.add(userModel.getStoneCode());
        }
        return stoneCodeAdapter;
    }

    private void activateStoneCodeButtonOnClickListener() {
        final EditText stoneCodeEditText = findViewById(R.id.insertManageStoneCodeEditText);
        final ActiveApplicationProvider activeApplicationProvider = new ActiveApplicationProvider(ManageStoneCodeActivity.this);
        activeApplicationProvider.setDialogTitle("Aguarde");
        activeApplicationProvider.setDialogMessage("Ativando...");
        activeApplicationProvider.useDefaultUI(true);

        activeApplicationProvider.setConnectionCallback(new StoneCallbackInterface() {
            @Override
            public void onError(@Nullable StoneStatus stoneStatus) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                Log.e("ManageStoneCodeActivity", "onError: " + activeApplicationProvider.getListOfErrors());
            }

            @Override
            public void onSuccess() {
                stoneCodeListView.setAdapter(populateStoneCodeListView());
                ((ArrayAdapter) stoneCodeListView.getAdapter()).notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
            }
        });

        activeApplicationProvider.activate(stoneCodeEditText.getText().toString());

        /*
          : example with list of stone codes :

            List<String> stoneCodeList = Arrays.asList("636852", "357894", "095632");
            activeApplicationProvider.activate(stoneCodeList);

         */
    }

    private void manageStoneCodeListViewOnItemClickListener(int position) {
        final ActiveApplicationProvider activeApplicationProvider = new ActiveApplicationProvider(ManageStoneCodeActivity.this);
        activeApplicationProvider.setDialogTitle("Aguarde");
        activeApplicationProvider.setDialogMessage("Desativando...");
        activeApplicationProvider.useDefaultUI(true);

        activeApplicationProvider.setConnectionCallback(new StoneCallbackInterface() {
            @Override
            public void onError(@Nullable StoneStatus stoneStatus) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                Log.e("ManageStoneCodeActivity", "onError: " + activeApplicationProvider.getListOfErrors());
            }

            @Override
            public void onSuccess() {
                stoneCodeListView.setAdapter(populateStoneCodeListView());
                ((ArrayAdapter) stoneCodeListView.getAdapter()).notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
            }
        });
        activeApplicationProvider.deactivate(userModelList.get(position).getStoneCode());
    }

}
