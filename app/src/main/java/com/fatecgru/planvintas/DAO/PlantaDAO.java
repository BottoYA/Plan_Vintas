package com.fatecgru.planvintas.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fatecgru.planvintas.model.Planta;
import com.fatecgru.planvintas.util.ConnectionFactory;

import java.util.ArrayList;
import java.util.List;

public class PlantaDAO {
    private ConnectionFactory connectionFactory;
    private SQLiteDatabase banco;

    public PlantaDAO(Context context) {
        connectionFactory = new ConnectionFactory(context);
        banco = connectionFactory.getWritableDatabase();
    }

    // 🔸 Inserir planta no banco
    public void inserir(Planta planta) {
        ContentValues valores = new ContentValues();
        valores.put("nome", planta.getNome());
        valores.put("idadeDias", planta.getIdadeDias());
        valores.put("especie", planta.getEspecie());
        valores.put("qtdRegarHoje", planta.getQtdRegarHoje());
        valores.put("qtdSolHoje", planta.getQtdSolHoje());
        valores.put("qtdSombraHoje", planta.getQtdSombraHoje());

        banco.insert("planta", null, valores);
    }

    // 🔸 Buscar todas as plantas cadastradas
    public List<Planta> listar() {
        List<Planta> lista = new ArrayList<>();
        Cursor cursor = banco.query("planta",
                new String[]{"id", "nome", "idadeDias", "especie", "qtdRegarHoje", "qtdSolHoje", "qtdSombraHoje"},
                null, null, null, null, null);

        while (cursor.moveToNext()) {
            Planta p = new Planta(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getInt(2),
                    cursor.getString(3)
            );
            p.setQtdRegarHoje(cursor.getInt(4));
            p.setQtdSolHoje(cursor.getInt(5));
            p.setQtdSombraHoje(cursor.getInt(6));

            lista.add(p);
        }
        cursor.close();
        return lista;
    }

    // 🔸 Atualizar uma planta
    public void atualizar(Planta planta) {
        ContentValues valores = new ContentValues();
        valores.put("nome", planta.getNome());
        valores.put("idadeDias", planta.getIdadeDias());
        valores.put("especie", planta.getEspecie());
        valores.put("qtdRegarHoje", planta.getQtdRegarHoje());
        valores.put("qtdSolHoje", planta.getQtdSolHoje());
        valores.put("qtdSombraHoje", planta.getQtdSombraHoje());

        banco.update("planta", valores, "id = ?", new String[]{String.valueOf(planta.getId())});
    }

    // 🔸 Deletar uma planta
    public void deletar(int id) {
        banco.delete("planta", "id = ?", new String[]{String.valueOf(id)});
    }
}