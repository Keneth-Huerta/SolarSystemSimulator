package com.solarsim.controller;

import com.solarsim.model.Moon;
import com.solarsim.model.Planet;
import com.solarsim.model.SolarSystem;
import com.solarsim.model.Star;
import com.solarsim.view.JavaFX3DSimulationView;

import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.AmbientLight;
import javafx.scene.PointLight;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.shape.Box;
import javafx.scene.shape.Circle;
import javafx.scene.shape.DrawMode;
import javafx.stage.Stage;

/**
 * Controlador principal de la simulación del sistema solar.
 * Maneja la lógica de la simulación, la vista y la interacción con el usuario.
 * Implementa el patrón MVC (Modelo-Vista-Controlador) con JavaFX 3D.
 */
public class SimulationController {
    private SolarSystem solarSystem;
    private JavaFX3DSimulationView simulationView;
    private Timer simulationTimer;
    private boolean isSimulationRunning;
    
    // Objetos 3D
    private Sphere sun3D;
    private Group orbitsGroup;
    private Sphere[] planets3D;
    private Sphere[] moons3D; // Array para las lunas
    
    /** Paso de tiempo en días de simulación */
    private static final double TIME_STEP = 1.0;
    
    /** 
     * Factor de escala para hacer visibles las órbitas en pantalla.
     * Usamos un factor progresivo para que los planetas interiores no estén tan cerca
     */
    private static final double SCALE_FACTOR = 1.0 / 5000000.0; // Aumentado para mayor visibilidad
    
    /** Factores adicionales de escala para cada planeta */
    private static final double MERCURY_SCALE_FACTOR = 8.0; // Factor muy aumentado para Mercurio
    private static final double VENUS_SCALE_FACTOR = 7.0;   // Factor aumentado para Venus
    private static final double EARTH_SCALE_FACTOR = 6.0;   // Factor aumentado para Tierra
    private static final double MARS_SCALE_FACTOR = 5.0;    // Factor aumentado para Marte
    private static final double OUTER_PLANETS_FACTOR = 2.0; // Aumentado para mayor visibilidad
    
    /** Factor de zoom actual */
    private double zoomFactor = 0.7; // Reducido para ver todo el sistema inicialmente
    
    /** Límites de zoom para evitar valores extremos */
    private static final double MAX_ZOOM = 5.0;
    private static final double MIN_ZOOM = 0.2;
    
    /** Tamaño visual del sol - reducido para evitar que se sobreponga con Mercurio */
    private static final int BASE_SUN_SIZE = 20; // Reducido para una mejor escala visual

    /**
     * Constructor que inicializa el controlador con una vista.
     * @param stage El Stage principal de JavaFX (ventana principal)
     */
    public SimulationController(Stage stage) {
        // Crear la vista 3D con JavaFX puro
        simulationView = new JavaFX3DSimulationView(stage);
        simulationView.setController(this);
        
        // Inicializar el modelo de simulación
        initializeSimulation();
        
        // Configurar la escena 3D con los planetas
        Platform.runLater(this::setup3DScene);
    }
    
    /**
     * Constructor sin parámetros para compatibilidad con código existente.
     */
    public SimulationController() {
        // Inicializar solo el modelo para casos donde no se necesite UI
        initializeSimulation();
        simulationTimer = new Timer(true);
        isSimulationRunning = false;
    }
    
