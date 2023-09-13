package br.com.stonesdk.sdkdemo.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import br.com.stone.sdk.payment.utils.StonePayment;
import br.com.stonesdk.sdkdemo.R;

public class DisconnectPinpadActivity extends AppCompatActivity {

    Spinner pinpadsSpinner;
    Button disconnectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disconnect_pinpad);

        pinpadsSpinner = findViewById(R.id.pinpadsSpinner);
        disconnectButton = findViewById(R.id.disconnectButton);

        setPinpadsToSpinner();

        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pinpadSelected = pinpadsSpinner.getSelectedItemPosition();
                StonePayment.removePinpadAtIndex(StonePayment.getPinpadFromListAt(pinpadSelected));
                setPinpadsToSpinner();
            }
        });
    }

    private void setPinpadsToSpinner() {

        List<String> pinpads = new ArrayList<>();
        for (Integer i = 0; i < StonePayment.getPinpadListSize(); i++)
            pinpads.add(StonePayment.getPinpadFromListAt(i).getName());


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, pinpads);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pinpadsSpinner.setAdapter(adapter);

    }
}
