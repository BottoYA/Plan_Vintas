package com.fatecgru.planvintas.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fatecgru.planvintas.R;

public class Loja extends AppCompatActivity {

    private ImageButton btnVoltar;
    private ImageView imgVasoMarrom, imgVasoAzul;
    private EditText edtValorMarrom, edtValorAzul;
    private TextView txtMoeda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loja);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnVoltar = findViewById(R.id.btnVoltar);
        imgVasoMarrom = findViewById(R.id.imgVasoMarrom);
        imgVasoAzul = findViewById(R.id.imgVasoAzul);
        edtValorMarrom = findViewById(R.id.edtValorMarrom);
        edtValorAzul = findViewById(R.id.edtValorAzul);
        txtMoeda = findViewById(R.id.txtMoeda);

        String nomePlanta = getIntent().getStringExtra("nome_planta");
        int idPlanta = getIntent().getIntExtra("id_planta", -1);
        int idade = getIntent().getIntExtra("idade_planta",0);
        int moedas = getIntent().getIntExtra("moedas_planta",0);

        txtMoeda.setText(String.valueOf(moedas)+"x");


        int Marrom = R.drawable.plantaa;
        if (idade <= 5) {
            Marrom = R.drawable.plantaa;
        } else if (idade <= 15) {
            Marrom = R.drawable.plantab;
        } else if (idade <= 30) {
            Marrom = R.drawable.plantac;
        } else {
            Marrom = R.drawable.plantad;
        }

        imgVasoMarrom.setImageResource(Marrom);

        int Azul = R.drawable.plantaa;
        if (idade <= 5) {
            Azul = R.drawable.plantaaazul;
        } else if (idade <= 15) {
            Azul = R.drawable.plantabazul;
        } else if (idade <= 30) {
            Azul = R.drawable.plantacazul;
        } else {
            Azul = R.drawable.plantadazul;
        }
        imgVasoAzul.setImageResource(Azul);

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}