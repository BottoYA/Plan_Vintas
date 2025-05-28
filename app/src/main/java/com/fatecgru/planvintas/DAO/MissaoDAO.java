package com.fatecgru.planvintas.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fatecgru.planvintas.model.Missao;
import com.fatecgru.planvintas.util.ConnectionFactory;

import java.util.ArrayList;
import java.util.List;

public class MissaoDAO {
    private ConnectionFactory connectionFactory;
    private SQLiteDatabase banco;

    public MissaoDAO(Context context) {
        connectionFactory = new ConnectionFactory(context);
        banco = connectionFactory.getWritableDatabase();
    }

    public void inserir(Missao missao) {
        ContentValues valores = new ContentValues();
        valores.put("descricao", missao.getDescricao());
        valores.put("recompensa", missao.getRecompensa());
        valores.put("status", missao.getStatus());
        valores.put("plantaId", missao.getPlantaId());

        banco.insert("missao", null, valores);
    }

    public List<Missao> listarPorPlanta(int plantaId) {
        List<Missao> lista = new ArrayList<>();
        Cursor cursor = banco.query("missao",
                new String[]{"id", "descricao", "recompensa", "status", "plantaId"},
                "plantaId = ?", new String[]{String.valueOf(plantaId)},
                null, null, null);

        while (cursor.moveToNext()) {
            Missao m = new Missao(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getInt(2),
                    cursor.getString(3),
                    cursor.getInt(4)
            );
            lista.add(m);
        }
        cursor.close();
        return lista;
    }

    public void atualizar(Missao missao) {
        ContentValues valores = new ContentValues();
        valores.put("descricao", missao.getDescricao());
        valores.put("recompensa", missao.getRecompensa());
        valores.put("status", missao.getStatus());
        valores.put("plantaId", missao.getPlantaId());

        banco.update("missao", valores, "id = ?", new String[]{String.valueOf(missao.getId())});
    }

    public void deletar(int id) {
        banco.delete("missao", "id = ?", new String[]{String.valueOf(id)});
    }
}
