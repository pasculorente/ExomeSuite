/*
 * Copyright (C) 2015 UICHUIMI
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
package exomesuite.actions;

import exomesuite.graphic.SizableImage;
import java.util.List;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

/**
 *
 * @author Lorente Arencibia, Pascual <pasculorente@gmail.com>
 */
public class MistParameters {

    @FXML
    private ComboBox<String> alignments;
    @FXML
    private TextField threshold;
    @FXML
    private TextField length;
    @FXML
    private Button cancel;
    @FXML
    private Button accept;

    @FXML
    private Button upThreshold;
    @FXML
    private Button downThreshold;
    @FXML
    private Button upLength;
    @FXML
    private Button downLength;

    /**
     * true if user clicked on Accept
     */
    private boolean accepted = false;
    /**
     * Force to close window.
     */
    private EventHandler handler;

    @FXML
    private void initialize() {
        accept.setOnAction(event -> {
            accepted = true;
            handler.handle(event);
        });
        cancel.setOnAction(event -> handler.handle(event));
        accept.setGraphic(new SizableImage("exomesuite/img/mist.png", SizableImage.SMALL_SIZE));
        cancel.setGraphic(new SizableImage("exomesuite/img/cancel.png", SizableImage.SMALL_SIZE));
        upLength.setGraphic(new SizableImage("exomesuite/img/up.png", SizableImage.SMALL_SIZE));
        upThreshold.setGraphic(new SizableImage("exomesuite/img/up.png", SizableImage.SMALL_SIZE));
        downLength.setGraphic(new SizableImage("exomesuite/img/down.png", SizableImage.SMALL_SIZE));
        downThreshold.setGraphic(new SizableImage("exomesuite/img/down.png", SizableImage.SMALL_SIZE));
        upLength.setOnAction(event -> {
            try {
                int l = Integer.valueOf(length.getText());
                length.setText(String.valueOf(l + 1));
            } catch (NumberFormatException ex) {
            }
        });
        upThreshold.setOnAction(event -> {
            try {
                int l = Integer.valueOf(threshold.getText());
                threshold.setText(String.valueOf(l + 1));
            } catch (NumberFormatException ex) {
            }
        });
        downLength.setOnAction(event -> {
            try {
                int l = Integer.valueOf(length.getText());
                if (l > 1) {
                    length.setText(String.valueOf(l - 1));
                }
            } catch (NumberFormatException ex) {
            }
        });
        downThreshold.setOnAction(event -> {
            try {
                int l = Integer.valueOf(threshold.getText());
                if (l > 1) {
                    threshold.setText(String.valueOf(l - 1));
                }
            } catch (NumberFormatException ex) {
            }
        });
        // Avoid non digit character
        threshold.setOnKeyTyped(event -> {
            if (!Character.isDigit(event.getCharacter().charAt(0))) {
                event.consume();
            }
        });
        length.setOnKeyTyped(event -> {
            if (!Character.isDigit(event.getCharacter().charAt(0))) {
                event.consume();
            }
        });
    }

    /**
     * Sets the default selected alignments. Use first {@code setAlignmentsOptions()}
     *
     * @param alignments
     */
    public void setAlignments(String alignments) {
        this.alignments.getSelectionModel().select(alignments);
    }

    /**
     * Sets the options for the comboBox of alignments.
     *
     * @param alignments
     */
    public void setAlignmentsOptions(List<String> alignments) {
        this.alignments.getItems().addAll(alignments);
    }

    /**
     * Sets the options for the comboBox of alignments.
     *
     * @param alignments
     */
    public void setAlignmentsOptions(String... alignments) {
        this.alignments.getItems().addAll(alignments);
    }

    /**
     * Event to hear when the user clicked on accept or cancel.
     *
     * @param handler method to close the window and read parameters
     */
    public void setOnClose(EventHandler handler) {
        this.handler = handler;
    }

    /**
     * true if user clicked on the Accept button.
     *
     * @return
     */
    public boolean accepted() {
        return accepted;
    }

    public void setThreshold(int i) {
        threshold.setText(String.valueOf(i));
    }

    public void setLength(int i) {
        length.setText(String.valueOf(i));
    }

    int getSelectedThreshold() {
        return Integer.valueOf(threshold.getText());
    }

    int getSelectedLength() {
        return Integer.valueOf(threshold.getText());
    }

    String getSelectedAlignments() {
        return alignments.getValue();

    }

}
