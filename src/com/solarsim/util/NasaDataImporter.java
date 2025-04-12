package com.solarsim.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.solarsim.model.Planet;

public class NasaDataImporter {

    private static final String NASA_API_URL = "https://api.le-systeme-solaire.net/rest/bodies/";
    private Gson gson;

    public NasaDataImporter() {
        this.gson = new Gson();
    }

    public JsonArray fetchAstronomicalData() throws IOException {
        URL url = new URL(NASA_API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() != 200) {
            throw new IOException("Failed to fetch data from NASA API: " + connection.getResponseMessage());
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
        return jsonResponse.getAsJsonArray("bodies");
    }

    public List<Map<String, Object>> processAstronomicalData() {
        List<Map<String, Object>> planetsList = new ArrayList<>();
        try {
            JsonArray bodies = fetchAstronomicalData();
            for (JsonElement element : bodies) {
                JsonObject body = element.getAsJsonObject();
                
                // Convert JsonObject to Map
                Map<String, Object> planetData = gson.fromJson(body, Map.class);
                planetsList.add(planetData);
                
                // Display some basic info
                System.out.println("Name: " + planetData.get("englishName"));
                if (planetData.containsKey("mass") && planetData.get("mass") != null) {
                    Map<String, Object> massInfo = (Map<String, Object>) planetData.get("mass");
                    if (massInfo.containsKey("massValue") && massInfo.containsKey("massExponent")) {
                        double massValue = ((Number) massInfo.get("massValue")).doubleValue();
                        double massExponent = ((Number) massInfo.get("massExponent")).doubleValue();
                        double totalMass = massValue * Math.pow(10, massExponent);
                        System.out.println("Mass: " + totalMass + " kg");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return planetsList;
    }
    
    public List<Planet> createPlanetsFromData() {
        List<Planet> planets = new ArrayList<>();
        try {
            JsonArray bodies = fetchAstronomicalData();
            for (JsonElement element : bodies) {
                JsonObject body = element.getAsJsonObject();
                
                // Only include planets (not moons, asteroids, etc.)
                if (body.has("isPlanet") && body.get("isPlanet").getAsBoolean()) {
                    String name = body.has("englishName") ? 
                                 body.get("englishName").getAsString() : "Unknown";
                    
                    // Default values
                    double mass = 0;
                    double radius = 0;
                    double orbitalRadius = 0;
                    double orbitalPeriod = 0;
                    
                    // Extract mass
                    if (body.has("mass") && !body.get("mass").isJsonNull()) {
                        JsonObject massObj = body.getAsJsonObject("mass");
                        double massValue = massObj.get("massValue").getAsDouble();
                        double massExponent = massObj.get("massExponent").getAsDouble();
                        mass = massValue * Math.pow(10, massExponent);
                    }
                    
                    // Extract radius
                    if (body.has("meanRadius") && !body.get("meanRadius").isJsonNull()) {
                        radius = body.get("meanRadius").getAsDouble();
                    }
                    
                    // Extract orbital radius (semi-major axis)
                    if (body.has("semimajorAxis") && !body.get("semimajorAxis").isJsonNull()) {
                        orbitalRadius = body.get("semimajorAxis").getAsDouble();
                    }
                    
                    // Extract orbital period
                    if (body.has("sideralOrbit") && !body.get("sideralOrbit").isJsonNull()) {
                        orbitalPeriod = body.get("sideralOrbit").getAsDouble();
                    }
                    
                    // Create planet with appropriate data
                    Planet planet = new Planet(name, mass, radius, orbitalRadius, orbitalPeriod);
                    planets.add(planet);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return planets;
    }
}