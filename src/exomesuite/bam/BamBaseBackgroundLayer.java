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
package exomesuite.bam;

import java.util.List;

/**
 * Draws a colored bar on each base, ocuppying the whole surface.
 *
 * @author Pascual Lorente Arencibia (pasculorente@gmail.com)
 */
public class BamBaseBackgroundLayer extends BamLayer {

    /**
     * The opacity of background areas.
     */
    final static double opacity = 0.1;

    @Override
    protected void draw(BamCanvas bamCanvas) {
        final double height = bamCanvas.getHeight();
        final double baseWidth = bamCanvas.getBaseWidth().get();
        final double margin = bamCanvas.getAxisMargin().get();
        final double barwidth = baseWidth * 0.9;
        final double barHeight = height - 2 * margin;
        final double y = height - margin - barHeight;
        final List<PileUp> list = bamCanvas.getAlignments();
        int i = 0;
        double x = margin;

        while (i < list.size() && x < bamCanvas.getWidth() - margin) {
            switch (list.get(i).getReference()) {
                case 'A':
                    getGraphicsContext2D().setFill(A_COLOR.deriveColor(0, 1, 1, opacity));
                    getGraphicsContext2D().fillRect(x, y, barwidth, barHeight);
                    break;
                case 'C':
                    getGraphicsContext2D().setFill(C_COLOR.deriveColor(0, 1, 1, opacity));
                    getGraphicsContext2D().fillRect(x, y, barwidth, barHeight);
                    break;
                case 'T':
                    getGraphicsContext2D().setFill(T_COLOR.deriveColor(0, 1, 1, opacity));
                    getGraphicsContext2D().fillRect(x, y, barwidth, barHeight);
                    break;
                case 'G':
                    getGraphicsContext2D().setFill(G_COLOR.deriveColor(0, 1, 1, opacity));
                    getGraphicsContext2D().fillRect(x, y, barwidth, barHeight);
                    break;
            }
            i++;
            x += baseWidth;
        }
    }

}
