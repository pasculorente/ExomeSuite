/*
 * Copyright (C) 2014 UICHUIMI
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package exomesuite;

import exomesuite.tool.GenomeManager;
import exomesuite.tsvreader.TSVReader;
import exomesuite.utils.Config;
import exomesuite.utils.OS;
import exomesuite.utils.ToolBarButton;
import exomesuite.vcfreader.CombineVariants;
import exomesuite.vcfreader.VCFReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controller class for the main window. Manages projects, adds tabs to the
 *
 * @author Pascual Lorente Arencibia
 */
public class MainViewController {

    /**
     * The config of the application.
     */
    private static Config config;

    /**
     * The TabPane where all the projects views are put. Into the tabPane there are only Nodes. The
     * {@link Project} are stored into projectList.
     */
    @FXML
    private TabPane projects;

    /**
     * An ArrayList to store all the opened projects.
     */
    private final List<Project> projectList = new ArrayList<>();
    @FXML
    private MenuItem openMenu;
    @FXML
    private MenuItem newMenu;
    @FXML
    private MenuItem databaseMenu;
    @FXML
    private FlowPane toolBar;
    @FXML
    private MenuItem openTSV;
    @FXML
    private MenuItem openVCFMenu;
    @FXML
    private MenuItem combineVCFMenu;

    /**
     * Puts into the {@code tabPane} the open Button, new Button and Databases Button.
     */
    public void initialize() {
        config = new Config(new File("exomesuite.config"));
        setMenus();
        setToolBar();
    }

    /**
     * Opens a FileChooser and lets the user open a .config file. If returned file is not null, it
     * will call {@code addProjectTab}.
     */
    private void openProject() {
        File f = OS.openFile("Config file", OS.CONFIG_FILTER);
        if (f == null) {
            return;
        }
        File path = f.getParentFile().getParentFile();
        String name = f.getParentFile().getName();
        addProjectToTabPane(new Project(name, path, Project.Type.SINGLE));
    }

    /**
     * Adds the project view to a new tab in the tabPane and the project to projectList.
     *
     */
    private void addProjectToTabPane(Project project) {
        final Tab tab = new Tab(project.getName());
        tab.setContent(project.getView());
        tab.setOnCloseRequest((Event event) -> {
            if (!project.close()) {
                event.consume();
            }
        });
        tab.setGraphic(new ImageView("exomesuite/img/single.png"));
        projects.getTabs().add(tab);
        projectList.add(project);
        projects.getSelectionModel().select(tab);
    }

    /**
     * Gets the Config of this project.
     *
     * @return the Config value of this project.
     */
    public static Config getConfig() {
        return config;
    }

    /**
     * Checks if all projects can be close. Iterates over projectList, if any of the projects
     * couldn't be closed, returns false.
     *
     * @return false if all projects can be closed, true otherwise.
     */
    boolean canClose() {
        AtomicBoolean exit = new AtomicBoolean(true);
        projectList.forEach((Project project) -> {
            if (!project.close()) {
                exit.set(false);
            }
        });
        return true;
    }

    /**
     * Gets a Vbox with a TextField for each database.
     *
     * @return the vbox
     */
    private VBox getDatabasesView() {
        String[] configs = {Config.MILLS, Config.PHASE1, Config.DBSNP, Config.OMNI, Config.HAPMAP};
        String[] labels = {"Mills and 1000G indels", "1000G phase1 indels", "dbSNP", "1000G OMNI",
            "Hapmap"};
        GridPane grid = new GridPane();
        int i;
        for (i = 0; i < configs.length; i++) {
            TextField textField = getDatabaseTextField(configs[i], labels[i], OS.VCF_FILTER);
//            TextField tf = getVcfParam(configs[i], labels[i]);
            Label lab = new Label(labels[i]);
            grid.addRow(i, lab, textField);
        }
        ColumnConstraints c1 = new ColumnConstraints();
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(c1, c2);
        TextField ensembl = getDatabaseTextField(Config.ENSEMBL_EXONS,
                "Ensembl exons database (TSV)", OS.TSV_FILTER, OS.ALL_FILTER);
//        TextField ensembl = getTsvTf(Config.ENSEMBL_EXONS, "Ensembl exons database (TSV)");
        grid.addRow(i, new Label("Ensembl exons"), ensembl);
        grid.setPadding(new Insets(4));
        return new VBox(grid, new GenomeManager().getView());
    }

