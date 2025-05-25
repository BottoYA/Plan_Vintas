package com.fatecgru.planvintas.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fatecgru.planvintas.DAO.PlantaDAO;
import com.fatecgru.planvintas.R;
import com.fatecgru.planvintas.model.Planta;

public class Cuidar extends AppCompatActivity {

    TextView txtNomePlanta, txtMoedas;
    ImageView imagemPlanta;
    ImageButton btnRegar, btnLuz, btnSombra, btnInfo, btnLoja;
    //Button btnExcluir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cuidar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtNomePlanta = findViewById(R.id.txtNomePlanta);
        txtMoedas = findViewById(R.id.txtMoedas);
        imagemPlanta = findViewById(R.id.imagemPlanta);
        btnRegar = findViewById(R.id.btnRegar);
        btnLuz = findViewById(R.id.btnLuz);
        btnSombra = findViewById(R.id.btnSombra);
        btnInfo = findViewById(R.id.btnInfo);
        btnLoja = findViewById(R.id.btnLoja);

        String nomePlanta = getIntent().getStringExtra("nome_planta");
        int idPlanta = getIntent().getIntExtra("id_planta", -1);
        int idade = getIntent().getIntExtra("idade_planta",0);
        int moedas = getIntent().getIntExtra("moedas_planta",0);

        txtNomePlanta.setText(nomePlanta);
        txtMoedas.setText(String.valueOf(moedas)+"x");

        int imagem = R.drawable.plantaa;
        if (idade <= 5) {
            imagem = R.drawable.plantaa;
        } else if (idade <= 15) {
            imagem = R.drawable.plantab;
        } else if (idade <= 30) {
            imagem = R.drawable.plantac;
        } else {
            imagem = R.drawable.plantad;
        }
        imagemPlanta.setImageResource(imagem);

        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getApplicationContext(), Info.class);
                startActivity(it);
            }
        });

        btnLoja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getApplicationContext(), Loja.class);
                startActivity(it);
            }
        });


/*btnExcluir = findViewById(R.id.btnExcluir);
btnExcluir.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        PlantaDAO dao = new PlantaDAO(getApplicationContext());
        dao.deletar(idPlanta);
        finish();
    }
});*/

    }
}