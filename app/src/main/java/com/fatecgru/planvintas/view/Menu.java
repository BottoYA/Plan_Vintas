package com.fatecgru.planvintas.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fatecgru.planvintas.DAO.PlantaDAO;
import com.fatecgru.planvintas.R;
import com.fatecgru.planvintas.model.Planta;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Menu extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PlantaAdapter adapter;
    private List<Planta> listaDePlantas;
    private ImageButton btnSair, btnCriar;

    private FusedLocationProviderClient fusedLocationClient;
    private static final int REQUEST_LOCATION_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        recyclerView = findViewById(R.id.recyclerView);
        btnCriar = findViewById(R.id.btnCriar);
        btnSair = findViewById(R.id.btnSair);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        listaDePlantas = new ArrayList<>();
        adapter = new PlantaAdapter(this, listaDePlantas);
        recyclerView.setAdapter(adapter);
        carregarPlantas();

        btnSair.setOnClickListener(v -> finish());

        btnCriar.setOnClickListener(v -> mostrarDialogCriarPlanta());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        checarPermissoesLocalizacao();
    }

    private void checarPermissoesLocalizacao() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            obterLocalizacao();
        }
    }

    private void obterLocalizacao() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permissão não concedida, solicitar permissão
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
            return;  // Interrompe para esperar o resultado da permissão
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        Toast.makeText(this, "Lat: " + latitude + ", Lon: " + longitude, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void mostrarDialogCriarPlanta() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.criar, null);

        final EditText edtNome = dialogView.findViewById(R.id.edtNomePlanta);
        final Spinner spinnerEspecie = dialogView.findViewById(R.id.edtEspeciePlanta);
        final EditText edtData = dialogView.findViewById(R.id.edtDataCriacao);

        String[] especies = {"Hortelã"};

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, especies);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEspecie.setAdapter(spinnerAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Criar nova planta");
        builder.setView(dialogView);

        builder.setPositiveButton("Criar", (dialog, which) -> {
            String nome = edtNome.getText().toString().trim();
            String especie = spinnerEspecie.getSelectedItem().toString();
            String dataStr = edtData.getText().toString().trim();

            if (nome.isEmpty() || dataStr.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            int idadeDias;
            try {
                idadeDias = Integer.parseInt(dataStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Idade inválida", Toast.LENGTH_SHORT).show();
                return;
            }

            Planta planta = new Planta();
            planta.setNome(nome);
            planta.setEspecie(especie);
            planta.setIdadeDias(idadeDias);

            PlantaDAO dao = new PlantaDAO(getApplicationContext());
            dao.inserir(planta);

            Toast.makeText(getApplicationContext(), "Planta criada!", Toast.LENGTH_SHORT).show();

            carregarPlantas();
        });

        builder.setNegativeButton("Cancelar", null);

        builder.create().show();
    }

    private void carregarPlantas() {
        PlantaDAO dao = new PlantaDAO(getApplicationContext());
        listaDePlantas.clear();
        listaDePlantas.addAll(dao.listar());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 &&
                    (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                obterLocalizacao();
            } else {
                Toast.makeText(this, "Permissão de localização negada", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
