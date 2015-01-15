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

/**
 * A boolean parameter. yes is true, no is false
 *
 * @author Pascual Lorente Arencibia (pasculorente@gmail.com)
 */
public class YesNoParam extends Param<Boolean> {

    /**
     * Creates a YesNoParam with default value TRUE
     */
    public YesNoParam() {
        setValue(Boolean.TRUE);
    }

    @Override
    protected Boolean editPassive() {
        return !getValue();
    }

    @Override
    protected String toLabel(Boolean value) {
        if (value == null) {
            return ExomeSuite.getResources().getString("no");
        }
        return value ? ExomeSuite.getResources().getString("yes")
                : ExomeSuite.getResources().getString("no");
    }

}
