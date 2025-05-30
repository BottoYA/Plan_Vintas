package com.fatecgru.planvintas.model;

import com.fatecgru.planvintas.R;

public class Hortela extends Planta {

    public Hortela(){

    }

    public Hortela(int id, String nome, int idadeDias, String especie, String ultimaAtualizacao) {
        super(id, nome, idadeDias, especie, ultimaAtualizacao);
    }


    public int getImagemPlanta() {
        String cor = corVaso != null ? corVaso : "marrom"; // Define padrão marrom caso venha nulo
        int idade = idadeDias;

        boolean azul = cor.equalsIgnoreCase("azul");

        if (idade <= 5) {
            return azul ? R.drawable.plantaaazul : R.drawable.plantaa;
        } else if (idade <= 15) {
            return azul ? R.drawable.plantabazul : R.drawable.plantab;
        } else if (idade <= 45) {
            return azul ? R.drawable.plantacazul : R.drawable.plantac;
        } else {
            return azul ? R.drawable.plantadazul : R.drawable.plantad;
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

    public int qtdSolPorDia(int tempMin, int tempMax, boolean chuva) {
        int temperatura = (tempMin+tempMax)/2;

        if (temperatura < 15 && chuva) return 2;
        else return 5;

    }



}
