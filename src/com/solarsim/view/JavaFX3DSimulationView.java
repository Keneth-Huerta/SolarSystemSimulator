package com.solarsim.view;

import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import com.solarsim.controller.SimulationController;

/**
 * Clase que implementa la interfaz de usuario 3D del simulador usando JavaFX puro.
 * Proporciona visualización 3D real de planetas y órbitas.
 */
public class JavaFX3DSimulationView {
    
    // Componentes JavaFX
    private Stage stage;
    private Scene scene;
    private SubScene subScene3D;
    private Group root3D;
    private PerspectiveCamera camera;
    private Group planetGroup;
    
    // Botones de control
    private Button botonIniciar;
    private Button botonPausar;
    private Button botonReiniciar;
    private Button botonCambiarVista; // Nuevo botón para cambiar vista
    
    // Para rotación 3D
    private double anchorX, anchorY;
    private double anchorAngleX = 0;
    private double anchorAngleY = 0;
    private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    
    // Estado de la vista
    private boolean is3DMode = true; // Estado para controlar si estamos en 3D o 2D
    
    // Controlador
    private SimulationController controller;
    
    // Estado de zoom
    private double zoomLevel = 1.0;

    /**
     * Constructor que recibe el stage principal desde la clase Main.
     * 
     * @param primaryStage El stage principal de la aplicación JavaFX
     */
    public JavaFX3DSimulationView(Stage primaryStage) {
        this.stage = primaryStage;
        createUserInterface();
        adjustLayoutForFullScreenWithControls(); // Ajustar el diseño para pantalla completa con controles
        setupEventHandlers();
        
        // Configurar y mostrar la ventana
        stage.setMaximized(true);
        stage.show();
    }
    
    /**
     * Crea y configura todos los componentes de la interfaz gráfica.
     */
    private void createUserInterface() {
        // Crear layout principal
        BorderPane borderPane = new BorderPane();
        
        // Barra de herramientas superior
        HBox toolbar = createToolbar();
        borderPane.setTop(toolbar);
        
        // Panel de información a la izquierda
        VBox infoPanel = createInfoPanel();
        borderPane.setLeft(infoPanel);
        
        // Área 3D para la simulación
        createSimulationArea();
        borderPane.setCenter(subScene3D);
        
        // Escena principal
        scene = new Scene(borderPane, 1200, 800);
        stage.setScene(scene);
        
        // Añadir controles de teclado para toda la escena
        configureKeyboardControls();
    }
    
    /**
     * Crea la barra de herramientas con los botones de control.
     */
    private HBox createToolbar() {
        HBox toolbar = new HBox(10);
        toolbar.setStyle("-fx-padding: 10; -fx-background-color: #333333;");
        
        botonIniciar = new Button("Iniciar");
        botonIniciar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        
        botonPausar = new Button("Pausar");
        botonPausar.setStyle("-fx-background-color: #FFC107; -fx-text-fill: white;");
        botonPausar.setDisable(true);
        
        botonReiniciar = new Button("Reiniciar");
        botonReiniciar.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
        
        botonCambiarVista = new Button("Cambiar Vista"); // Nuevo botón para cambiar vista
        botonCambiarVista.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        
        Label titleLabel = new Label("Sistema Solar 3D");
        titleLabel.setFont(new Font("Arial", 20));
        titleLabel.setStyle("-fx-text-fill: white;");
        
        toolbar.getChildren().addAll(titleLabel, botonIniciar, botonPausar, botonReiniciar, botonCambiarVista);
        
        return toolbar;
    }
    
    /**
     * Crea el panel de información con datos sobre los planetas.
     */
    private VBox createInfoPanel() {
        VBox infoPanel = new VBox(10);
        infoPanel.setStyle("-fx-padding: 10; -fx-background-color: #222222;");
        infoPanel.setPrefWidth(200);
        
        Label infoTitle = new Label("Información");
        infoTitle.setFont(new Font("Arial", 16));
        infoTitle.setStyle("-fx-text-fill: white;");
        
        Label instructions = new Label(
            "- Arrastra el ratón para rotar la vista\n" +
            "- Usa la rueda para hacer zoom\n" +
            "- Teclas WASD para movimiento\n" +
            "- R para reiniciar la vista"
        );
        instructions.setStyle("-fx-text-fill: #BBBBBB; -fx-wrap-text: true;");
        
        infoPanel.getChildren().addAll(infoTitle, instructions);
        
        return infoPanel;
    }
    
    /**
     * Crea el área de simulación 3D con la cámara y el grupo de planetas.
     */
    private void createSimulationArea() {
        // Grupo raíz para todos los elementos 3D
        root3D = new Group();
        
        // Grupo específico para planetas
        planetGroup = new Group();
        root3D.getChildren().add(planetGroup);
        
        // Cámara para vista en perspectiva
        camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(100000.0);
        // Posicionar la cámara para que el sistema solar esté centrado
        camera.setTranslateZ(-1500);
        camera.setTranslateY(-200);  // Reducido para que se vea más centrado
        camera.setTranslateX(200);   // Ajuste para centrar los planetas
        
        // Escena 3D con antialiasing para mejor calidad visual
        subScene3D = new SubScene(root3D, 800, 600, true, SceneAntialiasing.BALANCED);
        subScene3D.setFill(Color.BLACK);
        subScene3D.setCamera(camera);
        
        // Añadir rotaciones al grupo root
        root3D.getTransforms().addAll(rotateX, rotateY);
    }
    