    private TextField getDatabaseTextField(String key, String promptText, ExtensionFilter... filters) {
        TextField textField = new TextField();
        if (config.containsKey(key)) {
            textField.setText(config.getProperty(key));
        }
        textField.setPromptText(promptText);
        textField.setEditable(false);
        textField.setOnAction((ActionEvent event) -> selectDatabase(key, textField, filters));
        textField.setOnMouseClicked((MouseEvent event) -> selectDatabase(key, textField, filters));
        return textField;
    }

    private void selectDatabase(String key, TextField textField, ExtensionFilter... filters) {
        File f = OS.openFile(textField, key, filters);
        if (f != null) {
            config.setProperty(key, f.getAbsolutePath());
        }
    }

    /**
     * Prepares the menus.
     */
    private void setMenus() {
        // Open menu
        openMenu.setOnAction((ActionEvent event) -> openProject());
        openMenu.setGraphic(new ImageView("exomesuite/img/open.png"));
        openMenu.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        // New menu
        newMenu.setOnAction((ActionEvent event) -> showNewPane());
        newMenu.setGraphic(new ImageView("exomesuite/img/add.png"));
        newMenu.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        // Databases menu
        databaseMenu.setOnAction((ActionEvent event) -> showDatabasesPane());
        databaseMenu.setGraphic(new ImageView("exomesuite/img/database.png"));
        databaseMenu.setAccelerator(new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN));
        // Open TSV
        openTSV.setOnAction((ActionEvent event) -> openTSV());
        // VCF menu
        openVCFMenu.setOnAction((ActionEvent event) -> openVCF());
        combineVCFMenu.setOnAction((ActionEvent event) -> combineVCF());

    }

    private void setToolBar() {
        Button open = new ToolBarButton("open.png", "Open project... Ctrl+O", "Open");
        open.setOnAction((ActionEvent event) -> openProject());
        Button newProject = new ToolBarButton("add.png", "New project... Ctrl+N", "New");
        newProject.setOnAction((ActionEvent event) -> showNewPane());
        Button db = new ToolBarButton("database.png", "Select databases... Ctrl+D", "Databases");
        db.setOnAction((ActionEvent event) -> showDatabasesPane());

        toolBar.getChildren().addAll(open, newProject, db);
    }

    private void showNewPane() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("NewProjectView.fxml"));
            loader.load();
            NewProjectViewController controller = loader.getController();
            Stage stage = new Stage();
            Scene scen = new Scene(loader.getRoot());
            stage.setScene(scen);
            stage.setTitle("Create new project");
            stage.initModality(Modality.APPLICATION_MODAL);
            controller.getAcceptButton().setOnAction((ActionEvent event) -> {
                String name = controller.getName();
                File path = controller.getPath();
                File forward = controller.getForward();
                File reverse = controller.getReverse();
                if (!name.isEmpty() && !(path == null) && !(forward == null) && !(reverse == null)) {
                    Project project = new Project(name, path, Project.Type.SINGLE);
                    project.getConfig().setProperty(Config.FORWARD, forward.getAbsolutePath());
                    project.getConfig().setProperty(Config.REVERSE, reverse.getAbsolutePath());
                    addProjectToTabPane(project);
                    controller.clear();
                    stage.close();
                }
            });
            stage.showAndWait();
        } catch (IOException ex) {
            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void showDatabasesPane() {
        Scene scene = new Scene(getDatabasesView());
        Stage stage = new Stage();
        stage.setWidth(800);
        stage.setScene(scene);
        stage.setTitle("Databases manager");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    public void combineVCF() {
        new CombineVariants().show();

    }

    private void openTSV() {
        File f = OS.openFile("Choose any file", OS.MIST_FILTER, OS.TSV_FILTER, OS.ALL_FILTER);
        if (f != null) {
            new TSVReader(f).show();
        }
    }

    private void openVCF() {
        File f = OS.openFile("Select a VCF file", OS.VCF_FILTER);
        if (f != null) {
            new VCFReader(f).show();
        }
    }
}
