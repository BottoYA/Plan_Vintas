package com.fatecgru.planvintas.model;

import com.fatecgru.planvintas.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Planta {
    private int id;
    private String nome;
    private int idadeDias;
    private String especie; // Ex: "Lirio da Paz"

    private int qtdRegarHoje;
    private int qtdSolHoje;
    private int qtdSombraHoje;
    private int moedas = 0;

    // Construtor
    public Planta() {

    }

    public Planta(int id, String nome, int idadeDias, String especie) {
        this.id = id;
        this.nome = nome;
        this.idadeDias = idadeDias;
        this.especie = especie;

        // As tarefas começam zeradas e são definidas de acordo com o clima do dia
        this.qtdRegarHoje = 0;
        this.qtdSolHoje = 0;
        this.qtdSombraHoje = 0;
    }

    // Getters e Setters
    public int getMoedas() { return moedas; }
    public void setMoedas(int moedas) { this.moedas = moedas; }

    public void adicionarMoedas(int valor) {
        this.moedas += valor;
    }

    public boolean gastarMoedas(int valor) {
        if (this.moedas >= valor) {
            this.moedas -= valor;
            return true;
        } else {
            return false;
        }
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public void setIdadeDias(int idadeDias) {
        this.idadeDias = idadeDias;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public int getIdadeDias() {
        return idadeDias;
    }

    public String getEspecie() {
        return especie;
    }

    public int getQtdRegarHoje() {
        return qtdRegarHoje;
    }

    public int getQtdSolHoje() {
        return qtdSolHoje;
    }

    public int getQtdSombraHoje() {
        return qtdSombraHoje;
    }

    public void setQtdRegarHoje(int qtd) {
        this.qtdRegarHoje = qtd;
    }

    public void setQtdSolHoje(int qtd) {
        this.qtdSolHoje = qtd;
    }

    public void setQtdSombraHoje(int qtd) {
        this.qtdSombraHoje = qtd;
    }

    public void incrementarIdade() {
        this.idadeDias++;
    }

    // Ações
    public void regar() {
        if (qtdRegarHoje > 0) qtdRegarHoje--;
    }

    public void colocarSol() {
        if (qtdSolHoje > 0) qtdSolHoje--;
    }

    public void colocarSombra() {
        if (qtdSombraHoje > 0) qtdSombraHoje--;
    }

    public int getImagemPlanta() {
        if (idadeDias <= 5) {
            return R.drawable.plantaa;
        } else if (idadeDias <= 15) {
            return R.drawable.plantab;
        } else if (idadeDias <= 30) {
            return R.drawable.plantac;
        } else {
            return R.drawable.plantad;
        }
    }

    public int calcularIdadeEmDias(String dataCriacao) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        try {
            Date dataCriada = sdf.parse(dataCriacao);
            Date hoje = new Date();

            long diferenca = hoje.getTime() - dataCriada.getTime();
            return (int) (diferenca / (1000 * 60 * 60 * 24));
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
