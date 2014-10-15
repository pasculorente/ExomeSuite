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
package exomesuite.graphic;

import exomesuite.MainViewController;
import exomesuite.project.Project;
import exomesuite.project.ProjectListener;
import exomesuite.tsvreader.TSVReader;
import exomesuite.utils.FileManager;
import exomesuite.utils.OS;
import exomesuite.vcfreader.VCFReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import org.controlsfx.dialog.Dialogs;

/**
 * Shows properties of a project.
 *
 * @author Pascual Lorente Arencibia
 */
public class ProjectInfo extends VBox implements ProjectListener {

    @FXML
    private Parameter forward;
    @FXML
    private Parameter reverse;
    @FXML
    private Parameter name;
    @FXML
    private Parameter code;
    @FXML
    private Parameter description;
    @FXML
    private Parameter path;
    @FXML
    private Parameter encoding;
    @FXML
    private Parameter genome;
    @FXML
    private Parameter alignments;
    @FXML
    private Parameter variants;
    @FXML
    private ListView<String> files;
    @FXML
    private Button addButton;

    private Project project;

    public ProjectInfo() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ProjectInfo.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(ProjectInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void initialize() {
        // By default, this panel is hidden
        setVisible(false);

        forward.addExtensionFilter(FileManager.FASTQ_FILTER);
        reverse.addExtensionFilter(FileManager.FASTQ_FILTER);
        // Add listeners to each property change, so every time a property is changed,
        // it is reflected in project.getProperties()
        forward.setOnValueChanged(event
                -> project.setProperty(Project.PropertyName.FORWARD_FASTQ, forward.getValue()));
        reverse.setOnValueChanged(event
                -> project.setProperty(Project.PropertyName.REVERSE_FASTQ, reverse.getValue()));
        name.setOnValueChanged(event
                -> project.setProperty(Project.PropertyName.NAME, name.getValue()));
        description.setOnValueChanged(event
                -> project.setProperty(Project.PropertyName.DESCRIPTION, description.getValue()));
        path.setOnValueChanged(event -> changePath());
        encoding.setOnValueChanged(event -> project.setProperty(Project.PropertyName.FASTQ_ENCODING,
                encoding.getValue()));
        genome.setOnValueChanged(event -> project.setProperty(Project.PropertyName.REFERENCE_GENOME,
                genome.getValue()));
//        alignments.addExtensionFilter(FileManager.SAM_FILTER);
//        alignments.setOnValueChanged(event
//                -> project.setProperty(Project.PropertyName.BAM_FILE, alignments.getValue()));
//        variants.addExtensionFilter(FileManager.VCF_FILTER);
//        variants.setOnValueChanged(event
//                -> project.setProperty(Project.PropertyName.VCF_FILE, variants.getValue()));
//        path.setDisable(true);

        files.setContextMenu(getFilesContextMenu());
        // humm, with only one file I cannot listen to chagnes in selected item.
        files.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                showFile();
            }
        });
        addButton.setGraphic(new ImageView("exomesuite/img/addFile.png"));
        addButton.setOnAction(event -> addFile());
    }

    public void setProject(Project project) {
        if (project == null) {
            setVisible(false);
            this.project = null;
        } else {
            setVisible(true);
            this.project = project;
            forward.setValue(project.getProperty(Project.PropertyName.FORWARD_FASTQ, ""));
            reverse.setValue(project.getProperty(Project.PropertyName.REVERSE_FASTQ, ""));
            name.setValue(project.getProperty(Project.PropertyName.NAME, ""));
            code.setValue(project.getProperty(Project.PropertyName.CODE, ""));
            description.setValue(project.getProperty(Project.PropertyName.DESCRIPTION, ""));
            path.setValue(project.getProperty(Project.PropertyName.PATH, ""));
            encoding.setOptions(OS.getSupportedEncodings());
            encoding.setValue(project.getProperty(Project.PropertyName.FASTQ_ENCODING, ""));
            genome.setOptions(OS.getSupportedReferenceGenomes());
            genome.setValue(project.getProperty(Project.PropertyName.REFERENCE_GENOME, ""));
//            alignments.setValue(project.getProperty(Project.PropertyName.BAM_FILE, ""));
//            variants.setValue(project.getProperty(Project.PropertyName.VCF_FILE, ""));
            files.getItems().clear();
            String fs = project.getProperty(Project.PropertyName.FILES);
            if (fs != null && !fs.isEmpty()) {
                files.getItems().setAll(Arrays.asList(fs.split(";")));
            }
        }
    }

    private void addFile() {
        File f = FileManager.openFile("Select a file", FileManager.ALL_FILTER);
        if (f != null && !files.getItems().contains(f.getAbsolutePath())) {
            files.getItems().add(f.getAbsolutePath());
            // Add file to properties
            String fils = project.getProperty(Project.PropertyName.FILES, "");
            fils += f.getAbsolutePath() + ";";
            project.setProperty(Project.PropertyName.FILES, fils);
        }
    }

    private void showFile() {
        String f = files.getSelectionModel().getSelectedItem();
        if (f == null || f.isEmpty()) {
            return;
        }
        File file = new File(f);
        TabPane wa = MainViewController.getWorkingArea();
        // If it is already open, do not open another tab, do not be a spammer
        for (Tab t : wa.getTabs()) {
            if (t.getText().equals(file.getName())) {
                wa.getSelectionModel().select(t);
                return;
            }
        }
        Tab t = new Tab(file.getName());
        // Use TSV only on TSV files
        if (f.endsWith(".tsv") || f.endsWith(".txt") || f.endsWith(".mist")) {
            t.setContent(new TSVReader(file).get());
        } else if (f.endsWith(".vcf")) {
            t.setContent(new VCFReader(file).getView());
        } else {
            return;
        }
        MainViewController.getWorkingArea().getTabs().add(t);
    }

    private void removeFile() {
        String file = files.getSelectionModel().getSelectedItem();
        int index = files.getSelectionModel().getSelectedIndex();
        // Remove file from properties
        String fils = project.getProperty(Project.PropertyName.FILES, "");
        fils = fils.replace(file + ";", "");
        project.setProperty(Project.PropertyName.FILES, fils);
//            new File(file).delete(); NOO, don't delete the file. Or at least ask the user for
        files.getItems().remove(index);
    }

    private void changePath() {
        // Move files and directories
        final File from = new File(project.getProperty(Project.PropertyName.PATH));
        final File to = new File(path.getValue());
        clone(from, to);
        // Change properties by replacing path in all properties
        project.getProperties().forEach((Object t, Object u) -> {
            String key = (String) t;
            String value = (String) u;
            if (value.startsWith(from.getAbsolutePath())) {
                project.getProperties().setProperty(key,
                        value.replace(from.getAbsolutePath(), to.getAbsolutePath()));
            }
        });
        // Force properties file to be written
        project.setProperty(Project.PropertyName.PATH, to.getAbsolutePath());
    }

    private void clone(File from, File to) {
        for (File f : from.listFiles()) {
            if (f.isFile()) {
                Path source = f.toPath();
                Path target = new File(to, f.getName()).toPath();
                try {
                    Files.copy(source, target, COPY_ATTRIBUTES);
                    f.delete();
                } catch (IOException ex) {
                    Dialogs.create().showException(ex);
                    Logger.getLogger(ProjectInfo.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else if (f.isDirectory()) {
                File newPath = new File(to, f.getName());
                newPath.mkdirs();
                clone(f, newPath);
            } else {
                f.delete();
            }
        }
        from.delete();
    }

    @Override
    public void projectChanged(Project.PropertyName property) {
        // Outside ProjectInfo, only a few properties can be changed.
        switch (property) {
            case BAM_FILE:
                alignments.setValue(project.getProperty(Project.PropertyName.BAM_FILE, ""));
                break;
            case VCF_FILE:
                variants.setValue(project.getProperty(Project.PropertyName.VCF_FILE, ""));
                break;
            case FILES:
                files.getItems().clear();
                String fs = project.getProperty(Project.PropertyName.FILES);
                if (fs != null && !fs.isEmpty()) {
                    files.getItems().setAll(Arrays.asList(fs.split(";")));
                }
                break;

        }
    }

    private ContextMenu getFilesContextMenu() {
        MenuItem delete = new MenuItem("Delete", new ImageView("exomesuite/img/cancel4.png"));
        delete.setOnAction(event -> removeFile());
        return new ContextMenu(delete);
    }

}
