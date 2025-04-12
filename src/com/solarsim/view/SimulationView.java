package com.solarsim.view;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Clase que representa la interfaz gráfica del simulador del sistema solar.
 * Contiene la escena 3D y los botones de control.
 */
public class SimulationView extends JFrame {
    private JFXPanel jfxPanel;
    private JPanel panelControl;
    private JButton botonIniciar;
    private JButton botonPausar;
    private JButton botonReiniciar;
    
    // Componentes JavaFX
    private Scene scene;
    private Group root3D;
    private SubScene subScene3D;
    private PerspectiveCamera camera;
    private Group planetGroup;
    
    // Para rotación
    private double anchorX, anchorY;
    private double anchorAngleX = 0;
    private double anchorAngleY = 0;
    private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    
    /** Referencia al controlador */
    private com.solarsim.controller.SimulationController controller;
    
    /** Variables para el seguimiento del arrastre del ratón */
    private boolean estaDragging = false;

    /**
     * Constructor que inicializa la ventana y todos sus componentes.
     */
    public SimulationView() {
        setTitle("Simulador del Sistema Solar - Vista 3D");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        
        // Agregar controlador de cierre para limpiar recursos
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Platform.exit();
            }
        });
        
        // Inicializar panel JavaFX
        jfxPanel = new JFXPanel();
        
        // Configurar controles Swing
        panelControl = new JPanel();
        botonIniciar = new JButton("Iniciar");
        botonPausar = new JButton("Pausar");
        botonReiniciar = new JButton("Reiniciar");
        
        botonPausar.setEnabled(false); // Deshabilitar botón de pausa inicialmente

        panelControl.add(botonIniciar);
        panelControl.add(botonPausar);
        panelControl.add(botonReiniciar);

        // Agregar componentes a la ventana
        setLayout(new java.awt.BorderLayout());
        add(panelControl, java.awt.BorderLayout.NORTH);
        add(jfxPanel, java.awt.BorderLayout.CENTER);
        
        // Inicializar la escena JavaFX en un hilo de JavaFX
        Platform.runLater(this::createScene);
    }
    
    /**
     * Crea la escena 3D con JavaFX
     */
    private void createScene() {
        // Grupo raíz para todos los elementos 3D
        root3D = new Group();
        
        // Grupo específico para planetas
        planetGroup = new Group();
        root3D.getChildren().add(planetGroup);
        
        // Crear cámara para perspectiva 3D
        camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(100000.0);
        camera.setTranslateZ(-1000);
        
        // Crear escena 3D con fondo negro
        subScene3D = new SubScene(root3D, jfxPanel.getWidth(), jfxPanel.getHeight(), true, SceneAntialiasing.BALANCED);
        subScene3D.setFill(Color.BLACK);
        subScene3D.setCamera(camera);
        
        // Agregar transformaciones de rotación a la escena
        root3D.getTransforms().addAll(rotateX, rotateY);
        
        // Manejar eventos de ratón para rotación
        subScene3D.setOnMousePressed(event -> {
            anchorX = event.getSceneX();
            anchorY = event.getSceneY();
            anchorAngleX = rotateX.getAngle();
            anchorAngleY = rotateY.getAngle();
        });
        
        subScene3D.setOnMouseDragged(event -> {
            rotateX.setAngle(anchorAngleX - (anchorY - event.getSceneY()));
            rotateY.setAngle(anchorAngleY + (anchorX - event.getSceneX()));
        });
        
        // Agregar control de zoom con la rueda del ratón
        subScene3D.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                double delta = event.getDeltaY();
                if (controller != null) {
                    if (delta > 0) {
                        controller.zoomIn(0.1);
                    } else {
                        controller.zoomOut(0.1);
                    }
                }
            }
        });
        
        // Crear layout principal
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(subScene3D);
        
        // Agregar información
        Text infoText = new Text("Simulador del Sistema Solar - Utilice el ratón para rotar la vista");
        infoText.setFill(Color.WHITE);
        HBox infoBox = new HBox(10, infoText);
        borderPane.setBottom(infoBox);
        
        // Crear escena principal
        scene = new Scene(borderPane);
        jfxPanel.setScene(scene);
        
        // Ajustar tamaño de la sub-escena cuando cambie el tamaño del panel
        jfxPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                Platform.runLater(() -> {
                    subScene3D.setWidth(jfxPanel.getWidth());
                    subScene3D.setHeight(jfxPanel.getHeight());
                });
            }
        });
    }

    /**
     * Establece el controlador y configura los oyentes de eventos.
     * @param controller El controlador de la simulación
     */
    public void setController(com.solarsim.controller.SimulationController controller) {
        this.controller = controller;
        initializeListeners();
    }

    /**
     * Inicializa los oyentes de eventos para los botones de la interfaz.
     */
    private void initializeListeners() {
        botonIniciar.addActionListener(e -> {
            if (controller != null) {
                controller.startSimulation();
                botonIniciar.setEnabled(false);
                botonPausar.setEnabled(true);
            }
        });

        botonPausar.addActionListener(e -> {
            if (controller != null) {
                controller.pauseSimulation();
                botonIniciar.setEnabled(true);
                botonPausar.setEnabled(false);
            }
        });

        botonReiniciar.addActionListener(e -> {
            if (controller != null) {
                controller.resetSimulation();
                botonIniciar.setEnabled(true);
                botonPausar.setEnabled(false);
            }
        });
    }

    /**
     * Accede al grupo de planetas para poder actualizarlo.
     * @return El grupo que contiene los planetas
     */
    public Group getPlanetGroup() {
        return planetGroup;
    }

    /**
     * Accede a la cámara 3D para su configuración.
     * @return La cámara de la escena 3D
     */
    public PerspectiveCamera getCamera() {
        return camera;
    }
    
    /**
     * Accede al panel JavaFX.
     * @return El panel JavaFX para integración Swing/JavaFX
     */
    public JFXPanel getJFXPanel() {
        return jfxPanel;
    }

    /**
     * Método principal para pruebas independientes de la vista.
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimulationView view = new SimulationView();
            view.setVisible(true);
        });
    }
}