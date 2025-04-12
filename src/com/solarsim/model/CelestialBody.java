package com.solarsim.model;

/**
 * Clase base para todos los cuerpos celestes del sistema solar.
 * Define propiedades y comportamientos comunes de todos los cuerpos astronómicos.
 */
public class CelestialBody {
    private String name;
    private double mass; // in kilograms
    private double radius; // in kilometers
    private double[] position; // in 3D space (x, y, z)
    private double[] velocity; // in 3D space (vx, vy, vz)
    private double currentAngle; // Current orbital angle in radians

    /**
     * Constructor básico que inicializa un cuerpo celeste.
     * @param name Nombre del cuerpo celeste
     * @param mass Masa del cuerpo (en kilogramos)
     * @param radius Radio del cuerpo (en kilómetros)
     */
    public CelestialBody(String name, double mass, double radius) {
        this.name = name;
        this.mass = mass;
        this.radius = radius;
        this.position = new double[]{0, 0, 0};
        this.velocity = new double[]{0, 0, 0};
        this.currentAngle = 0;
    }

    /**
     * Constructor completo que inicializa un cuerpo celeste con posición y velocidad.
     * @param name Nombre del cuerpo celeste
     * @param mass Masa del cuerpo (en kilogramos)
     * @param radius Radio del cuerpo (en kilómetros)
     * @param position Vector de posición inicial [x, y, z]
     * @param velocity Vector de velocidad inicial [vx, vy, vz]
     */
    public CelestialBody(String name, double mass, double radius, double[] position, double[] velocity) {
        this.name = name;
        this.mass = mass;
        this.radius = radius;
        this.position = position;
        this.velocity = velocity;
        this.currentAngle = 0;
    }

    /**
     * Obtiene el nombre del cuerpo celeste.
     * @return Nombre del cuerpo celeste
     */
    public String getName() {
        return name;
    }

    /**
     * Obtiene la masa del cuerpo celeste.
     * @return Masa en kilogramos
     */
    public double getMass() {
        return mass;
    }
    
    /**
     * Obtiene el radio del cuerpo celeste.
     * @return Radio en kilómetros
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Obtiene la posición actual del cuerpo celeste.
     * @return Vector de posición [x, y, z]
     */
    public double[] getPosition() {
        return position;
    }

    /**
     * Obtiene el vector de velocidad actual del cuerpo celeste.
     * @return Vector de velocidad [vx, vy, vz]
     */
    public double[] getVelocity() {
        return velocity;
    }
    
    /**
     * Obtiene el ángulo orbital actual del cuerpo celeste.
     * @return Ángulo en radianes
     */
    public double getCurrentAngle() {
        return currentAngle;
    }
    
    /**
     * Establece el ángulo orbital del cuerpo celeste.
     * @param angle Nuevo ángulo en radianes
     */
    public void setCurrentAngle(double angle) {
        this.currentAngle = angle;
    }

    /**
     * Establece la posición del cuerpo celeste.
     * @param position Nuevo vector de posición [x, y, z]
     */
    public void setPosition(double[] position) {
        this.position = position;
    }

    /**
     * Establece la velocidad del cuerpo celeste.
     * @param velocity Nuevo vector de velocidad [vx, vy, vz]
     */
    public void setVelocity(double[] velocity) {
        this.velocity = velocity;
    }

    /**
     * Actualiza la posición del cuerpo celeste basándose en su velocidad.
     * Método básico que puede ser sobrescrito por subclases con físicas más complejas.
     * @param time Tiempo transcurrido (en días)
     */
    public void updatePosition(double time) {
        // Update position based on velocity and time
        for (int i = 0; i < position.length; i++) {
            position[i] += velocity[i] * time;
        }
    }
}