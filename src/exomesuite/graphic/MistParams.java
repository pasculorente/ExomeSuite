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

import java.util.List;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * FXML Controller class
 *
 * @author Pascual Lorente Arencibia
 */
public class MistParams {

    @FXML
    private Parameter bamFile;
    @FXML
    private Parameter threshold;
    @FXML
    private Button accept;

    private EventHandler closeEvent;

    /**
     * Initializes the controller class.
     */
    public void initialize() {
        bamFile.setOnValueChanged(e -> enableAccept());
        accept.setDisable(true);
        accept.setOnAction(e -> accept(e));
    }

    public void setBamOptions(List<String> options) {
        bamFile.setOptions(options);
    }

    public String getSelectedBam() {
        return bamFile.getValue();
    }

    public int getThreshold() {
        return Integer.valueOf(threshold.getValue());
    }

    public void enableAccept() {
        if (bamFile.getValue() != null && !bamFile.getValue().isEmpty()) {
            accept.setDisable(false);
        }
    }

    private void accept(Event e) {
        if (closeEvent != null) {
            closeEvent.handle(e);
        }
    }

    public void setOnAccept(EventHandler eventHandler) {
        closeEvent = eventHandler;
    }
}
