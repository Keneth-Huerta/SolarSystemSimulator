package com.solarsim.model;

/**
 * Clase que representa una estrella en el sistema solar.
 * Extiende CelestialBody añadiendo propiedades específicas de estrellas.
 */
public class Star extends CelestialBody {
    private double luminosity; // Luminosity of the star in watts
    private double temperature; // Surface temperature of the star in Kelvin

    /**
     * Constructor que inicializa una estrella con todos sus parámetros.
     * @param name Nombre de la estrella
     * @param mass Masa de la estrella (en kilogramos)
     * @param radius Radio de la estrella (en kilómetros)
     * @param luminosity Luminosidad de la estrella (en vatios)
     * @param temperature Temperatura superficial (en Kelvin)
     */
    public Star(String name, double mass, double radius, double luminosity, double temperature) {
        super(name, mass, radius);
        this.luminosity = luminosity;
        this.temperature = temperature;
    }

    /**
     * Obtiene la luminosidad de la estrella.
     * @return Luminosidad en vatios
     */
    public double getLuminosity() {
        return luminosity;
    }

    /**
     * Establece la luminosidad de la estrella.
     * @param luminosity Nueva luminosidad en vatios
     */
    public void setLuminosity(double luminosity) {
        this.luminosity = luminosity;
    }

    /**
     * Obtiene la temperatura superficial de la estrella.
     * @return Temperatura en Kelvin
     */
    public double getTemperature() {
        return temperature;
    }

    /**
     * Establece la temperatura superficial de la estrella.
     * @param temperature Nueva temperatura en Kelvin
     */
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    /**
     * Genera una representación en texto de la estrella.
     * @return Cadena con la información de la estrella
     */
    @Override
    public String toString() {
        return "Star{" +
                "name='" + getName() + '\'' +
                ", mass=" + getMass() +
                ", radius=" + getRadius() +
                ", luminosity=" + luminosity +
                ", temperature=" + temperature +
                '}';
    }
}