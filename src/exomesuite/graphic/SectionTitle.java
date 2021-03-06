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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;

/**
 * Horizontal line with a label in the center. Useful to separete things. It is like a separator but
 * with a title.
 *
 * @author Pascual Lorente Arencibia
 */
public class SectionTitle extends HBox {

    @FXML
    private Label title;
    @FXML
    private Separator leftSeparator;
    @FXML
    private Separator rightSeparator;

    /**
     * Creates a new Section title.
     */
    public SectionTitle() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SectionTitle.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(SectionTitle.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Gets the text of the section.
     *
     * @return the title
     */
    public String getTitle() {
        return title.getText();
    }

    /**
     * Sets the text of the title.
     *
     * @param title the title
     */
    public void setTitle(String title) {
        this.title.setText(title);
    }

    /**
     * hLine will show or not the horizontal lines.
     *
     * @param hline true to show the h lines or false to hide them
     */
    public void setHLine(boolean hline) {
        leftSeparator.setVisible(hline);
        rightSeparator.setVisible(hline);
    }

    /**
     * hLine will show or not the horizontal lines.
     *
     * @return true if hlines are visible
     */
    public boolean isHLine() {
        return leftSeparator.isVisible();
    }

    /**
     * The paint of the background.
     *
     * @return the current paint of the background
     */
    public Paint getPaint() {
        return getBackground().getFills().get(0).getFill();
    }

    /**
     * The paint of the background.
     *
     * @param paint the paint of the background
     */
    public void setPaint(Paint paint) {
        setBackground(new Background(new BackgroundFill(paint, CornerRadii.EMPTY, Insets.EMPTY)));
    }

}