    /**
     * Configura la escena 3D con todos los elementos.
     */
    private void setup3DScene() {
        if (simulationView == null) return;
        
        Group planetGroup = simulationView.getPlanetGroup();
        
        // Limpiar grupo por si acaso
        planetGroup.getChildren().clear();
        
        // Añadir iluminación ambiental
        AmbientLight ambientLight = new AmbientLight(javafx.scene.paint.Color.rgb(30, 30, 30));
        planetGroup.getChildren().add(ambientLight);
        
        // Crear el Sol en el centro
        sun3D = createSun3D(BASE_SUN_SIZE);
        planetGroup.getChildren().add(sun3D);
        
        // Añadir luz puntual en el sol
        PointLight sunLight = new PointLight(javafx.scene.paint.Color.WHITE);
        sunLight.getScope().add(planetGroup);
        planetGroup.getChildren().add(sunLight);
        
        // Crear grupo para órbitas
        orbitsGroup = new Group();
        planetGroup.getChildren().add(orbitsGroup);
        
        // Contar planetas para crear el array
        int planetCount = 0;
        int moonCount = 0;
        for (int i = 0; i < solarSystem.getCelestialBodies().size(); i++) {
            if (solarSystem.getCelestialBodies().get(i) instanceof Planet) {
                planetCount++;
            } else if (solarSystem.getCelestialBodies().get(i) instanceof Moon) {
                moonCount++;
            }
        }
        planets3D = new Sphere[planetCount];
        moons3D = new Sphere[moonCount];
        
        // Añadir todos los planetas y sus órbitas
        int planetIndex = 0;
        for (int i = 0; i < solarSystem.getCelestialBodies().size(); i++) {
            if (solarSystem.getCelestialBodies().get(i) instanceof Planet) {
                Planet planeta = (Planet) solarSystem.getCelestialBodies().get(i);
                
                // Añadir órbita
                Group orbit = createOrbit3D(planeta);
                orbitsGroup.getChildren().add(orbit);
                
                // Añadir planeta como esfera 3D
                Sphere planeta3D = createPlanet3D(planeta);
                planets3D[planetIndex++] = planeta3D;
                planetGroup.getChildren().add(planeta3D);
                
                // Añadir etiqueta con el nombre
                javafx.scene.text.Text nombrePlaneta = new javafx.scene.text.Text(planeta.getName());
                nombrePlaneta.setFill(javafx.scene.paint.Color.WHITE);
                nombrePlaneta.setTranslateX(planeta3D.getTranslateX() + planeta.getSize() * 2);
                nombrePlaneta.setTranslateZ(planeta3D.getTranslateZ());
                planetGroup.getChildren().add(nombrePlaneta);
            }
        }
        
        // Añadir todas las lunas
        int moonIndex = 0;
        for (int i = 0; i < solarSystem.getCelestialBodies().size(); i++) {
            if (solarSystem.getCelestialBodies().get(i) instanceof Moon) {
                Moon luna = (Moon) solarSystem.getCelestialBodies().get(i);
                
                // Añadir órbita de la luna
                Group moonOrbit = createMoonOrbit3D(luna);
                orbitsGroup.getChildren().add(moonOrbit);
                
                // Añadir luna como esfera 3D
                Sphere luna3D = createMoon3D(luna);
                moons3D[moonIndex++] = luna3D;
                planetGroup.getChildren().add(luna3D);
                
                // Añadir etiqueta con el nombre
                javafx.scene.text.Text nombreLuna = new javafx.scene.text.Text(luna.getName());
                nombreLuna.setFill(javafx.scene.paint.Color.LIGHTGRAY);
                nombreLuna.setTranslateX(luna3D.getTranslateX() + luna.getSize() * 2);
                nombreLuna.setTranslateZ(luna3D.getTranslateZ());
                nombreLuna.setFont(javafx.scene.text.Font.font("Arial", 8)); // Fuente más pequeña para las lunas
                planetGroup.getChildren().add(nombreLuna);
            }
        }
    }
    
