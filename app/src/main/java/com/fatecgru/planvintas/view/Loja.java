package com.fatecgru.planvintas.view;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fatecgru.planvintas.R;
import com.fatecgru.planvintas.DAO.PlantaDAO;
import com.fatecgru.planvintas.model.Planta;

public class Loja extends AppCompatActivity {

    private ImageButton btnVoltar;
    private ImageView imgVasoMarrom, imgVasoAzul;
    private EditText edtValorMarrom, edtValorAzul;
    private TextView txtMoeda;

    private PlantaDAO plantaDAO;
    private Planta planta;

    private boolean vasoAzulComprado = false;
    private boolean usandoVasoAzul = false;

    private final int PRECO_VASO_AZUL = 5; // 💰 Defina aqui o preço

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

        // 🔗 Ligação dos componentes
        btnVoltar = findViewById(R.id.btnVoltar);
        imgVasoMarrom = findViewById(R.id.imgVasoMarrom);
        imgVasoAzul = findViewById(R.id.imgVasoAzul);
        edtValorMarrom = findViewById(R.id.edtValorMarrom);
        edtValorAzul = findViewById(R.id.edtValorAzul);
        txtMoeda = findViewById(R.id.txtMoeda);

        // 📦 Recebe dados da planta
        int idPlanta = getIntent().getIntExtra("id_planta", -1);
        plantaDAO = new PlantaDAO(this);
        planta = plantaDAO.buscarPorId(idPlanta);

        if (planta == null) {
            Toast.makeText(this, "Erro ao carregar planta!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 🔥 Estado inicial
        vasoAzulComprado = planta.isVasoAzul();
        usandoVasoAzul = vasoAzulComprado; // Se já tem, já está usando; se não, começa no marrom

        txtMoeda.setText(planta.getMoedas() + "x");

        // 🎨 Atualiza imagens conforme idade
        atualizarImagens(planta.getIdadeDias());

        // 🔵 Clique no vaso azul
        imgVasoAzul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!vasoAzulComprado) {
                    if (planta.getMoedas() >= PRECO_VASO_AZUL) {
                        // Compra
                        planta.setMoedas(planta.getMoedas() - PRECO_VASO_AZUL);
                        planta.setVasoAzul(true);
                        planta.setCorVaso("azul");
                        plantaDAO.atualizar(planta);

                        vasoAzulComprado = true;
                        usandoVasoAzul = true;

                        txtMoeda.setText(planta.getMoedas() + "x");
                        Toast.makeText(Loja.this, "Vaso azul comprado e equipado!", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(Loja.this, "Moedas insuficientes!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Já tem → apenas seleciona
                    usandoVasoAzul = true;
                    atualizarImagens(planta.getIdadeDias());

                    Toast.makeText(Loja.this, "Vaso azul selecionado!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 🟤 Clique no vaso marrom
        imgVasoMarrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usandoVasoAzul = false;
                atualizarImagens(planta.getIdadeDias());
                Toast.makeText(Loja.this, "Vaso marrom selecionado!", Toast.LENGTH_SHORT).show();
            }
        });

        // 🔙 Voltar
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plantaDAO.close();
                finish();
            }
        });
    }

    private void atualizarImagens(int idade) {
        int imgMarrom, imgAzul, imgSelecionada;

        if (idade <= 5) {
            imgMarrom = R.drawable.plantaa;
            imgAzul = R.drawable.plantaaazul;
        } else if (idade <= 15) {
            imgMarrom = R.drawable.plantab;
            imgAzul = R.drawable.plantabazul;
        } else if (idade <= 30) {
            imgMarrom = R.drawable.plantac;
            imgAzul = R.drawable.plantacazul;
        } else {
            imgMarrom = R.drawable.plantad;
            imgAzul = R.drawable.plantadazul;
        }

        imgVasoMarrom.setImageResource(imgMarrom);
        imgVasoAzul.setImageResource(imgAzul);

        // Aqui você define qual está selecionado para exibir
        if (usandoVasoAzul) {
            imgVasoAzul.setAlpha(1f);
            imgVasoMarrom.setAlpha(0.5f);
        } else {
            imgVasoAzul.setAlpha(0.5f);
            imgVasoMarrom.setAlpha(1f);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        plantaDAO.close();
    }
}
