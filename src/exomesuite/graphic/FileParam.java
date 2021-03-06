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
import exomesuite.utils.FileManager;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.stage.FileChooser;

/**
 * A Param that allows user to select Files from FileSystem. With OPEN behaviour (default), it
 * allows user to select an existing file from the FileSystem by calling
 * {@code FileManager.openFile()}. With SAVE behaviour, it allows user to create a new File, or to
 * open an existing one.
 *
 * @see FileManager
 * @author Pascual Lorente Arencibia (pasculorente@gmail.com)
 */
public class FileParam extends Param<File> {

    /**
     * The list of filters.
     */
    private final List<FileChooser.ExtensionFilter> filters = new ArrayList();
    /**
     * Flag to display full path or name.
     */
    private boolean showFullPath = false;
    /**
     * OPEN by default.
     */
    private Behaviour behaviour = Behaviour.OPEN;

    @Override
    protected File editPassive() {
        File parent = null;
        if (getValue() != null) {
            parent = getValue().getParentFile();
        }
        File file;
        String message = ExomeSuite.getStringFormatted("select.file", getTitle());
        switch (behaviour) {
            case OPEN:
                if (parent != null) {
                    file = FileManager.openFile(message, parent, filters);
                } else {
                    file = FileManager.openFile(message, filters);
                }
                return file;
            case SAVE:
                if (parent != null) {
                    file = FileManager.saveFile(message, parent, filters);
                } else {
                    file = FileManager.saveFile(message, filters);
                }
                return file;
        }
        return null;
    }

    @Override
    protected String toLabel(File value) {
        if (value == null) {
            return getPromptText();
        }
        return showFullPath ? value.getAbsolutePath() : value.getName();
    }

    /**
     * Adds a filter for the FileSelector. You can use FileManager.*_FILTER constants.
     *
     * @param filter a filter to add to the FileSelector
     */
    public void addFilter(FileChooser.ExtensionFilter filter) {
        filters.add(filter);
    }

    /**
     * When showFullPath is true, the passive label displays {@code file.getAbsolutePath()}. When
     * showFullPath is false {@code file.getName()}.
     *
     * @param showFullPath true if you want the FileParam to show the whole path or false for only
     * the name of the file
     */
    public void setShowFullPath(boolean showFullPath) {
        this.showFullPath = showFullPath;
    }

    /**
     * When showFullPath is true, the passive label displays {@code file.getAbsolutePath()}. When
     * showFullPath is false {@code file.getName()}.
     *
     * @return true if the FileParam shows the whole path or false if only the name of the file
     */
    public boolean isShowFullPath() {
        return showFullPath;
    }

    /**
     * OPEN to show an openFile dialog or CLOSE to show a saveFile dialog.
     *
     * @return the behaviour of the FileParam
     */
    public Behaviour getBehaviour() {
        return behaviour;
    }

    /**
     * OPEN to show an openFile dialog or CLOSE to show a saveFile dialog.
     *
     * @param behaviour a FileParam.Behaviour value: OPEN or SAVE
     */
    public void setBehaviour(Behaviour behaviour) {
        this.behaviour = behaviour;
    }

    /**
     * The behaviour of the FileParam. It can open or save files.
     */
    public enum Behaviour {

        /**
         * Indicates FileParam to show an openDialog.
         */
        OPEN,
        /**
         * Indicates FileParam to show a saveDialog.
         */
        SAVE
    }
}
