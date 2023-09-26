package br.com.stonesdk.sdkdemo.activities;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

import br.com.stone.posandroid.hal.api.mifare.MifareKeyType;
import br.com.stone.posandroid.providers.PosMifareProvider;
import br.com.stonesdk.sdkdemo.R;
import stone.application.interfaces.StoneCallbackInterface;

/**
 *  https://sdkandroid.stone.com.br/reference/provider-de-mifare
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
        if(mifareProvider != null) mifareProvider.cancelDetection();
    }

    /**
     *  Exemplo de detecção de cartão, pegando o UUID.
     */
    public void detectCard(View view){
        mifareProvider = new PosMifareProvider(getApplicationContext());

        mifareProvider.setConnectionCallback(new StoneCallbackInterface() {
            @Override
            public void onSuccess() {
                setCardUUIDText(Arrays.toString(mifareProvider.getCardUUID()));
                logTextView.append("Cartão detectado: " + Arrays.toString(mifareProvider.getCardUUID()) + "\n");
            }

            @Override
            public void onError() {
                runOnUiThread(() -> {
                    Toast.makeText(MifareActivity.this, "Erro na detecção", Toast.LENGTH_SHORT).show();
                    logTextView.append(mifareProvider.getListOfErrors().toString() + "\n");
                });
            }
        });
        mifareProvider.execute();
    }

    public void cancelDetectCard(View view) {
        if(mifareProvider != null) mifareProvider.cancelDetection();
    }

    /**
     *  Exemplo de leitura do valor de um bloco.
     * @param sector Número do setor.
     * @param block Número do bloco relativo ao setor (mínimo 0, máximo 3).
     * @param key Chave de autenticação do bloco.
     */
    private void executeBlockRead(int sector, int block, byte[] key) {
        PosMifareProvider mifareProvider = new PosMifareProvider(getApplicationContext());
        mifareProvider.setConnectionCallback(new StoneCallbackInterface() {
            @Override
            public void onSuccess() {
                setCardUUIDText(Arrays.toString(mifareProvider.getCardUUID()));
                logTextView.append("Cartão detectado: " + Arrays.toString(mifareProvider.getCardUUID()) + "\n");

                // Autentica o setor
                try {
                    mifareProvider.authenticateSector(MifareKeyType.TypeA, key, (byte) sector);
                } catch (PosMifareProvider.MifareException e) {
                    Toast.makeText(MifareActivity.this, "Erro na autenticação", Toast.LENGTH_SHORT).show();
                    logTextView.append(mifareProvider.getListOfErrors().toString() + "\n");
                    return;
                }

                // Lê o valor de um bloco no setor
                // O valor lido será escrito no byteArray caso a leitura ocorra com sucesso
                byte[] byteArray = new byte[16];
                try {
                    mifareProvider.readBlock((byte) sector, (byte) block, byteArray);
                } catch (PosMifareProvider.MifareException e) {
                    Toast.makeText(MifareActivity.this, "Erro na leitura do bloco", Toast.LENGTH_SHORT).show();
                    logTextView.append(e.getErrorEnum().name() + "\n");
                    return;
                }

                Toast.makeText(MifareActivity.this, "Valor lido: " + Arrays.toString(byteArray), Toast.LENGTH_SHORT).show();
                logTextView.append("Valor lido: " + new String(byteArray) + "\n");
            }

            @Override
            public void onError() {
                runOnUiThread(() -> {
                    Toast.makeText(MifareActivity.this, "Erro na detecção", Toast.LENGTH_SHORT).show();
                    logTextView.append(mifareProvider.getListOfErrors().toString() + "\n");
                });
            }
        });

        mifareProvider.execute();
    }

    /**
     *  Exemplo de escrita em um bloco.
     * @param sector Número do setor.
     * @param block Número do bloco relativo ao setor (mínimo 0, máximo 3).
     * @param key Chave de autenticação do bloco.
     * @param value Valor que será gravado no bloco. (byte[16]).
     */
    private void executeBlockWrite(int sector, int block, byte[] key, byte[] value) {
        PosMifareProvider mifareProvider = new PosMifareProvider(getApplicationContext());
        mifareProvider.setConnectionCallback(new StoneCallbackInterface() {
            @Override
            public void onSuccess() {
                setCardUUIDText(Arrays.toString(mifareProvider.getCardUUID()));
                logTextView.append("Cartão detectado: " + Arrays.toString(mifareProvider.getCardUUID()) + "\n");

                // Autentica o setor
                try {
                    mifareProvider.authenticateSector(MifareKeyType.TypeA, key, (byte) sector);
                } catch (PosMifareProvider.MifareException e) {
                    Toast.makeText(MifareActivity.this, "Erro na autenticação", Toast.LENGTH_SHORT).show();
                    logTextView.append(mifareProvider.getListOfErrors().toString() + "\n");
                    return;
                }

                // Lê o valor de um bloco no setor

                try {
                    mifareProvider.writeBlock((byte) sector, (byte) block, value);
                } catch (PosMifareProvider.MifareException e) {
                    Toast.makeText(MifareActivity.this, "Erro na escrita do bloco", Toast.LENGTH_SHORT).show();
                    logTextView.append(e.getErrorEnum().name() + "\n");
                    return;
                }

                Toast.makeText(MifareActivity.this, "Sucesso", Toast.LENGTH_SHORT).show();
                logTextView.append("Valor gravado: " + new String(value) + "\n");
            }

            @Override
            public void onError() {
                runOnUiThread(() -> {
                    Toast.makeText(MifareActivity.this, "Erro na detecção", Toast.LENGTH_SHORT).show();
                    logTextView.append(mifareProvider.getListOfErrors().toString() + "\n");
                });
            }
        });

        mifareProvider.execute();
    }


    public void readBlockDialog(View view){
        // Olhe o método executeReadBlock para ver a implementação do provider
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
                        byte[] key = hexStringToByteArray(keyDialogTextView.getText().toString());
                        executeBlockRead(sector, block, key);
                    } catch (NumberFormatException e) {

                    }

                })
                .setNegativeButton("Cancelar", (dialogInterface, i) -> {

                }).create();

        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Definir bloco")
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    try {
                        keyDialog.show();
                    } catch (NumberFormatException e) {

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
                        byte[] key = hexStringToByteArray(keyDialogTextView.getText().toString());
                        // Valor que será escrito precisa ter 16 bytes
                        byte[] value = String.format("%-16s", valueDialogTextView.getText().toString()).getBytes();
                        executeBlockWrite(sector, block, key, value);
                    } catch (NumberFormatException e) {

                    }

                }).setNegativeButton("Cancelar", (dialogInterface, i) -> {

                }).create();



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

                    }

                })
                .setNegativeButton("Cancelar", (dialogInterface, i) -> {

                }).create();

        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Definir bloco")
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    try {
                        keyDialog.show();
                    } catch (NumberFormatException e) {

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
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }


    private void setCardUUIDText(String uuid) {
        TextView textView = findViewById(R.id.card_uuid_value);
        String uuidText = getString(R.string.uuid_s,  uuid);
        textView.setText(uuidText);
    }
}