    /**
     * Crea una representación 3D de un planeta.
     * @param planeta El planeta a representar
     * @return Una esfera 3D que representa al planeta
     */
    private Sphere createPlanet3D(Planet planeta) {
        double tamaño = planeta.getSize() * zoomFactor;
        if (tamaño < 2) tamaño = 2; // Tamaño mínimo para visibilidad
        
        Sphere esfera = new Sphere(tamaño);
        
        // Convertir java.awt.Color a javafx.scene.paint.Color
        java.awt.Color awtColor = planeta.getColor();
        javafx.scene.paint.Color fxColor = javafx.scene.paint.Color.rgb(
            awtColor.getRed(),
            awtColor.getGreen(),
            awtColor.getBlue(),
            awtColor.getAlpha() / 255.0
        );
        
        // Crear material con iluminación para efecto 3D
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(fxColor);
        material.setSpecularColor(javafx.scene.paint.Color.WHITE);
        esfera.setMaterial(material);
        
        // Posicionar el planeta en el espacio 3D usando la posición del modelo
        double[] position = planeta.getPosition();
        esfera.setTranslateX(position[0] * zoomFactor);
        esfera.setTranslateY(0); // Mantener en el plano horizontal por ahora
        esfera.setTranslateZ(position[2] * zoomFactor);
        
        return esfera;
    }
    
    /**
     * Crea una representación 3D de la órbita del planeta.
     * @param planeta Planeta cuya órbita se va a renderizar
     * @return Grupo con la órbita renderizada como un círculo en el espacio 3D
     */
    private Group createOrbit3D(Planet planeta) {
        Group orbitGroup = new Group();
        
        javafx.scene.shape.Circle orbita = new Circle(
            0, 0, 
            planeta.getOrbitalRadius() * zoomFactor
        );
        
        orbita.setFill(javafx.scene.paint.Color.TRANSPARENT);
        orbita.setStroke(javafx.scene.paint.Color.rgb(70, 70, 70));
        orbita.setStrokeWidth(1);
        
        // Rotar el círculo 90 grados para que quede en el plano XZ
        orbita.getTransforms().add(new Rotate(90, Rotate.X_AXIS));
        
        orbitGroup.getChildren().add(orbita);
        return orbitGroup;
    }
    
    /**
     * Crea una representación 3D de una luna o satélite.
     * @param luna La luna a representar
     * @return Una esfera 3D que representa a la luna
     */
    private Sphere createMoon3D(Moon luna) {
        // Aumentamos el tamaño para mayor visibilidad
        double tamaño = luna.getSize() * zoomFactor * 2.0;  // Multiplicador adicional para mejor visibilidad
        if (tamaño < 2.5) tamaño = 2.5; // Tamaño mínimo aumentado para visibilidad
        
        Sphere esfera = new Sphere(tamaño);
        
        // Convertir java.awt.Color a javafx.scene.paint.Color
        java.awt.Color awtColor = luna.getColor();
        javafx.scene.paint.Color fxColor = javafx.scene.paint.Color.rgb(
            awtColor.getRed(),
            awtColor.getGreen(),
            awtColor.getBlue(),
            awtColor.getAlpha() / 255.0
        );
        
        // Crear material con iluminación para efecto 3D
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(fxColor);
        material.setSpecularColor(javafx.scene.paint.Color.WHITE);
        material.setSpecularPower(10.0);  // Más brillo para que resalte
        esfera.setMaterial(material);
        
        // Posicionar la luna en el espacio 3D usando la posición del modelo
        double[] position = luna.getPosition();
        esfera.setTranslateX(position[0] * zoomFactor);
        esfera.setTranslateY(0); // Mantener en el plano horizontal por ahora
        esfera.setTranslateZ(position[2] * zoomFactor);
        
        return esfera;
    }
    
