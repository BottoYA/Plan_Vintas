package com.fatecgru.planvintas.model;

public class LirioDaPazInfo {

    //---------------------FATOS SOBRE A PLANTA PARA POR NO LIVRINHO--------------------------------
    public static String getCuriosidades() {
        return "O Lírio da Paz é uma planta purificadora de ar, fácil de cuidar e floresce na sombra.";
    }

    public static String getDicasGerais() {
        return "Prefere ambientes de meia sombra e gosta de solo úmido, mas não encharcado.";
    }

    public static String getCuidadosDiasQuentes() {
        return "Regue 3 vezes no dia e mantenha em meia sombra, evitando sol direto forte.";
    }

    public static String getCuidadosDiasFrios() {
        return "Regar 1 vez e manter em local iluminado, sem vento frio.";
    }

    public static String getCuidadosDiasChuvosos() {
        return "Apenas 1 rega e manter em local protegido da chuva direta.";
    }

    //---------------------FATOS SOBRE A PLANTA PARA POR NO LIVRINHO--------------------------------




    // Definição das tarefas do dia conforme o clima
    public static void definirTarefas(Planta planta, String clima) {
        switch (clima) {
            case "Clear":
            case "Hot":
                planta.setQtdRegarHoje(3);
                planta.setQtdSolHoje(1);
                planta.setQtdSombraHoje(1);
                break;

            case "Rain":
                planta.setQtdRegarHoje(1);
                planta.setQtdSolHoje(0);
                planta.setQtdSombraHoje(1);
                break;

            case "Clouds":
            default:
                planta.setQtdRegarHoje(2);
                planta.setQtdSolHoje(1);
                planta.setQtdSombraHoje(1);
                break;
        }
    }
}