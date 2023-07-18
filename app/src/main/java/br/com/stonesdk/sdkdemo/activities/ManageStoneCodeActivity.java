package br.com.stonesdk.sdkdemo.activities;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

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

        findViewById(R.id.activateManageStoneCodeButton).setOnClickListener(view -> activateStoneCodeButtonOnClickListener());

        stoneCodeListView = findViewById(R.id.manageStoneCodeListView);
        stoneCodeListView.setAdapter(populateStoneCodeListView());
        stoneCodeListView.setOnItemClickListener((adapterView, view, position, l) -> deactivateStoneCodeListViewOnItemClickListener(position));
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
        String stoneCode = stoneCodeEditText.getText().toString();
        final ActiveApplicationProvider activeApplicationProvider = new ActiveApplicationProvider(ManageStoneCodeActivity.this);

        activeApplicationProvider.activate(stoneCode, new StoneCallbackInterface() {
            @Override
            public void onSuccess() {
                stoneCodeListView.setAdapter(populateStoneCodeListView());
                ((ArrayAdapter) stoneCodeListView.getAdapter()).notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(@Nullable StoneStatus stoneStatus) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                String error;
                if (stoneStatus != null) {
                    error = stoneStatus.getMessage();
                } else {
                    error = activeApplicationProvider.getListOfErrors().toString();
                }
                Log.e("DevicesActivity", "onError: " + error);
                Log.e("ManageStoneCodeActivity", "onError: " + error);
            }
        });
    }

    private void deactivateStoneCodeListViewOnItemClickListener(int position) {
        String stoneCode = userModelList.get(position).getStoneCode();
        final ActiveApplicationProvider activeApplicationProvider = new ActiveApplicationProvider(ManageStoneCodeActivity.this);

        activeApplicationProvider.deactivate(stoneCode, new StoneCallbackInterface() {
            @Override
            public void onSuccess() {
                stoneCodeListView.setAdapter(populateStoneCodeListView());
                ((ArrayAdapter) stoneCodeListView.getAdapter()).notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(@Nullable StoneStatus stoneStatus) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                String error;
                if (stoneStatus != null) {
                    error = stoneStatus.getMessage();
                } else {
                    error = activeApplicationProvider.getListOfErrors().toString();
                }
                Log.e("DevicesActivity", "onError: " + error);
                Log.e("ManageStoneCodeActivity", "onError: " + error);            }
        });
    }

}
