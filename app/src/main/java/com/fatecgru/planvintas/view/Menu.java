package com.fatecgru.planvintas.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fatecgru.planvintas.DAO.PlantaDAO;
import com.fatecgru.planvintas.R;
import com.fatecgru.planvintas.model.Planta;

import java.util.ArrayList;
import java.util.List;

public class Menu extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PlantaAdapter adapter;
    private List<Planta> listaDePlantas;
    ImageButton btnSair, btnCriar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        recyclerView = findViewById(R.id.recyclerView);
        btnCriar = findViewById(R.id.btnCriar);
        btnSair = findViewById(R.id.btnSair);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        listaDePlantas = new ArrayList<>();
        adapter = new PlantaAdapter(this, listaDePlantas);
        recyclerView.setAdapter(adapter);
        carregarPlantas();

        btnSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnCriar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogCriarPlanta();
            }
        });



    }


    private void mostrarDialogCriarPlanta() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.criar, null);

        final EditText edtNome = dialogView.findViewById(R.id.edtNomePlanta);
        final Spinner spinnerEspecie = dialogView.findViewById(R.id.edtEspeciePlanta);
        final EditText edtData = dialogView.findViewById(R.id.edtDataCriacao);

        // Criar lista de espécies (aqui só o Lírio da Paz, mas pode adicionar outras)
        String[] especies = {"Lírio da Paz"};

        // Adaptador para o Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, especies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEspecie.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Criar nova planta");
        builder.setView(dialogView);

        builder.setPositiveButton("Criar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nome = edtNome.getText().toString().trim();
                String especie = spinnerEspecie.getSelectedItem().toString();
                int dataCriacao = Integer.parseInt(edtData.getText().toString().trim());

                Planta planta = new Planta();
                planta.setNome(nome);
                planta.setEspecie(especie);
                planta.setIdadeDias(dataCriacao);

                PlantaDAO dao = new PlantaDAO(getApplicationContext());
                dao.inserir(planta);

                Toast.makeText(getApplicationContext(), "Planta criada!", Toast.LENGTH_SHORT).show();

                carregarPlantas();
            }
        });

        builder.setNegativeButton("Cancelar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void carregarPlantas() {
        PlantaDAO dao = new PlantaDAO(getApplicationContext());
        listaDePlantas.clear();
        listaDePlantas.addAll(dao.listar());
        adapter.notifyDataSetChanged();
    }
}