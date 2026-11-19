package br.com.srcsoftware.api.atlantis.utilidades;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtils {

    private static final Gson GSON_COM_NULOS = new GsonBuilder()
            .serializeNulls() // Força a inclusão de campos com valor null no JSON
            .setPrettyPrinting() // Opcional: formata o JSON (indenta)
            .create();

    /**
     * Converte qualquer objeto para JSON, incluindo atributos nulos.
     * 
     * @param objeto Objeto a ser serializado
     * @return String contendo a estrutura JSON
     */
    public static String converterParaJsonComNulos(Object objeto) {
        if (objeto == null) {
            return "{}";
        }
        return GSON_COM_NULOS.toJson(objeto);
    }
}