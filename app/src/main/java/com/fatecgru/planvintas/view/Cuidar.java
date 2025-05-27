package com.fatecgru.planvintas.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Address;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fatecgru.planvintas.DAO.PlantaDAO;
import com.fatecgru.planvintas.R;
import com.fatecgru.planvintas.model.Hortela;
import com.fatecgru.planvintas.util.ClimaUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.Locale;

public class Cuidar extends AppCompatActivity {

    TextView txtNomePlanta, txtMoedas, txtClima, txtCidade, txtRegar;
    ImageView imagemPlanta;
    ImageButton btnRegar, btnLuz, btnSombra, btnInfo, btnLoja, btnVoltar2;
    Button btnExcluir;

    FusedLocationProviderClient fusedLocationClient;

    ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuidar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializa views
        txtNomePlanta = findViewById(R.id.txtNomePlanta);
        txtMoedas = findViewById(R.id.txtMoedas);
        txtClima = findViewById(R.id.txtClima); // Certifique-se que existe no XML
        txtCidade = findViewById(R.id.txtCidade); // Certifique-se que existe no XML
        imagemPlanta = findViewById(R.id.imagemPlanta);
        btnRegar = findViewById(R.id.btnRegar);
        btnLuz = findViewById(R.id.btnLuz);
        btnSombra = findViewById(R.id.btnSombra);
        btnInfo = findViewById(R.id.btnInfo);
        btnLoja = findViewById(R.id.btnLoja);
        btnVoltar2 = findViewById(R.id.btnVoltar2);
        btnExcluir = findViewById(R.id.btnExcluir);
        txtRegar = findViewById(R.id.txtRegar);




        // Dados da planta vindos da Intent
        String nomePlanta = getIntent().getStringExtra("nome_planta");
        int idPlanta = getIntent().getIntExtra("id_planta", -1);
        int idade = getIntent().getIntExtra("idade_planta", 0);
        int moedas = getIntent().getIntExtra("moedas_planta", 0);

        txtNomePlanta.setText(nomePlanta);
        txtMoedas.setText(moedas + "x");

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

        btnInfo.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Info.class));
        });

        btnLoja.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Loja.class);
            intent.putExtra("id_planta", idPlanta);
            intent.putExtra("idade_planta", idade);
            intent.putExtra("moedas_planta", moedas);
            startActivity(intent);
        });

        btnVoltar2.setOnClickListener(v -> finish());

        btnExcluir.setOnClickListener(v -> {
            PlantaDAO dao = new PlantaDAO(getApplicationContext());
            dao.deletar(idPlanta);
            finish();
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        obterLocalizacaoEClima();
                    } else {
                        Toast.makeText(this, "Permissão de localização negada", Toast.LENGTH_SHORT).show();
                        txtClima.setText("Permissão negada");
                        txtCidade.setText("-");
                    }
                });

        checarPermissaoLocalizacao();
    }

    private void checarPermissaoLocalizacao() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            obterLocalizacaoEClima();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void obterLocalizacaoEClima() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            txtClima.setText("Permissão de localização não concedida");
            txtCidade.setText("-");
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        double lat = location.getLatitude();
                        double lon = location.getLongitude();

                        String cidade = getCidadePorGeocoder(lat, lon);
                        txtCidade.setText(cidade != null ? cidade : "Cidade não encontrada");

                        ClimaUtil.obterClima(lat, lon, new ClimaUtil.ClimaCallback() {
                            @Override
                            public void onResultado(String cidade, String clima, double tempMin, double tempMax) {
                                txtCidade.setText(cidade != null && !cidade.isEmpty() ? cidade : "Cidade não encontrada");
                                txtClima.setText(clima + " - " + tempMin + "°C / " + tempMax + "°C");

                                boolean chuva = clima.toLowerCase().contains("chuva"); // só um exemplo simples

                                Hortela hortela = new Hortela();
                                int regas = hortela.qtdRegaPorDia((int) tempMin, (int) tempMax, chuva);

                                txtRegar.setText("Quantidade de regas hoje: " + regas);
                            }




                        @Override
                            public void onErro(String mensagemErro) {
                                txtClima.setText("Erro ao obter clima");
                                txtCidade.setText("-");
                                txtRegar.setText("Não foi possível calcular regas");
                            }
                        });



                    } else {
                        // Pode tentar getCurrentLocation (API 30+) para fallback
                        txtClima.setText("Localização não encontrada");
                        txtCidade.setText("-");
                    }
                })
                .addOnFailureListener(e -> {
                    txtClima.setText("Erro ao obter localização");
                    txtCidade.setText("-");
                });
    }

    private String getCidadePorGeocoder(double lat, double lon) {
        try {
            Geocoder geocoder = new Geocoder(this, new Locale("pt", "BR"));
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getLocality();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
