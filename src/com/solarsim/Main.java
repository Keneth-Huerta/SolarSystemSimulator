package com.solarsim;

import com.solarsim.controller.SimulationController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * Clase principal que inicia la aplicación del simulador del sistema solar.
 * Extiende de Application para aprovechar las capacidades 3D de JavaFX.
 */
public class Main extends Application {
    /**
     * Método principal que inicia la aplicación.
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        // Iniciar la aplicación JavaFX
        launch(args);
    }
    
    /**
     * Método que se ejecuta cuando se inicia la aplicación JavaFX.
     * Crea e inicializa el controlador de simulación.
     * @param primaryStage Escenario principal de la aplicación JavaFX
     */
    @Override
    public void start(Stage primaryStage) {
        // Configurar el título de la ventana principal
        primaryStage.setTitle("Simulador del Sistema Solar 3D");
        
        // Crear e inicializar el controlador de simulación pasándole el stage
        SimulationController simulationController = new SimulationController(primaryStage);
    }
    
    /**
     * Método que se ejecuta al cerrar la aplicación.
     * Asegura la limpieza adecuada de los recursos.
     */
    @Override
    public void stop() {
        // Asegurar que se limpien todos los recursos al cerrar la aplicación
        Platform.exit();
    }
}