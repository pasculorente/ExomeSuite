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

import exomesuite.ExomeSuite;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Prompt questions.
 *
 * @author Pascual Lorente Arencibia (pasculorente@gmail.com)
 */
public class Dialog extends VBox {

    private final Button yes = new Button();
    private final Button no = new Button();
    private final Button cancel = new Button();
    private final Label message = new Label("Yes or no?");
    private final HBox hbox = new HBox();
    private Response response = Response.CANCEL;
    private static final String DEFAULT_YES = ExomeSuite.getResources().getString("yes");
    private static final String DEFAULT_NO = ExomeSuite.getResources().getString("no");
    private static final String DEFAULT_CANCEL = ExomeSuite.getResources().getString("cancel");
    private final Stage stage = new Stage();

    /**
     * Creates an instance of dialog. Use {@code showYesNo()} or {@code showYesNoCancel()} to make
     * the Dialog visible.
     */
    public Dialog() {
        setMinWidth(350);
        getChildren().setAll(message, hbox);
        hbox.setPadding(new Insets(10));
        message.setPadding(new Insets(10));
        setAlignment(Pos.TOP_CENTER);
        hbox.setAlignment(Pos.TOP_RIGHT);
        hbox.setSpacing(5);
        Scene scene = new Scene(this);
        stage.setScene(scene);
        yes.setOnAction(e -> {
            response = Response.YES;
            stage.close();
        });
        no.setOnAction(e -> {
            response = Response.NO;
            stage.close();
        });
        cancel.setOnAction(e -> {
            response = Response.CANCEL;
            stage.close();
        });

    }

    /**
     * Prompts a yes/no question. If no texts are provided for any of the alternative texts, or they
     * are set to null, "Yes", "No" and "Cancel" strings will be used. Empty Strings are valid as
     * alternative text.
     *
     * @param title title for stage
     * @param text quetion text
     * @param yesText an alternative text for yes
     * @param noText an alternative text for no
     * @return YES, NO or CANCEL
     */
    public Response showYesNo(String title, String text, String yesText, String noText) {
        hbox.getChildren().setAll(yes, no);
        yes.setText(yesText == null ? DEFAULT_YES : yesText);
        no.setText(noText == null ? DEFAULT_NO : noText);
        show(title, text);
        return response;
    }

    /**
     * Prompts a yes/no/cancel question.
     *
     * @param title title for stage
     * @param text quetion text
     * @param yesText an alternative text for yes
     * @param noText an alternative text for no
     * @param cancelText alternative text for cancel
     * @return YES, NO or CANCEL
     */
    public Response showYesNoCancel(String title, String text, String yesText, String noText, String cancelText) {
        hbox.getChildren().setAll(yes, no, cancel);
        yes.setText(yesText == null ? DEFAULT_YES : yesText);
        no.setText(noText == null ? DEFAULT_NO : noText);
        cancel.setText(cancelText == null ? DEFAULT_CANCEL : cancelText);
        show(title, text);
        return response;
    }

    private void show(String title, String text) {
        stage.setTitle(title);
        message.setText(text);
        response = Response.CANCEL;
        stage.centerOnScreen();
        stage.setAlwaysOnTop(true);
        stage.initStyle(StageStyle.UTILITY);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    /**
     * Sets an icon for the yes button.
     *
     * @param icon the icon
     */
    public void setYesIcon(Node icon) {
        yes.setGraphic(icon);
    }

    /**
     * Sets an icon for the no button.
     *
     * @param icon the icon
     */
    public void setNoIcon(Node icon) {
        no.setGraphic(icon);
    }

    /**
     * Sets an icon for the cancel button.
     *
     * @param icon the icon
     */
    public void setCancelIcon(Node icon) {
        cancel.setGraphic(icon);
    }

    /**
     * Dialog result: YES, NO and CANCEL.
     */
    public enum Response {

        /**
         * When a dialog window is accepted (normally by clicking on "Yes" button.
         */
        YES,
        /**
         * When a dialog window is denied (normally by clicking on "No" button.
         */
        NO,
        /**
         * When a dialog window is canceled (by clicking on "Cancel" or closing the dialog).
         */
        CANCEL
    };

}
