package com.fatecgru.planvintas.model;

import com.fatecgru.planvintas.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Planta {
    private int id;
    private String nome;
     int idadeDias;
    private int moedas;
     String corVaso;
    private String especie;
    private String ultimaAtualizacao;
    private int qtdRegarHoje;
    private int qtdSolHoje;
    private int qtdSombraHoje;
    private List<Missao> missoes;
    private boolean vasoAzul;


    public boolean isVasoAzul() {
        return vasoAzul;
    }

    public void setVasoAzul(boolean vasoAzul) {
        this.vasoAzul = vasoAzul;
    }

    public List<Missao> getMissoes() {
        return missoes;
    }

    public void setMissoes(List<Missao> missoes) {
        this.missoes = missoes;
    }





    public Planta() {
        this.idadeDias = 0;
        this.ultimaAtualizacao = getDataHoje();
    }

    public Planta(int id, String nome, int idadeDias, String especie, String ultimaAtualizacao) {
        this.id = id;
        this.nome = nome;
        this.idadeDias = idadeDias;
        this.especie = especie;
        this.ultimaAtualizacao = ultimaAtualizacao != null ? ultimaAtualizacao : getDataHoje();

        this.qtdRegarHoje = 0;
        this.qtdSolHoje = 0;
        this.qtdSombraHoje = 0;
    }

    // Getters e Setters
    public int getId() { return id; }
    public String getNome() { return nome; }
    public int getIdadeDias() { return idadeDias; }
    public String getEspecie() { return especie; }
    public String getUltimaAtualizacao() { return ultimaAtualizacao; }

    public int getMoedas() {
        return moedas;
    }

    public void setMoedas(int moedas) {
        this.moedas = moedas;
    }

    public String getCorVaso() {
        return corVaso;
    }

    public void setCorVaso(String corVaso) {
        this.corVaso = corVaso;
    }

    public int getQtdRegarHoje() { return qtdRegarHoje; }
    public int getQtdSolHoje() { return qtdSolHoje; }
    public int getQtdSombraHoje() { return qtdSombraHoje; }

    public void setId(int id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setIdadeDias(int idadeDias) { this.idadeDias = idadeDias; }
    public void setEspecie(String especie) { this.especie = especie; }
    public void setUltimaAtualizacao(String ultimaAtualizacao) { this.ultimaAtualizacao = ultimaAtualizacao; }

    public void setQtdRegarHoje(int qtd) { this.qtdRegarHoje = qtd; }
    public void setQtdSolHoje(int qtd) { this.qtdSolHoje = qtd; }
    public void setQtdSombraHoje(int qtd) { this.qtdSombraHoje = qtd; }

    // Método que retorna a data de hoje em String no formato dd/MM/yyyy
    private String getDataHoje() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }

    // Método para calcular a diferença em dias entre duas datas em String
    private int calcularDiferencaDias(String dataAnterior, String dataAtual) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date d1 = sdf.parse(dataAnterior);
            Date d2 = sdf.parse(dataAtual);
            long diff = d2.getTime() - d1.getTime();
            return (int) (diff / (1000 * 60 * 60 * 24));
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Método que atualiza a idade da planta baseado na última atualização
    public void atualizarIdadeSeNecessario() {
        String dataHoje = getDataHoje();
        int diasPassados = calcularDiferencaDias(ultimaAtualizacao, dataHoje);

        if (diasPassados > 0) {
            this.idadeDias += diasPassados;
            this.ultimaAtualizacao = dataHoje;
        }
    }

    // Métodos de ações
    public void regar() {
        if (qtdRegarHoje > 0) qtdRegarHoje--;
    }

    public void colocarSol() {
        if (qtdSolHoje > 0) qtdSolHoje--;
    }

    public void colocarSombra() {
        if (qtdSombraHoje > 0) qtdSombraHoje--;
    }

    // Método para retornar a imagem da planta conforme a idade
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

    // Método para calcular idade baseado em data de criação - pode ficar se quiser
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

    public void adicionarMoedas(int quantidade) {
        this.moedas += quantidade;
    }

}