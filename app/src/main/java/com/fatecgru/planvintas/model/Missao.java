package com.fatecgru.planvintas.model;

public class Missao {
    private int id;
    private String descricao;
    private int recompensa;
    private String status; // "pendente", "concluida", etc.
    private int plantaId;  // Relacionamento com Planta

    // Construtores, getters e setters
    public Missao() {}

    public Missao(int id, String descricao, int recompensa, String status, int plantaId) {
        this.id = id;
        this.descricao = descricao;
        this.recompensa = recompensa;
        this.status = status;
        this.plantaId = plantaId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getRecompensa() {
        return recompensa;
    }

    public void setRecompensa(int recompensa) {
        this.recompensa = recompensa;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPlantaId() {
        return plantaId;
    }

    public void setPlantaId(int plantaId) {
        this.plantaId = plantaId;
    }
}
