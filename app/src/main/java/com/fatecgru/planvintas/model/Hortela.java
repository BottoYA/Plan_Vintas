package com.fatecgru.planvintas.model;

import com.fatecgru.planvintas.R;

public class Hortela extends Planta {

    public Hortela(){

    }

    public Hortela(int id, String nome, int idadeDias, String especie, String ultimaAtualizacao) {
        super(id, nome, idadeDias, especie, ultimaAtualizacao);
    }

    @Override
    public int getImagemPlanta() {
        boolean vasoAzul = "azul".equalsIgnoreCase(getCorVaso());

        if (getIdadeDias() <= 6) {
            return vasoAzul ? R.drawable.plantaaazul : R.drawable.plantaa;
        } else if (getIdadeDias() <= 15) {
            return vasoAzul ? R.drawable.plantabazul : R.drawable.plantab;
        } else if (getIdadeDias() <= 45) {
            return vasoAzul ? R.drawable.plantacazul : R.drawable.plantac;
        } else {
            return vasoAzul ? R.drawable.plantadazul : R.drawable.plantad;
        }
    }


    public int qtdRegaPorDia(int tempMin, int tempMax, boolean chuva) {
        int temperatura = (tempMin + tempMax) / 2; // média

        if (temperatura <= 14 && chuva) return 1;
        if (temperatura >= 15 && temperatura <= 25 && chuva) return 0;
        if (temperatura >= 15 && temperatura <= 25 && !chuva) return 1;
        if (temperatura >= 25 && temperatura <= 30) return 2;
        if (temperatura > 30 && chuva) return 2;
        if (temperatura > 30 && !chuva) return 3;

        return 1;
    }



}