    /**
     * Crea una representación 3D de la órbita de una luna.
     * @param luna Luna cuya órbita se va a renderizar
     * @return Grupo con la órbita renderizada como un círculo en el espacio 3D
     */
    private Group createMoonOrbit3D(Moon luna) {
        Group orbitGroup = new Group();
        
        // Obtener la posición del planeta padre
        Planet parentPlanet = luna.getParentPlanet();
        double[] planetPosition = parentPlanet.getPosition();
        
        // Crear un círculo centrado en la posición del planeta
        Circle orbita = new Circle(
            0, 0, 
            luna.getOrbitalRadius() * zoomFactor
        );
        
        orbita.setFill(javafx.scene.paint.Color.TRANSPARENT);
        // Color más visible para las órbitas de las lunas
        orbita.setStroke(javafx.scene.paint.Color.rgb(120, 120, 160, 0.7));
        orbita.setStrokeWidth(0.8); // Grosor ligeramente aumentado
        
        // Rotar el círculo 90 grados para que quede en el plano XZ
        orbita.getTransforms().add(new Rotate(90, Rotate.X_AXIS));
        
        // Trasladar el círculo a la posición del planeta padre
        orbitGroup.setTranslateX(planetPosition[0] * zoomFactor);
        orbitGroup.setTranslateZ(planetPosition[2] * zoomFactor);
        
        orbitGroup.getChildren().add(orbita);
        return orbitGroup;
    }
    
    /**
     * Crea una esfera 3D que representa el Sol con mayor brillo y efecto de iluminación.
     * @param tamaño Tamaño del sol
     * @return Esfera con material brillante para representar el Sol
     */
    private Sphere createSun3D(double tamaño) {
        Sphere sol = new Sphere(tamaño * zoomFactor);

        // Material más brillante para el sol
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(javafx.scene.paint.Color.rgb(255, 255, 0)); // Amarillo brillante
        material.setSpecularColor(javafx.scene.paint.Color.rgb(255, 255, 200)); // Brillo
        material.setSpecularPower(5.0); // Más brillante

        sol.setMaterial(material);
        sol.setDrawMode(DrawMode.FILL);

        return sol;
    }
    
    /**
     * Obtiene el factor de zoom actual.
     * @return El factor de zoom actual
     */
    public double getZoomFactor() {
        return zoomFactor;
    }
    
    /**
     * Aumenta el nivel de zoom de la vista.
     * @param factor Cantidad por la que aumentar el zoom
     */
    public void zoomIn(double factor) {
        double nuevoZoom = zoomFactor + factor;
        if (nuevoZoom <= MAX_ZOOM) {
            zoomFactor = nuevoZoom;
            updateZoom();
        }
    }
    
    /**
     * Disminuye el nivel de zoom de la vista.
     * @param factor Cantidad por la que disminuir el zoom
     */
    public void zoomOut(double factor) {
        double nuevoZoom = zoomFactor - factor;
        if (nuevoZoom >= MIN_ZOOM) {
            zoomFactor = nuevoZoom;
            updateZoom();
        }
    }
    
    /**
     * Actualiza el zoom en la escena 3D.
     */
    private void updateZoom() {
        if (simulationView == null) return;
        
        Platform.runLater(() -> {
            // Actualizar la posición de la cámara para el zoom
            double distanciaZ = -1500 / zoomFactor;
            simulationView.getCamera().setTranslateZ(distanciaZ);
            
            // Actualizar órbitas para mantener la consistencia visual
            updateOrbitsForZoom();
            
            // Actualizar el sol
            if (sun3D != null) {
                sun3D.setRadius(BASE_SUN_SIZE * zoomFactor);
            }
            
            // Actualizar cada planeta (solo tamaño visual, no posición)
            updatePlanetSizesForZoom();
            
            // También actualizar las posiciones de los planetas según su posición actual en el modelo
            // Esto corrige el problema cuando se hace zoom mientras la simulación está pausada
            updatePlanetPositionsForZoom();
        });
    }
    
    /**
     * Actualiza las órbitas de los planetas cuando cambia el zoom
     */
    private void updateOrbitsForZoom() {
        if (orbitsGroup == null) return;
        
        orbitsGroup.getChildren().clear();
        
        // Recrear todas las órbitas con el zoom actualizado
        for (int i = 0; i < solarSystem.getCelestialBodies().size(); i++) {
            if (solarSystem.getCelestialBodies().get(i) instanceof Planet) {
                Planet planeta = (Planet) solarSystem.getCelestialBodies().get(i);
                Group orbit = createOrbit3D(planeta);
                orbitsGroup.getChildren().add(orbit);
            } else if (solarSystem.getCelestialBodies().get(i) instanceof Moon) {
                Moon luna = (Moon) solarSystem.getCelestialBodies().get(i);
                Group moonOrbit = createMoonOrbit3D(luna);
                orbitsGroup.getChildren().add(moonOrbit);
            }
        }
    }
    
