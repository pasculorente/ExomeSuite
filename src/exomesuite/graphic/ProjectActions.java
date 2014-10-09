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

import exomesuite.project.Action;
import exomesuite.project.AlignAction;
import exomesuite.project.CallAction;
import exomesuite.project.MistAction;
import exomesuite.project.Project;
import exomesuite.systemtask.SystemTask;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * An hbox which contains buttons that perform actions.
 *
 * @author Pascual Lorente Arencibia
 */
public class ProjectActions extends VBox {

    private Project project;

    @FXML
    private ProgressBar progressBar;
    @FXML
    private HBox buttons;
    @FXML
    private Label message;
    @FXML
    private FlatButton cancel;

    private SystemTask task;

    private final List<Action> actions = new ArrayList<>();

    public ProjectActions() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ProjectActions.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(ProjectActions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    public void initialize() {
        cancel.setOnAction((ActionEvent event) -> {
            if (task != null && task.isRunning()) {
                task.cancel(true);
            }
        });
        cancel.setDisable(true);
        progressBar.setProgress(0);
        progressBar.setVisible(false);
        cancel.setVisible(false);

        Action align = new AlignAction("align.png", "Align genome", "Select FASTQ files first");
        Action call = new CallAction("call.png", "Call variants", "Align sequences first");
        Action mist = new MistAction("mist.png", "Mist analysis", "Align sequences first");
        actions.add(align);
        actions.add(call);
        actions.add(mist);
    }

    public void setProject(Project project) {
        if (task == null || !task.isRunning()) {
            this.project = project;
            refreshActions();
        }
    }

    private void refreshActions() {
        buttons.getChildren().clear();
        actions.forEach((Action a) -> {
            FlatButton fb = new FlatButton(a.getIcon(), a.isDisabled(project)
                    ? a.getDisableDescription() : a.getDescription());
            fb.setDisable(a.isDisabled(project));
            fb.setOnAction((ActionEvent event) -> call(a));
            buttons.getChildren().add(fb);
        });
    }

    private void call(Action a) {
        // Get the task form the action, getTask must configure using project
        task = a.getTask(project);
        if (task == null) {
            return;
        }
        // Bind progress
        progressBar.progressProperty().bind(task.progressProperty());
        message.textProperty().bind(task.messageProperty());
        // Bind end actions, cancelled or succeded
        task.setOnCancelled((WorkerStateEvent event) -> cancelled(a, task));
        task.setOnSucceeded((WorkerStateEvent event) -> succeded(a, task));
        // Disable action buttons, only one action at a time
        buttons.getChildren().forEach((Node node) -> ((FlatButton) node).setDisable(true));
        // Launch the task
        new Thread(task).start();
        // Enable cancel button and show progress
        cancel.setDisable(false);
        progressBar.setVisible(true);
        cancel.setVisible(true);
    }

    private void cancelled(Action a, SystemTask t) {
        a.onCancelled(project, t);
        unbind();
        message.setText(a.getDescription() + " cancelled: code " + t.getValue());
        cancel.setDisable(true);
        refreshActions();
    }

    private void succeded(Action a, SystemTask t) {
        a.onSucceeded(project, t);
        unbind();
        message.setText(a.getDescription() + " succeded: code " + t.getValue());
        cancel.setDisable(true);
        refreshActions();
    }

    private void unbind() {
        progressBar.progressProperty().unbind();
        progressBar.setProgress(0);
        message.textProperty().unbind();
    }

}
