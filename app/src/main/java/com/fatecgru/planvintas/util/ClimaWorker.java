package com.fatecgru.planvintas.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ClimaWorker extends Worker {

    public ClimaWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            // Obter localização salva
            SharedPreferences prefs = getApplicationContext().getSharedPreferences("LocalizacaoPrefs", Context.MODE_PRIVATE);
            double lat = Double.longBitsToDouble(prefs.getLong("latitude", Double.doubleToLongBits(0.0)));
            double lon = Double.longBitsToDouble(prefs.getLong("longitude", Double.doubleToLongBits(0.0)));

            if (lat == 0.0 && lon == 0.0) {
                Log.w("ClimaWorker", "Coordenadas não definidas");
                return Result.failure();
            }

            // Versão síncrona para o Worker
            String apiKey = "SUA_API_KEY";
            String urlStr = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat +
                    "&lon=" + lon + "&units=metric&lang=pt_br&appid=" + apiKey;

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            reader.close();

            JSONObject jsonObject = new JSONObject(json.toString());
            String clima = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
            double tempMin = jsonObject.getJSONObject("main").getDouble("temp_min");
            double tempMax = jsonObject.getJSONObject("main").getDouble("temp_max");

            // Salvar resultados
            SharedPreferences climaPrefs = getApplicationContext().getSharedPreferences("ClimaPrefs", Context.MODE_PRIVATE);
            climaPrefs.edit()
                    .putString("descricao", clima)
                    .putFloat("tempMin", (float) tempMin)
                    .putFloat("tempMax", (float) tempMax)
                    .apply();

            Log.d("ClimaWorker", "Dados climáticos atualizados");
            return Result.success();

        } catch (Exception e) {
            Log.e("ClimaWorker", "Erro ao obter clima: " + e.getMessage());
            return Result.retry();
        }
    }
}