    /**
     * Actualiza solo los tamaños de los planetas, no sus posiciones
     */
    private void updatePlanetSizesForZoom() {
        if (planets3D == null) return;
        
        int planetIndex = 0;
        for (int i = 0; i < solarSystem.getCelestialBodies().size(); i++) {
            if (solarSystem.getCelestialBodies().get(i) instanceof Planet) {
                Planet planeta = (Planet) solarSystem.getCelestialBodies().get(i);
                
                if (planetIndex < planets3D.length) {
                    Sphere planeta3D = planets3D[planetIndex];
                    // Solo actualizar el radio visual
                    double tamaño = planeta.getSize() * zoomFactor;
                    if (tamaño < 2) tamaño = 2; // Tamaño mínimo para visibilidad
                    planeta3D.setRadius(tamaño);
                }
                planetIndex++;
            }
        }
        
        if (moons3D == null) return;
        
        int moonIndex = 0;
        for (int i = 0; i < solarSystem.getCelestialBodies().size(); i++) {
            if (solarSystem.getCelestialBodies().get(i) instanceof Moon) {
                Moon luna = (Moon) solarSystem.getCelestialBodies().get(i);
                
                if (moonIndex < moons3D.length) {
                    Sphere luna3D = moons3D[moonIndex];
                    // Solo actualizar el radio visual
                    double tamaño = luna.getSize() * zoomFactor;
                    if (tamaño < 1.5) tamaño = 1.5; // Tamaño mínimo para visibilidad
                    luna3D.setRadius(tamaño);
                }
                moonIndex++;
            }
        }
    }

    /**
     * Actualiza las posiciones de los planetas cuando cambia el zoom sin avanzar la simulación
     * Esto es especialmente importante cuando la simulación está pausada
     */
    private void updatePlanetPositionsForZoom() {
        if (planets3D == null) return;
        
        int planetIndex = 0;
        for (int i = 0; i < solarSystem.getCelestialBodies().size(); i++) {
            if (solarSystem.getCelestialBodies().get(i) instanceof Planet) {
                Planet planeta = (Planet) solarSystem.getCelestialBodies().get(i);
                
                if (planetIndex < planets3D.length) {
                    Sphere planeta3D = planets3D[planetIndex];
                    
                    // Obtener la posición actual del modelo
                    double[] position = planeta.getPosition();
                    
                    // Aplicar el zoom a la posición actual
                    planeta3D.setTranslateX(position[0] * zoomFactor);
                    planeta3D.setTranslateZ(position[2] * zoomFactor);
                    
                    // Actualizar también la etiqueta
                    Group planetGroup = simulationView.getPlanetGroup();
                    int textIndex = planetGroup.getChildren().indexOf(planeta3D) + 1;
                    if (textIndex < planetGroup.getChildren().size() && 
                        planetGroup.getChildren().get(textIndex) instanceof javafx.scene.text.Text) {
                        javafx.scene.text.Text text = 
                            (javafx.scene.text.Text) planetGroup.getChildren().get(textIndex);
                        text.setTranslateX(position[0] * zoomFactor + planeta.getSize() * 2);
                        text.setTranslateZ(position[2] * zoomFactor);
                    }
                }
                planetIndex++;
            }
        }
        
        if (moons3D == null) return;
        
        int moonIndex = 0;
        for (int i = 0; i < solarSystem.getCelestialBodies().size(); i++) {
            if (solarSystem.getCelestialBodies().get(i) instanceof Moon) {
                Moon luna = (Moon) solarSystem.getCelestialBodies().get(i);
                
                if (moonIndex < moons3D.length) {
                    Sphere luna3D = moons3D[moonIndex];
                    
                    // Obtener la posición actual del modelo
                    double[] position = luna.getPosition();
                    
                    // Aplicar el zoom a la posición actual
                    luna3D.setTranslateX(position[0] * zoomFactor);
                    luna3D.setTranslateZ(position[2] * zoomFactor);
                    
                    // También actualizamos la etiqueta del nombre si existe
                    Group planetGroup = simulationView.getPlanetGroup();
                    int textIndex = planetGroup.getChildren().indexOf(luna3D) + 1;
                    if (textIndex < planetGroup.getChildren().size() && 
                        planetGroup.getChildren().get(textIndex) instanceof javafx.scene.text.Text) {
                        javafx.scene.text.Text text = 
                            (javafx.scene.text.Text) planetGroup.getChildren().get(textIndex);
                        text.setTranslateX(position[0] * zoomFactor + luna.getSize() * 2);
                        text.setTranslateZ(position[2] * zoomFactor);
                    }
                }
                moonIndex++;
            }
        }
    }

