package com.solarsim.model;

import java.awt.Color;

/**
 * Clase que representa una luna o satélite en el sistema solar.
 * Extiende CelestialBody añadiendo propiedades específicas de satélites.
 */
public class Moon extends CelestialBody {
    private double orbitalRadius; // en kilómetros
    private double orbitalPeriod; // en días
    private Color color;
    private double size; // tamaño visual para renderizado
    private Planet parentPlanet; // planeta alrededor del cual orbita
    
    /**
     * Constructor para crear una luna con todas sus propiedades.
     * 
     * @param name Nombre de la luna
     * @param mass Masa de la luna en kilogramos
     * @param radius Radio de la luna en kilómetros
     * @param orbitalRadius Radio orbital alrededor del planeta en kilómetros
     * @param orbitalPeriod Periodo orbital en días
     * @param color Color para visualización
     * @param size Tamaño visual para renderizado
     * @param parentPlanet Planeta alrededor del cual orbita
     */
    public Moon(String name, double mass, double radius, double orbitalRadius, 
                double orbitalPeriod, Color color, double size, Planet parentPlanet) {
        super(name, mass, radius);
        this.orbitalRadius = orbitalRadius;
        this.orbitalPeriod = orbitalPeriod;
        this.color = color;
        this.size = size;
        this.parentPlanet = parentPlanet;
    }
    
    /**
     * Obtiene el radio orbital de la luna alrededor de su planeta.
     * @return Radio orbital en kilómetros
     */
    public double getOrbitalRadius() {
        return orbitalRadius;
    }
    
    /**
     * Obtiene el periodo orbital de la luna.
     * @return Periodo orbital en días
     */
    public double getOrbitalPeriod() {
        return orbitalPeriod;
    }
    
    /**
     * Obtiene el color asignado a la luna para su visualización.
     * @return Color de la luna
     */
    public Color getColor() {
        return color;
    }
    
    /**
     * Establece el color de la luna para su visualización.
     * @param color Nuevo color para la luna
     */
    public void setColor(Color color) {
        this.color = color;
    }
    
    /**
     * Obtiene el tamaño visual asignado a la luna para su renderización.
     * @return Tamaño visual de la luna
     */
    public double getSize() {
        return size;
    }
    
    /**
     * Establece el tamaño visual de la luna para su renderización.
     * @param size Nuevo tamaño visual
     */
    public void setSize(double size) {
        this.size = size;
    }
    
    /**
     * Obtiene el planeta alrededor del cual orbita esta luna.
     * @return El planeta padre
     */
    public Planet getParentPlanet() {
        return parentPlanet;
    }
    
    /**
     * Actualiza la posición de la luna basándose en su movimiento orbital.
     * La posición final es relativa al planeta alrededor del cual orbita.
     * 
     * @param time Tiempo transcurrido (en días)
     */
    @Override
    public void updatePosition(double time) {
        // Calcular nueva posición orbital usando ángulo
        double angularVelocity = 2 * Math.PI / orbitalPeriod;
        double newAngle = getCurrentAngle() + (angularVelocity * time);
        
        // Normalizar el ángulo para mantenerlo entre 0 y 2π
        newAngle = newAngle % (2 * Math.PI);
        if (newAngle < 0) {
            newAngle += 2 * Math.PI;
        }
        
        // Establecer el nuevo ángulo
        setCurrentAngle(newAngle);
        
        // Obtener la posición actual del planeta padre
        double[] planetPosition = parentPlanet.getPosition();
        
        // Calcular la posición orbital relativa al planeta
        double[] position = new double[3];
        position[0] = planetPosition[0] + orbitalRadius * Math.cos(getCurrentAngle());
        position[1] = 0; // Mantener en el plano orbital (sin inclinación por ahora)
        position[2] = planetPosition[2] + orbitalRadius * Math.sin(getCurrentAngle());
        
        // Actualizar la posición
        setPosition(position);
    }
}