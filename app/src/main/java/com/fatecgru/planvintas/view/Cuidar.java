package com.fatecgru.planvintas.view;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Cuidar extends AppCompatActivity {

    TextView txtNomePlanta, txtMoedas, txtClima, txtCidade, txtRegar, txtSol, txtSombra;
    ImageView imagemPlanta;
    ImageButton btnRegar, btnLuz, btnSombra, btnInfo, btnLoja, btnVoltar2;
    //Button btnExcluir;

    FusedLocationProviderClient fusedLocationClient;
    ActivityResultLauncher<String> requestPermissionLauncher;

    private Hortela hortela;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuidar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ----- INICIALIZA VIEWS -----
        txtNomePlanta = findViewById(R.id.txtNomePlanta);
        txtMoedas = findViewById(R.id.txtMoedas);
        txtClima = findViewById(R.id.txtClima);
        txtCidade = findViewById(R.id.txtCidade);
        imagemPlanta = findViewById(R.id.imagemPlanta);
        btnRegar = findViewById(R.id.btnRegar);
        btnLuz = findViewById(R.id.btnLuz);
        btnSombra = findViewById(R.id.btnSombra);
        btnInfo = findViewById(R.id.btnInfo);
        btnLoja = findViewById(R.id.btnLoja);
        btnVoltar2 = findViewById(R.id.btnVoltar2);
        //btnExcluir = findViewById(R.id.btnExcluir);
        txtRegar = findViewById(R.id.txtRegar);
        txtSol = findViewById(R.id.txtSol);
        txtSombra = findViewById(R.id.txtSombra);

        // ----- SHARED PREFERENCES -----
        preferences = getSharedPreferences("MissoesPref", MODE_PRIVATE);
        editor = preferences.edit();

        // ----- RECEBE DADOS DA INTENT -----
        String nomePlanta = getIntent().getStringExtra("nome_planta");
        int idPlanta = getIntent().getIntExtra("id_planta", -1);
        int idade = getIntent().getIntExtra("idade_planta", 0);
        String cor = getIntent().getStringExtra("cor");





        // ----- CRIA PLANTA -----
        hortela = new Hortela();
        hortela.setId(idPlanta);
        hortela.setIdadeDias(idade);
        hortela.setCorVaso(cor);

        PlantaDAO dao = new PlantaDAO(getApplicationContext());
        hortela = (Hortela) dao.buscarPorId(idPlanta);

        txtNomePlanta.setText(hortela.getNome());
        txtMoedas.setText(hortela.getMoedas() + "x");
        imagemPlanta.setImageResource(hortela.getImagemPlanta());


        txtMoedas.setText(hortela.getMoedas() + "x");
        imagemPlanta.setImageResource(hortela.getImagemPlanta());

        // ----- VERIFICA MISSÕES -----
        boolean regaPendente = preferences.getBoolean("regaPendente_" + hortela.getId(), true);
        boolean solPendente = preferences.getBoolean("solPendente_" + hortela.getId(), true);
        boolean sombraPendente = preferences.getBoolean("sombraPendente_" + hortela.getId(), true);

        if (!regaPendente) txtRegar.setTextColor(Color.TRANSPARENT);
        if (!solPendente) txtSol.setTextColor(Color.TRANSPARENT);
        if (!sombraPendente) txtSombra.setTextColor(Color.TRANSPARENT);

        // ----- BOTÕES DE MISSÃO -----
        btnRegar.setOnClickListener(v -> realizarMissao("regaPendente_", txtRegar, "regar"));
        btnLuz.setOnClickListener(v -> realizarMissao("solPendente_", txtSol, "sol"));
        btnSombra.setOnClickListener(v -> realizarMissao("sombraPendente_", txtSombra, "sombra"));

        // ----- OUTROS BOTÕES -----
        btnInfo.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Info.class)));

        btnLoja.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Loja.class);
            intent.putExtra("id_planta", hortela.getId());
            intent.putExtra("idade_planta", hortela.getIdadeDias());
            intent.putExtra("moedas_planta", hortela.getMoedas());
            startActivity(intent);
        });

        btnVoltar2.setOnClickListener(v -> finish());

        /*btnExcluir.setOnClickListener(v -> {
            PlantaDAO dao = new PlantaDAO(getApplicationContext());
            dao.deletar(hortela.getId());
            finish();
        });*/

        // ----- LOCALIZAÇÃO E CLIMA -----
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

        // ----- RESETAR MISSÕES DIARIAS -----
        resetarMissoesSeForNovoDia();
    }

    // ----- FUNÇÃO PARA REALIZAR MISSÃO -----
    private void realizarMissao(String chave, TextView textoMissao, String nomeMissao) {
        boolean missaoPendente = preferences.getBoolean(chave + hortela.getId(), true);

        if (missaoPendente) {
            hortela.adicionarMoedas(5);
            txtMoedas.setText(hortela.getMoedas() + "x");

            PlantaDAO dao = new PlantaDAO(getApplicationContext());
            dao.atualizarMoedas(hortela);

            editor.putBoolean(chave + hortela.getId(), false).apply();

            Toast.makeText(this, "Missão de " + nomeMissao + " concluída! +5 moedas", Toast.LENGTH_SHORT).show();
            textoMissao.setVisibility(View.GONE);
        } else {
            Toast.makeText(this, "Você já concluiu a missão de " + nomeMissao + " hoje.", Toast.LENGTH_SHORT).show();
        }
    }

    // ----- PERMISSÕES -----
    private void checarPermissaoLocalizacao() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            obterLocalizacaoEClima();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    // ----- OBTÉM LOCALIZAÇÃO E CLIMA -----
    private void obterLocalizacaoEClima() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            txtClima.setText("Permissão de localização não concedida");
            txtCidade.setText("-");
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
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

                        boolean chuva = clima.toLowerCase().contains("rain");

                        int regas = hortela.qtdRegaPorDia((int) tempMin, (int) tempMax, chuva);
                        int sol = hortela.qtdSolPorDia((int) tempMin, (int) tempMax, chuva);

                        txtRegar.setText("Quantidade de regas hoje: " + regas);
                        txtSol.setText("Tomar " + sol + "h de sol");
                        txtSombra.setText("Descansar o resto do dia na sombra");
                    }

                    @Override
                    public void onErro(String erro) {
                        txtClima.setText(erro);
                    }
                });
            } else {
                txtClima.setText("Localização não encontrada");
                txtCidade.setText("-");
            }
        });
    }

    // ----- PEGA CIDADE PELO GEOCODER -----
    private String getCidadePorGeocoder(double lat, double lon) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getLocality();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ----- RESETAR MISSÕES DIARIAMENTE -----
    private void resetarMissoesSeForNovoDia() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dataHoje = sdf.format(new Date());
        String dataUltimaMissao = preferences.getString("dataUltimaMissao", "");

        if (!dataHoje.equals(dataUltimaMissao)) {
            editor.putBoolean("regaPendente_" + hortela.getId(), true);
            editor.putBoolean("solPendente_" + hortela.getId(), true);
            editor.putBoolean("sombraPendente_" + hortela.getId(), true);
            editor.putString("dataUltimaMissao", dataHoje);
            editor.apply();
        }
    }
}