    /**
     * Inicializa el modelo de simulación con los cuerpos celestes.
     * Crea el Sol y todos los planetas con sus propiedades.
     */
    private void initializeSimulation() {
        // Crear el modelo
        solarSystem = new SolarSystem();
        
        // Añadir el sol con datos escalados
        Star sol = new Star("Sol", 1.989e30, 695700, 3.828e26, 5778);
        solarSystem.addCelestialBody(sol);
        
        // Ajustes para una mejor visualización de las órbitas
        // Para los planetas cercanos, aumentamos mucho más sus órbitas para separarlos visualmente del Sol
        
        // Mercurio - órbita muy alejada del sol para visualización
        Planet mercurio = new Planet(
            "Mercurio", 
            3.3011e23, 
            2439.7, 
            57.9e6 * SCALE_FACTOR * MERCURY_SCALE_FACTOR, 
            88.0, 
            new Color(169, 169, 169), 
            2);
        
        // Venus - órbita más alejada
        Planet venus = new Planet(
            "Venus", 
            4.8675e24, 
            6051.8, 
            108.2e6 * SCALE_FACTOR * VENUS_SCALE_FACTOR, 
            224.7, 
            new Color(255, 198, 73), 
            3.5);
        
        // Tierra - órbita más alejada
        Planet tierra = new Planet(
            "Tierra", 
            5.97237e24, 
            6371.0, 
            149.6e6 * SCALE_FACTOR * EARTH_SCALE_FACTOR, 
            365.25, 
            new Color(0, 0, 255), 
            4);
        
        // Marte - órbita más alejada
        Planet marte = new Planet(
            "Marte", 
            6.4171e23, 
            3389.5, 
            227.9e6 * SCALE_FACTOR * MARS_SCALE_FACTOR, 
            687.0, 
            new Color(255, 0, 0), 
            3);
        
        // Planetas exteriores
        Planet jupiter = new Planet(
            "Júpiter", 
            1.8982e27, 
            69911, 
            778.5e6 * SCALE_FACTOR * OUTER_PLANETS_FACTOR, 
            4332.59, 
            new Color(255, 165, 0), 
            8.5);
        
        Planet saturno = new Planet(
            "Saturno", 
            5.6834e26, 
            58232, 
            1434.0e6 * SCALE_FACTOR * OUTER_PLANETS_FACTOR, 
            10759.22, 
            new Color(210, 180, 140), 
            7);
        
        Planet urano = new Planet(
            "Urano", 
            8.6810e25, 
            25362, 
            2871.0e6 * SCALE_FACTOR * OUTER_PLANETS_FACTOR, 
            30688.5, 
            new Color(173, 216, 230), 
            5.5);
        
        Planet neptuno = new Planet(
            "Neptuno", 
            1.02413e26, 
            24622, 
            4495.0e6 * SCALE_FACTOR * OUTER_PLANETS_FACTOR, 
            60182, 
            new Color(0, 0, 139), 
            5.5);
        
        solarSystem.addCelestialBody(mercurio);
        solarSystem.addCelestialBody(venus);
        solarSystem.addCelestialBody(tierra);
        solarSystem.addCelestialBody(marte);
        solarSystem.addCelestialBody(jupiter);
        solarSystem.addCelestialBody(saturno);
        solarSystem.addCelestialBody(urano);
        solarSystem.addCelestialBody(neptuno);
        
        // Configurar temporizador de simulación
        simulationTimer = new Timer(true);
        isSimulationRunning = false;
    }

