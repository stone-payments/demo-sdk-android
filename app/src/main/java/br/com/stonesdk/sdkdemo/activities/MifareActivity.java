package br.com.stonesdk.sdkdemo.activities;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

import br.com.stone.sdk.android.error.StoneStatus;
import br.com.stone.sdk.android.error.sdk.mifare.StoneMifareException;
import br.com.stone.sdk.hardware.providers.PosMifareProvider;
import br.com.stonesdk.sdkdemo.R;

/**
 * https://sdkandroid.stone.com.br/reference/provider-de-mifare
 */
public class MifareActivity extends AppCompatActivity {
    private TextView logTextView;
    private PosMifareProvider mifareProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mifare);

        logTextView = findViewById(R.id.logTextView);
        setCardUUIDText("");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mifareProvider != null) mifareProvider.cancelDetection();
    }

    public void detectCard(View view) {
        mifareProvider = new PosMifareProvider(getApplicationContext());

        mifareProvider.detectCards(new PosMifareProvider.MifareCallback() {
            @Override
            public void success(@NonNull byte[] bytes) {
                setCardUUIDText(Arrays.toString(bytes));
                logTextView.append("Cartão detectado: " + Arrays.toString(bytes) + "\n");
            }

            @Override
            public void error(@NonNull StoneStatus stoneStatus) {
                Toast.makeText(MifareActivity.this, "Erro na detecção", Toast.LENGTH_SHORT).show();
                logTextView.append(stoneStatus.getMessage() + "\n");
            }
        });
    }

    public void cancelDetectCard(View view) {
        if (mifareProvider != null) mifareProvider.cancelDetection();
    }

    private void executeBlockRead(int sector, int block) {
        mifareProvider = new PosMifareProvider(getApplicationContext());

        byte[] byteArray = mifareProvider.readBlock((byte) sector, (byte) block);
        setCardUUIDText(Arrays.toString(byteArray));
    }

    private void executeBlockWrite(int sector, int block, byte[] value) {
        mifareProvider = new PosMifareProvider(getApplicationContext());

        try {
            mifareProvider.writeBlock((byte) sector, (byte) block, value);
        } catch (StoneMifareException error) {
            Toast.makeText(MifareActivity.this, "Erro na escrita do bloco", Toast.LENGTH_SHORT).show();
            logTextView.append(error.getMessage());
        }
    }


    public void readBlockDialog(View view) {
        View dialogView = getLayoutInflater().inflate(R.layout.dual_input_dialog, null);
        View keyDialogView = getLayoutInflater().inflate(R.layout.input_dialog, null);
        TextView label1 = dialogView.findViewById(R.id.editTextLabel);
        TextView label2 = dialogView.findViewById(R.id.editTextLabel2);
        TextView sectorTextView = dialogView.findViewById(R.id.editText);
        TextView blockTextView = dialogView.findViewById(R.id.editText2);
        TextView keyDialogTextView = keyDialogView.findViewById(R.id.editText);
        sectorTextView.setInputType(InputType.TYPE_CLASS_NUMBER);
        blockTextView.setInputType(InputType.TYPE_CLASS_NUMBER);
        keyDialogTextView.setText("FFFFFFFFFFFF");
        label1.setText("Sector");
        label2.setText("Block");

        AlertDialog keyDialog = new AlertDialog.Builder(this)
                .setView(keyDialogView)
                .setTitle("Chave do setor")
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    try {
                        int sector = Integer.parseInt(sectorTextView.getText().toString());
                        int block = Integer.parseInt(blockTextView.getText().toString());
                        executeBlockRead(sector, block);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                })
                .setNegativeButton("Cancelar", (dialogInterface, i) -> {
                })
                .create();

        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Definir bloco")
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    try {
                        keyDialog.show();
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                })
                .setNegativeButton("Cancelar", (dialogInterface, i) -> {
                })
                .show();
    }

    public void writeCardDialog(View view) {
        View dialogView = getLayoutInflater().inflate(R.layout.dual_input_dialog, null);
        TextView label1 = dialogView.findViewById(R.id.editTextLabel);
        TextView label2 = dialogView.findViewById(R.id.editTextLabel2);
        TextView sectorTextView = dialogView.findViewById(R.id.editText);
        TextView blockTextView = dialogView.findViewById(R.id.editText2);
        sectorTextView.setInputType(InputType.TYPE_CLASS_NUMBER);
        blockTextView.setInputType(InputType.TYPE_CLASS_NUMBER);
        label1.setText("Sector");
        label2.setText("Block");

        View keyDialogView = getLayoutInflater().inflate(R.layout.input_dialog, null);
        TextView keyDialogTextView = keyDialogView.findViewById(R.id.editText);
        keyDialogTextView.setInputType(InputType.TYPE_CLASS_NUMBER);
        keyDialogTextView.setText("FFFFFFFFFFFF");

        View valueDialogView = getLayoutInflater().inflate(R.layout.input_dialog, null);
        TextView valueDialogTextView = valueDialogView.findViewById(R.id.editText);


        AlertDialog valueDialog = new AlertDialog.Builder(this)
                .setView(valueDialogView)
                .setTitle("Valor que será escrito")
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    try {
                        int sector = Integer.parseInt(sectorTextView.getText().toString());
                        int block = Integer.parseInt(blockTextView.getText().toString());
                        byte[] value = String.format("%-16s", valueDialogTextView.getText().toString()).getBytes();
                        executeBlockWrite(sector, block, value);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                })
                .setNegativeButton("Cancelar", (dialogInterface, i) -> {
                })
                .create();

        AlertDialog keyDialog = new AlertDialog.Builder(this)
                .setView(keyDialogView)
                .setTitle("Chave do setor")
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    try {
                        int sector = Integer.parseInt(sectorTextView.getText().toString());
                        int block = Integer.parseInt(blockTextView.getText().toString());
                        byte[] key = hexStringToByteArray(keyDialogTextView.getText().toString());
                        valueDialog.show();
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                })
                .setNegativeButton("Cancelar", (dialogInterface, i) -> {
                })
                .create();

        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Definir bloco")
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    try {
                        keyDialog.show();
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                })
                .setNegativeButton("Cancelar", (dialogInterface, i) -> {
                })
                .show();
    }


    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }


    private void setCardUUIDText(String uuid) {
        TextView textView = findViewById(R.id.card_uuid_value);
        String uuidText = getString(R.string.uuid_s, uuid);
        textView.setText(uuidText);
    }
}