    /**
     * Configura los manejadores de eventos de ratón para la rotación y zoom.
     */
    private void setupEventHandlers() {
        // Manejar eventos de arrastre para rotación de la escena
        subScene3D.setOnMousePressed(event -> {
            anchorX = event.getSceneX();
            anchorY = event.getSceneY();
            anchorAngleX = rotateX.getAngle();
            anchorAngleY = rotateY.getAngle();
        });
        
        subScene3D.setOnMouseDragged(event -> {
            // Calcular la rotación basada en el movimiento del ratón
            rotateX.setAngle(anchorAngleX - (anchorY - event.getSceneY()) * 0.25);
            rotateY.setAngle(anchorAngleY + (anchorX - event.getSceneX()) * 0.25);
        });
        
        // Configurar zoom con la rueda del ratón
        subScene3D.setOnScroll(this::handleScroll);
        
        // Configurar eventos de los botones cuando se establezca el controlador
    }
    
    /**
     * Maneja el evento de scroll para hacer zoom en la escena 3D.
     */
    private void handleScroll(ScrollEvent event) {
        double delta = event.getDeltaY();
        // Pasar al controlador cuando esté disponible
        if (controller != null) {
            if (delta > 0) {
                controller.zoomIn(0.1);
            } else {
                controller.zoomOut(0.1);
            }
        } else {
            // Zoom provisional mientras no hay controlador
            if (delta > 0) {
                camera.setTranslateZ(camera.getTranslateZ() * 0.9);
                zoomLevel += 0.1;
            } else {
                camera.setTranslateZ(camera.getTranslateZ() * 1.1);
                zoomLevel -= 0.1;
            }
            
            // Limitar el zoom
            if (zoomLevel < 0.2) zoomLevel = 0.2;
            if (zoomLevel > 5.0) zoomLevel = 5.0;
        }
    }
    
    /**
     * Configura los controles de teclado para la navegación.
     */
    private void configureKeyboardControls() {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            double moveAmount = 50.0;
            
            switch (event.getCode()) {
                case W:
                    camera.setTranslateY(camera.getTranslateY() + moveAmount);
                    break;
                case S:
                    camera.setTranslateY(camera.getTranslateY() - moveAmount);
                    break;
                case A:
                    camera.setTranslateX(camera.getTranslateX() + moveAmount);
                    break;
                case D:
                    camera.setTranslateX(camera.getTranslateX() - moveAmount);
                    break;
                case R:
                    // Reiniciar posición de la cámara
                    camera.setTranslateX(200);
                    camera.setTranslateY(-200);
                    camera.setTranslateZ(-1500);
                    rotateX.setAngle(0);
                    rotateY.setAngle(0);
                    break;
                default:
                    break;
            }
        });
    }
    
    /**
     * Establece el controlador y configura los botones.
     * @param controller El controlador de simulación
     */
    public void setController(SimulationController controller) {
        this.controller = controller;
        
        // Configurar eventos de los botones
        botonIniciar.setOnAction(e -> {
            if (controller != null) {
                controller.startSimulation();
                botonIniciar.setDisable(true);
                botonPausar.setDisable(false);
            }
        });
        
        botonPausar.setOnAction(e -> {
            if (controller != null) {
                controller.pauseSimulation();
                botonIniciar.setDisable(false);
                botonPausar.setDisable(true);
            }
        });
        
        botonReiniciar.setOnAction(e -> {
            if (controller != null) {
                controller.resetSimulation();
                botonIniciar.setDisable(false);
                botonPausar.setDisable(true);
            }
        });
        
        botonCambiarVista.setOnAction(e -> {
            if (is3DMode) {
                // Cambiar a vista 2D
                camera.setTranslateZ(-5000); // Alejar la cámara para una vista 2D
                rotateX.setAngle(90); // Rotar la vista para que sea desde arriba
                is3DMode = false;
            } else {
                // Cambiar a vista 3D
                camera.setTranslateZ(-1500); // Restaurar la posición de la cámara
                rotateX.setAngle(0); // Restaurar la rotación
                is3DMode = true;
            }
        });
    }
    
    /**
     * Obtiene el grupo de planetas para que el controlador pueda añadir elementos 3D.
     * @return El grupo de planetas
     */
    public Group getPlanetGroup() {
        return planetGroup;
    }
    
    /**
     * Obtiene la cámara para que el controlador pueda ajustar la vista.
     * @return La cámara de la escena 3D
     */
    public PerspectiveCamera getCamera() {
        return camera;
    }
    
    /**
     * Actualiza el tamaño de la subescena 3D cuando cambia el tamaño de la ventana.
     */
    public void updateSubSceneSize() {
        subScene3D.widthProperty().bind(stage.widthProperty());
        subScene3D.heightProperty().bind(stage.heightProperty());
    }

    /**
     * Modifica la interfaz para que la simulación 3D ocupe toda la pantalla,
     * pero mantiene los botones y las instrucciones visibles.
     */
    private void adjustLayoutForFullScreenWithControls() {
        BorderPane borderPane = (BorderPane) scene.getRoot();

        // Mantener la barra de herramientas superior
        HBox toolbar = createToolbar();
        toolbar.setStyle("-fx-padding: 5; -fx-background-color: #333333;");
        borderPane.setTop(toolbar);

        // Panel de información compacto a la izquierda
        VBox infoPanel = createInfoPanel();
        infoPanel.setStyle("-fx-padding: 5; -fx-background-color: rgba(34, 34, 34, 0.7);");
        infoPanel.setPrefWidth(150);
        borderPane.setLeft(infoPanel);

        // Asegurar que la subescena 3D ocupe el espacio restante
        updateSubSceneSize();
    }
}