    /**
     * Inicia la simulación del movimiento planetario.
     * Configura un temporizador que actualiza y renderiza periódicamente.
     */
    public void startSimulation() {
        if (!isSimulationRunning) {
            isSimulationRunning = true;
            simulationTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    updateSimulation();
                    updatePlanetPositions();
                }
            }, 0, 50); // Actualizar aproximadamente 20 veces por segundo
        }
    }

    /**
     * Pausa la simulación del movimiento planetario.
     * Detiene el temporizador de actualización.
     */
    public void pauseSimulation() {
        if (isSimulationRunning) {
            isSimulationRunning = false;
            simulationTimer.cancel();
            simulationTimer = new Timer(true);
        }
    }

    /**
     * Reinicia la simulación a su estado inicial.
     * Reposiciona todos los planetas a su posición original.
     */
    public void resetSimulation() {
        // Detener la simulación actual
        pauseSimulation();
        
        // Resetear todos los planetas a sus posiciones iniciales
        for (int i = 0; i < solarSystem.getCelestialBodies().size(); i++) {
            if (solarSystem.getCelestialBodies().get(i) instanceof Planet) {
                Planet planeta = (Planet) solarSystem.getCelestialBodies().get(i);
                planeta.setCurrentAngle(0);  // Restablecer ángulo a la posición inicial
                
                // Actualizar posición basada en el ángulo reseteado
                double[] posicion = new double[3];
                posicion[0] = planeta.getOrbitalRadius();
                posicion[1] = 0;
                posicion[2] = 0;
                planeta.setPosition(posicion);
            }
        }
        
        // Resetear también todas las lunas
        for (int i = 0; i < solarSystem.getCelestialBodies().size(); i++) {
            if (solarSystem.getCelestialBodies().get(i) instanceof Moon) {
                Moon luna = (Moon) solarSystem.getCelestialBodies().get(i);
                luna.setCurrentAngle(0);  // Restablecer ángulo a la posición inicial
                
                // Actualizar posición basada en el planeta padre
                Planet padre = luna.getParentPlanet();
                double[] posicionPadre = padre.getPosition();
                
                double[] posicion = new double[3];
                posicion[0] = posicionPadre[0] + luna.getOrbitalRadius();
                posicion[1] = 0;
                posicion[2] = posicionPadre[2];
                luna.setPosition(posicion);
            }
        }
        
        // Reconstruir la escena 3D con las posiciones reseteadas
        Platform.runLater(this::setup3DScene);
    }

    /**
     * Actualiza la posición de todos los cuerpos celestes basándose en el paso de tiempo.
     */
    private void updateSimulation() {
        solarSystem.simulateMovement(TIME_STEP);
        
        // Después de actualizar las posiciones, actualizar las órbitas de las lunas
        // porque sus posiciones relativas a los planetas han cambiado
        if (orbitsGroup != null) {
            Platform.runLater(() -> {
                // Solo actualizar las órbitas de las lunas, no las de los planetas
                for (int i = 0; i < orbitsGroup.getChildren().size(); i++) {
                    Group orbitGroup = (Group)orbitsGroup.getChildren().get(i);
                    if (orbitGroup.getTranslateX() != 0 || orbitGroup.getTranslateZ() != 0) {
                        // Esta es probablemente una órbita de luna (está desplazada del origen)
                        // Necesitamos encontrar a qué luna corresponde
                        for (int j = 0; j < solarSystem.getCelestialBodies().size(); j++) {
                            if (solarSystem.getCelestialBodies().get(j) instanceof Moon) {
                                Moon luna = (Moon)solarSystem.getCelestialBodies().get(j);
                                Planet padre = luna.getParentPlanet();
                                
                                // Actualizar la posición de la órbita para que siga al planeta
                                double[] planetPosition = padre.getPosition();
                                orbitGroup.setTranslateX(planetPosition[0] * zoomFactor);
                                orbitGroup.setTranslateZ(planetPosition[2] * zoomFactor);
                            }
                        }
                    }
                }
            });
        }
    }
    
    /**
     * Corrige la posición de los planetas para alinearlos perfectamente con sus órbitas y asegura que el zoom no los desplace.
     */
    private void updatePlanetPositions() {
        if (simulationView == null || planets3D == null) return;

        Platform.runLater(() -> {
            // Actualizar cada planeta
            int planetIndex = 0;
            for (int i = 0; i < solarSystem.getCelestialBodies().size(); i++) {
                if (solarSystem.getCelestialBodies().get(i) instanceof Planet) {
                    Planet planeta = (Planet) solarSystem.getCelestialBodies().get(i);

                    if (planetIndex < planets3D.length) {
                        Sphere planeta3D = planets3D[planetIndex];

                        // Obtener la posición exacta del modelo
                        double[] position = planeta.getPosition();
                        
                        // Aplicar la posición exactamente como está en el modelo, con el factor de zoom
                        planeta3D.setTranslateX(position[0] * zoomFactor);
                        planeta3D.setTranslateZ(position[2] * zoomFactor);

                        // También actualizamos la etiqueta del nombre si existe
                        Group planetGroup = simulationView.getPlanetGroup();
                        int textIndex = planetGroup.getChildren().indexOf(planeta3D) + 1;
                        if (textIndex < planetGroup.getChildren().size() && 
                            planetGroup.getChildren().get(textIndex) instanceof javafx.scene.text.Text) {
                            javafx.scene.text.Text text = 
                                (javafx.scene.text.Text) planetGroup.getChildren().get(textIndex);
                            text.setTranslateX(position[0] * zoomFactor + planeta.getSize() * 2);
                            text.setTranslateZ(position[2] * zoomFactor);
                        }
                    }
                    planetIndex++;
                }
            }
            
            if (moons3D == null) return;
            
            int moonIndex = 0;
            for (int i = 0; i < solarSystem.getCelestialBodies().size(); i++) {
                if (solarSystem.getCelestialBodies().get(i) instanceof Moon) {
                    Moon luna = (Moon) solarSystem.getCelestialBodies().get(i);
                    
                    if (moonIndex < moons3D.length) {
                        Sphere luna3D = moons3D[moonIndex];
                        
                        // Obtener la posición exacta del modelo
                        double[] position = luna.getPosition();
                        
                        // Aplicar la posición exactamente como está en el modelo, con el factor de zoom
                        luna3D.setTranslateX(position[0] * zoomFactor);
                        luna3D.setTranslateZ(position[2] * zoomFactor);
                        
                        // También actualizamos la etiqueta del nombre si existe
                        Group planetGroup = simulationView.getPlanetGroup();
                        int textIndex = planetGroup.getChildren().indexOf(luna3D) + 1;
                        if (textIndex < planetGroup.getChildren().size() && 
                            planetGroup.getChildren().get(textIndex) instanceof javafx.scene.text.Text) {
                            javafx.scene.text.Text text = 
                                (javafx.scene.text.Text) planetGroup.getChildren().get(textIndex);
                            text.setTranslateX(position[0] * zoomFactor + luna.getSize() * 2);
                            text.setTranslateZ(position[2] * zoomFactor);
                        }
                    }
                    moonIndex++;
                }
            }
        });
    }
}