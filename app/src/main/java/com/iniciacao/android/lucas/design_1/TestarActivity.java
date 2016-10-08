package com.iniciacao.android.lucas.design_1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.iniciacao.android.lucas.design_1.sensor.Detection;

public class TestarActivity extends AppCompatActivity implements View.OnClickListener{

    private Button button;
    private static final String btn_txt_ativa = "Ativar";
    private static final String btn_txt_desativa = "Desativar";
    private Detection detection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testar);

        button = (Button) findViewById(R.id.button2);
        button.setText(btn_txt_ativa);
        button.setOnClickListener(this);

        detection = new Detection(getApplicationContext()){
            @Override
            public void movimentDetected() {
                Toast.makeText(getApplicationContext(), "Hello from Teste", Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button2) {
            if (((Button) v).getText().equals(btn_txt_ativa)) {
                ((Button) v).setText(btn_txt_desativa);
                detection.changeStateTo(true);
            } else if (((Button) v).getText().equals(btn_txt_desativa)) {
                ((Button) v).setText(btn_txt_ativa);
                detection.changeStateTo(false);
            }
        }
    }
}
