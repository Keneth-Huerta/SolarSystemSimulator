package com.solarsim.model;

import java.awt.Color;

/**
 * Clase que representa un planeta en el sistema solar.
 * Extiende CelestialBody añadiendo propiedades específicas de planetas.
 */
public class Planet extends CelestialBody {
    private double orbitalRadius; // in kilometers
    private double orbitalPeriod; // in Earth days
    private Color color;
    private double size; // visual size for rendering

    /**
     * Constructor básico que inicializa un planeta con sus propiedades físicas.
     * @param name Nombre del planeta
     * @param mass Masa del planeta (en kilogramos)
     * @param radius Radio del planeta (en kilómetros)
     * @param orbitalRadius Radio orbital (en kilómetros)
     * @param orbitalPeriod Periodo orbital (en días terrestres)
     */
    public Planet(String name, double mass, double radius, double orbitalRadius, double orbitalPeriod) {
        super(name, mass, radius);
        this.orbitalRadius = orbitalRadius;
        this.orbitalPeriod = orbitalPeriod;
        this.color = Color.WHITE; // Default color
        this.size = radius / 1000; // Default size based on radius
    }
    
    /**
     * Constructor completo que inicializa un planeta con propiedades físicas y visuales.
     * @param name Nombre del planeta
     * @param mass Masa del planeta (en kilogramos)
     * @param radius Radio del planeta (en kilómetros)
     * @param orbitalRadius Radio orbital (en kilómetros)
     * @param orbitalPeriod Periodo orbital (en días terrestres)
     * @param color Color para la representación visual
     * @param size Tamaño visual para la representación
     */
    public Planet(String name, double mass, double radius, double orbitalRadius, double orbitalPeriod, Color color, double size) {
        super(name, mass, radius);
        this.orbitalRadius = orbitalRadius;
        this.orbitalPeriod = orbitalPeriod;
        this.color = color;
        this.size = size;
    }

    /**
     * Obtiene el radio orbital del planeta.
     * @return Radio orbital en kilómetros
     */
    public double getOrbitalRadius() {
        return orbitalRadius;
    }

    /**
     * Obtiene el periodo orbital del planeta.
     * @return Periodo orbital en días terrestres
     */
    public double getOrbitalPeriod() {
        return orbitalPeriod;
    }
    
    /**
     * Obtiene el color asignado al planeta para su visualización.
     * @return Color del planeta
     */
    public Color getColor() {
        return color;
    }
    
    /**
     * Establece el color del planeta para su visualización.
     * @param color Nuevo color para el planeta
     */
    public void setColor(Color color) {
        this.color = color;
    }
    
    /**
     * Obtiene el tamaño visual asignado al planeta para su renderización.
     * @return Tamaño visual del planeta
     */
    public double getSize() {
        return size;
    }
    
    /**
     * Establece el tamaño visual del planeta para su renderización.
     * @param size Nuevo tamaño visual
     */
    public void setSize(double size) {
        this.size = size;
    }

    /**
     * Actualiza la posición del planeta basándose en su movimiento orbital.
     * Calcula la nueva posición en función del tiempo transcurrido y la velocidad angular.
     * @param time Tiempo transcurrido (en días)
     */
    @Override
    public void updatePosition(double time) {
        // Calculate new orbital position using angle
        double angularVelocity = 2 * Math.PI / orbitalPeriod;
        double newAngle = getCurrentAngle() + (angularVelocity * time);
        
        // Normalizar el ángulo para mantenerlo entre 0 y 2π
        newAngle = newAngle % (2 * Math.PI);
        if (newAngle < 0) {
            newAngle += 2 * Math.PI;
        }
        
        // Establecer el nuevo ángulo
        setCurrentAngle(newAngle);
        
        // Update 3D position basado en el radio orbital exacto
        double[] position = new double[3];
        position[0] = orbitalRadius * Math.cos(getCurrentAngle());
        position[1] = 0; // Mantener en el plano orbital (sin inclinación)
        position[2] = orbitalRadius * Math.sin(getCurrentAngle());
        setPosition(position);
        
        // Debug
        System.out.println("Actualizando: " + getName() + ", Ángulo: " + getCurrentAngle() + 
                           ", Pos X: " + position[0] + ", Pos Z: " + position[2]);
    }
}