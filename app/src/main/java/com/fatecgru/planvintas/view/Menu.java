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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.fatecgru.planvintas.DAO.PlantaDAO;
import com.fatecgru.planvintas.R;
import com.fatecgru.planvintas.model.Planta;
import com.fatecgru.planvintas.util.ClimaWorker;
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
    ImageButton btnSair, btnCriar;

    // === Localização ===
    private FusedLocationProviderClient fusedLocationClient;
    private double latitude, longitude;
    private static final int REQUEST_LOCATION_PERMISSION = 100;

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

        // === Localização ===
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
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

    // Método para obter localização
    private void obterLocalizacao() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        // Aqui você pode usar latitude/longitude para buscar o clima
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

        String[] especies = {"Lírio da Paz"};

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

    // Em MainActivity.java ou Menu.java
    public void agendarClimaDiario() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        long delay = calendar.getTimeInMillis() - System.currentTimeMillis();
        if (delay < 0) delay += TimeUnit.DAYS.toMillis(1);

        PeriodicWorkRequest climaRequest = new PeriodicWorkRequest.Builder(ClimaWorker.class, 24, TimeUnit.HOURS)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "ClimaDiario",
                ExistingPeriodicWorkPolicy.REPLACE,
                climaRequest
        );
    }


    // Callback para permissão de localização
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
