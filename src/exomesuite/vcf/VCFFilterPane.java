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
package exomesuite.vcf;

import exomesuite.graphic.SizableImage;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 *
 * @author Pascual Lorente Arencibia
 */
public class VCFFilterPane extends VBox {

    private final Label staticInfo = new Label();
    private final ComboBox<VCFFilter.Field> field = new ComboBox<>();
    private final ComboBox<VCFFilter.Connector> connector = new ComboBox<>();
    private final ComboBox<String> info = new ComboBox<>();
    private final TextField value = new TextField();
    private final Button accept = new Button(null, new SizableImage("exomesuite/img/accept.png", 16));
    private final Button cancel = new Button(null, new SizableImage("exomesuite/img/cancel.png", 16));
    private final Button delete = new Button(null, new SizableImage("exomesuite/img/delete.png", 16));
    private EventHandler onAccept, onDelete;
    private final HBox activePane = new HBox(field, info, connector, value, accept, cancel);
    private VCFFilter filter;

    public VCFFilterPane(List<String> infos) {
        filter = new VCFFilter(VCFFilter.Connector.EQUALS, VCFFilter.Field.CHROMOSOME, infos.get(0));
        info.getItems().addAll(infos);
        initialize();
    }

    private void initialize() {
        field.getItems().setAll(VCFFilter.Field.values());
        connector.getItems().setAll(VCFFilter.Connector.values());
        accept.setOnAction(e -> accept());
        cancel.setOnAction(e -> toPassive());
        setOnMouseClicked(e -> startEdit());
        value.setOnAction(e -> accept());
        delete.setOnAction(e -> delete());
        value.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                accept();
            } else if (e.getCode() == KeyCode.ESCAPE) {
                toPassive();
            }
        });
        field.setOnAction(e -> {
            if (field.getSelectionModel().getSelectedItem() == VCFFilter.Field.INFO) {
                info.setDisable(false);
            } else {
                info.setDisable(true);
            }
            value.requestFocus();
        });
        info.setOnAction(e -> value.requestFocus());
        connector.setOnAction(e -> value.requestFocus());
        staticInfo.setText("Click to set the filter");
        HBox.setHgrow(value, Priority.SOMETIMES);
        getStyleClass().add("filter-box");
        toPassive();
    }

    public VCFFilter getFilter() {
        return filter;
    }

    public void setFilter(VCFFilter filter) {
        this.filter = filter;
    }

    private void accept() {
        filter.setField(field.getSelectionModel().getSelectedItem());
        filter.setConnector(connector.getSelectionModel().getSelectedItem());
        filter.setValue(value.getText());
        filter.setSelectedInfo(info.getValue());
        getChildren().clear();
        setStaticInfo();
        toPassive();
        if (onAccept != null) {
            onAccept.handle(new ActionEvent());
        }
    }

    private void toPassive() {
        Separator separator = new Separator(Orientation.HORIZONTAL);
        separator.setVisible(false);
        HBox.setHgrow(separator, Priority.ALWAYS);
        HBox box = new HBox(staticInfo, separator, delete);
        box.setAlignment(Pos.CENTER);
        getChildren().setAll(box);
    }

    private void startEdit() {
        if (info.getSelectionModel().isEmpty()) {
            info.getSelectionModel().select(0);
        }
        if (connector.getSelectionModel().isEmpty()) {
            connector.getSelectionModel().select(VCFFilter.Connector.EQUALS);
        }
        if (field.getSelectionModel().isEmpty()) {
            field.getSelectionModel().select(VCFFilter.Field.CHROMOSOME);
        }
        getChildren().setAll(activePane);
        value.requestFocus();
    }

    private void setStaticInfo() {
        String f = (filter.getField() == VCFFilter.Field.INFO)
                ? filter.getSelectedInfo() : filter.getField().name();
        String v = filter.getValue() == null || filter.getValue().isEmpty()
                ? "[empty]" : filter.getValue();
        staticInfo.setText(f + " " + filter.getConnector() + " " + v);
    }

    public void setOnAccept(EventHandler onAccept) {
        this.onAccept = onAccept;
    }

    public void setOnDelete(EventHandler onDelete) {
        this.onDelete = onDelete;
    }

    private void delete() {
        if (onDelete != null) {
            onDelete.handle(new ActionEvent());
        }
    }

}
