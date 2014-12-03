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

import exomesuite.vcf.Variant;
import exomesuite.vcf.VariantListener;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

/**
 *
 * @author Pascual Lorente Arencibia (pasculorente@gmail.com)
 */
public class VariantInfo extends FlowPane implements VariantListener {

    public VariantInfo() {
    }

    @Override
    public void variantChanged(Variant variant) {
        getChildren().clear();
        if (variant != null) {
            for (String info : variant.getInfo().split(";")) {
                Label label = new Label(info);
                label.getStyleClass().add("parameter");
                getChildren().add(label);
            }
        }
    }

}