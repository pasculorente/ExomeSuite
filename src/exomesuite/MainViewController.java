package exomesuite;

import exomesuite.utils.Config;
import exomesuite.utils.OS;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author uichuimi03
 */
public class MainViewController {

    @FXML
    private TabPane projects;
    @FXML
    private VBox genomes;
    private List<Tab> tabList;
    private static Config config;

    public void initialize() {
        tabList = new ArrayList<>();
        initializeManager();
        initializeGenomes();
    }

    private void initializeManager() {
        config = new Config(new File("exomesuite.config"));
        final Tab newTab = new Tab();
        Button newButton = new Button(null, new ImageView("exomesuite/img/new.png"));
        Button openButton = new Button(null, new ImageView("exomesuite/img/open.png"));
        HBox newOpenProject = new HBox(openButton, newButton);
        newTab.setGraphic(newOpenProject);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("WelcomeView.fxml"));
            loader.load();
            newTab.setContent(loader.getRoot());
        } catch (IOException ex) {
            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        newTab.setClosable(false);
        projects.getTabs().add(newTab);
        newButton.setOnAction((ActionEvent event) -> {
            newProject();
        });
        openButton.setOnAction((ActionEvent event) -> {
            openProject();
        });
    }

    private void initializeGenomes() {
        genomes.getChildren().add(new GenomeManager().getView());
    }

    private void newProject() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("NewProjectView.fxml"));
            loader.load();
            NewProjectViewController controller = loader.getController();
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.getRoot()));
            controller.setStage(stage);
            stage.showAndWait();
            if (!controller.isAccepted()) {
                return;
            }
            String name = controller.getName();
            File path = controller.getPath();
            if (name.isEmpty() || path == null) {
                return;
            }
            final Tab tab = new Tab(name);
            projects.getTabs().add(0, tab);
            Project project = new Project(name, path);
            tab.setContent(project.getToolsPane());
            tabList.add(tab);
            projects.getSelectionModel().select(tab);
        } catch (IOException ex) {
            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void openProject() {
        String[] filters = {"*.config"};
        File f = OS.openFile("Config file", ".config", filters);
        if (f == null) {
            return;
        }
        File path = f.getParentFile().getParentFile();
        String name = f.getParentFile().getName();
        final Tab tab = new Tab(name);
        projects.getTabs().add(0, tab);
        Project project = new Project(name, path);
        tab.setContent(project.getToolsPane());
        tabList.add(tab);
        projects.getSelectionModel().select(tab);

        System.out.println("Openninnnnnng ...");
    }

    public static String getGenome() {
        return config.getProperty("genome");
    }

    public static void setGenome(String genome) {
        config.setProperty("genome", genome);
    }

    public static Config getConfig() {
        return config;
    }

}
