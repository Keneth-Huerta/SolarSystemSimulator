package com.solarsim.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase para cargar y gestionar datos astronómicos desde archivos JSON.
 * Proporciona métodos para acceder a la información de planetas y otros cuerpos celestes.
 */
public class AstronomicalData {

    private Map<String, JsonObject> planetData;
    private Gson gson;

    /**
     * Constructor que inicializa y carga datos astronómicos desde un archivo.
     * @param filePath Ruta al archivo JSON con los datos astronómicos
     */
    public AstronomicalData(String filePath) {
        planetData = new HashMap<>();
        gson = new Gson();
        loadData(filePath);
    }

    /**
     * Carga los datos astronómicos desde un archivo JSON.
     * @param filePath Ruta al archivo JSON
     */
    private void loadData(String filePath) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JsonArray planetsArray = JsonParser.parseString(content).getAsJsonArray();
            for (JsonElement element : planetsArray) {
                JsonObject planet = element.getAsJsonObject();
                String name = planet.get("name").getAsString();
                planetData.put(name, planet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Obtiene los datos de un planeta específico como objeto JSON.
     * @param planetName Nombre del planeta
     * @return Objeto JSON con los datos del planeta, o null si no existe
     */
    public JsonObject getPlanetData(String planetName) {
        return planetData.get(planetName);
    }

    /**
     * Obtiene todos los datos de planetas disponibles.
     * @return Mapa con todos los datos planetarios
     */
    public Map<String, JsonObject> getAllPlanetData() {
        return planetData;
    }
    
    /**
     * Obtiene los datos de un planeta específico como Map.
     * @param planetName Nombre del planeta
     * @return Map con los datos del planeta, o null si no existe
     */
    public Map<String, Object> getPlanetDataAsMap(String planetName) {
        JsonObject jsonObject = planetData.get(planetName);
        if (jsonObject == null) {
            return null;
        }
        return gson.fromJson(jsonObject, Map.class);
    }
}