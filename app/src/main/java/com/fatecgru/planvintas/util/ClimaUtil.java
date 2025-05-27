package com.fatecgru.planvintas.util;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ClimaUtil {

    public interface ClimaCallback {
        void onResultado(String cidade, String clima, double tempMin, double tempMax);
        void onErro(String mensagemErro);
    }

    public static void obterClima(double lat, double lon, ClimaCallback callback) {
        new Thread(() -> {
            try {
                String apiKey = "5884aba1d9517520f05d9aacf13842a8"; // Substitua aqui pela sua chave
                String urlStr = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat +
                        "&lon=" + lon + "&units=metric&lang=pt_br&appid=" + apiKey;

                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    throw new Exception("Erro na conexão: " + responseCode);
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }
                reader.close();

                JSONObject jsonObject = new JSONObject(json.toString());
                String cidade = jsonObject.getString("name"); // Aqui pega o nome da cidade direto do JSON
                String clima = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
                double tempMin = jsonObject.getJSONObject("main").getDouble("temp_min");
                double tempMax = jsonObject.getJSONObject("main").getDouble("temp_max");

                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onResultado(cidade, clima, tempMin, tempMax));

            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onErro(e.getMessage()));
            }
        }).start();
    }
}
