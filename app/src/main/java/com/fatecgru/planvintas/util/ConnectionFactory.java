package com.fatecgru.planvintas.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ConnectionFactory extends SQLiteOpenHelper {

    private static final String NOME_BANCO = "bd_plantinhas";
    private static final int VERSAO = 2;  // Aumenta a versão (era 1)

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
                "ultimaAtualizacao TEXT);";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 🔥 Simples, mas apaga tudo. Usado apenas para testes ou versões iniciais.
        db.execSQL("DROP TABLE IF EXISTS planta");
        onCreate(db);
    }
}



