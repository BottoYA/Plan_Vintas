package com.fatecgru.planvintas.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ConnectionFactory extends SQLiteOpenHelper {

    private static final String NOME_BANCO = "bd_plantinhas";
    private static final int VERSAO = 5;

    public ConnectionFactory(Context context) {
        super(context, NOME_BANCO, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE planta (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT NOT NULL, " +
                "idadeDias INTEGER, " +
                "especie TEXT, " +
                "moedas INTEGER, " +
                "qtdRegarHoje INTEGER, " +
                "qtdSolHoje INTEGER, " +
                "qtdSombraHoje INTEGER, " +
                "corVaso TEXT," +
                "ultimaAtualizacao TEXT, "+
                "azul BOOLEAN)";

        db.execSQL(sql);

        String sqlMissao = "CREATE TABLE missao (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "descricao TEXT NOT NULL, " +
                "recompensa INTEGER NOT NULL, " +
                "status TEXT NOT NULL," +       // Exemplo: 'pendente', 'concluida', etc.
                "plantaId INTEGER NOT NULL," + // FK para planta
                "FOREIGN KEY(plantaId) REFERENCES planta(id)" +
                ");";
        db.execSQL(sqlMissao);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE planta ADD COLUMN azul BOOLEAN DEFAULT 0");
        }

        if (oldVersion < 5) {
            String sqlMissao = "CREATE TABLE missao (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "descricao TEXT NOT NULL, " +
                    "recompensa INTEGER NOT NULL, " +
                    "status TEXT NOT NULL," +
                    "plantaId INTEGER NOT NULL," +
                    "FOREIGN KEY(plantaId) REFERENCES planta(id)" +
                    ");";
            db.execSQL(sqlMissao);
        }
    }



}



