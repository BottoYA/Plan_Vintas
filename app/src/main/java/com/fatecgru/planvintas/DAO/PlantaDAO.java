package com.fatecgru.planvintas.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fatecgru.planvintas.model.Hortela;
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

    public void inserir(Planta planta) {
        ContentValues valores = new ContentValues();
        valores.put("nome", planta.getNome());
        valores.put("idadeDias", planta.getIdadeDias());
        valores.put("especie", planta.getEspecie());
        valores.put("moedas", planta.getMoedas());
        valores.put("qtdRegarHoje", planta.getQtdRegarHoje());
        valores.put("qtdSolHoje", planta.getQtdSolHoje());
        valores.put("qtdSombraHoje", planta.getQtdSombraHoje());
        valores.put("corVaso", planta.getCorVaso());
        valores.put("ultimaAtualizacao", planta.getUltimaAtualizacao());
        valores.put("azul", planta.isVasoAzul());

        banco.insert("planta", null, valores);
    }

    public List<Planta> listar() {
        List<Planta> lista = new ArrayList<>();
        Cursor cursor = banco.query(
                "planta",
                new String[]{"id", "nome", "idadeDias", "especie", "moedas",
                        "qtdRegarHoje", "qtdSolHoje", "qtdSombraHoje",
                        "corVaso", "ultimaAtualizacao", "azul"},
                null, null, null, null, null
        );

        while (cursor.moveToNext()) {
            String especie = cursor.getString(3);
            String ultimaAtualizacao = cursor.getString(9);

            Planta p;

            if ("Hortelã".equalsIgnoreCase(especie)) {
                Hortela hortela = new Hortela(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        especie,
                        ultimaAtualizacao
                );
                hortela.setCorVaso(cursor.getString(8));
                p = hortela;
            } else {
                Planta planta = new Planta(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        especie,
                        ultimaAtualizacao
                );
                planta.setCorVaso(cursor.getString(8));
                p = planta;
            }

            p.setMoedas(cursor.getInt(4));
            p.setQtdRegarHoje(cursor.getInt(5));
            p.setQtdSolHoje(cursor.getInt(6));
            p.setQtdSombraHoje(cursor.getInt(7));

            // ✅ Pega o atributo azul
            boolean vasoAzul = cursor.getInt(10) == 1;
            p.setVasoAzul(vasoAzul);

            p.atualizarIdadeSeNecessario();
            atualizar(p);

            lista.add(p);
        }
        cursor.close();
        return lista;
    }

    public void atualizar(Planta planta) {
        ContentValues valores = new ContentValues();
        valores.put("nome", planta.getNome());
        valores.put("idadeDias", planta.getIdadeDias());
        valores.put("especie", planta.getEspecie());
        valores.put("moedas", planta.getMoedas());
        valores.put("qtdRegarHoje", planta.getQtdRegarHoje());
        valores.put("qtdSolHoje", planta.getQtdSolHoje());
        valores.put("qtdSombraHoje", planta.getQtdSombraHoje());
        valores.put("corVaso", planta.getCorVaso());
        valores.put("ultimaAtualizacao", planta.getUltimaAtualizacao());
        valores.put("azul", planta.isVasoAzul());

        banco.update("planta", valores, "id = ?", new String[]{String.valueOf(planta.getId())});
    }

    public boolean atualizarMoedas(Hortela hortela) {
        ContentValues valores = new ContentValues();
        valores.put("moedas", hortela.getMoedas());

        int rows = banco.update("planta", valores, "id = ?", new String[]{String.valueOf(hortela.getId())});
        return rows > 0;
    }

    public Planta buscarPorId(int id) {
        Cursor cursor = banco.query(
                "planta",
                new String[]{"id", "nome", "idadeDias", "especie", "moedas",
                        "qtdRegarHoje", "qtdSolHoje", "qtdSombraHoje",
                        "corVaso", "ultimaAtualizacao", "azul"},
                "id = ?",
                new String[]{String.valueOf(id)},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            String especie = cursor.getString(3);
            String ultimaAtualizacao = cursor.getString(9);

            Planta p;

            if ("Hortelã".equalsIgnoreCase(especie)) {
                Hortela hortela = new Hortela(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        especie,
                        ultimaAtualizacao
                );
                hortela.setCorVaso(cursor.getString(8));
                p = hortela;
            } else {
                Planta planta = new Planta(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        especie,
                        ultimaAtualizacao
                );
                planta.setCorVaso(cursor.getString(8));
                p = planta;
            }

            p.setMoedas(cursor.getInt(4));
            p.setQtdRegarHoje(cursor.getInt(5));
            p.setQtdSolHoje(cursor.getInt(6));
            p.setQtdSombraHoje(cursor.getInt(7));

            // ✅ Pega o atributo azul
            boolean vasoAzul = cursor.getInt(10) == 1;
            p.setVasoAzul(vasoAzul);

            p.atualizarIdadeSeNecessario();
            atualizar(p);

            cursor.close();
            return p;
        }

        cursor.close();
        return null;
    }

    public void deletar(int id) {
        banco.delete("planta", "id = ?", new String[]{String.valueOf(id)});
    }

    public void close() {
        if (banco != null && banco.isOpen()) {
            banco.close();
        }
    }
}
