/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ---------------------------------------------------------------------
 *
 * Created on 19.03.2013 by Christian Albrecht, KNIME AG, Zurich, Switzerland
 */
package org.knime.quickform.nodes.interactive;

import java.util.ArrayList;
import java.util.Arrays;

import org.knime.core.data.BooleanValue;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.StringValue;

/**
 *
 * @author Christian Albrecht, KNIME AG, Zurich, Switzerland
 */
public class JSONDataTableSpec {

    /**
     *
     * @author Christian Albrecht, KNIME AG, Zurich, Switzerland
     */
    public static enum JSTypes {
        BOOLEAN("boolean"),
        NUMBER("number"),
        STRING("string"),
        UNDEFINED("undefined");

        private String name;

        JSTypes(final String name) {
            this.name = name;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return getName();
        }
    }

    static JSTypes getJSONType(final DataType colType) {
        JSTypes type;
        if (colType.isCompatible(BooleanValue.class)) {
            type = JSTypes.BOOLEAN;
        } else if (colType.isCompatible(DoubleValue.class)) {
            type = JSTypes.NUMBER;
        } else if (colType.isCompatible(StringValue.class)) {
            type = JSTypes.STRING;
        } else {
            type = JSTypes.UNDEFINED;
        }

        return type;
    }

    private int numColumns;
    private int numRows;
    private ArrayList<String> colTypes = new ArrayList<String>();
    private ArrayList<String> colNames = new ArrayList<String>();

    private int numExtensions;
    private ArrayList<String> extensionTypes = new ArrayList<String>();
    private ArrayList<String> extensionNames = new ArrayList<String>();

    /**
     * Empty default constructor for bean initialization.
     */
    public JSONDataTableSpec() {
        // empty creator for bean initialization
    }

    /**
     * @param spec the DataTableSpec for this JSONTable
     * @param nRows the number of rows in the DataTable
     *
     */
    public JSONDataTableSpec(final DataTableSpec spec, final int nRows) {

        setNumColumns(spec.getNumColumns());
        setNumRows(nRows);
        setColNames(spec.getColumnNames());

        String[] types = new String[spec.getNumColumns()];
        String[] kinds = new String[spec.getNumColumns()];
        for (int i = 0; i < spec.getNumColumns(); i++) {
            DataType colType = spec.getColumnSpec(i).getType();
            types[i] = getJSONType(colType).name;
        }
        setColTypes(types);
    }

    /**
     * @return the num_columns
     */
    public int getNumColumns() {
        return numColumns;
    }

    /**
     * @param num the num_columns to set
     */
    public void setNumColumns(final int num) {
        this.numColumns = num;
    }

    /**
     * @return the num_rows
     */
    public int getNumRows() {
        return numRows;
    }

    /**
     * @param num the num_rows to set
     */
    public void setNumRows(final int num) {
        this.numRows = num;
    }

    /**
     * @return the colNames
     */
    public String[] getColNames() {
        return colNames.toArray(new String[0]);
    }

    /**
     * @param names the colNames to set
     */
    public void setColNames(final String[] names) {
        this.colNames = new ArrayList<String>();
        this.colNames.addAll(Arrays.asList(names));
    }

    /**
     * @return the column types
     */
    public String[] getColTypes() {
        return colTypes.toArray(new String[0]);
    }

    /**
     * @param types the types to set
     */
    public void setColTypes(final String[] types) {
        this.colTypes = new ArrayList<String>();
        this.colTypes.addAll(Arrays.asList(types));
    }

    /**
     * @return
     */
    public int getNumExtensions() {
        return numExtensions;
    }

    /**
     * @param num
     */
    public void setNumExtensions(final int num) {
        this.numExtensions = num;
    }

    /**
     * @return
     */
    public String[] getExtensionTypes() {
        return extensionTypes.toArray(new String[0]);
    }

    /**
     * @param types
     */
    public void setExtensionTypes(final String[] types) {
        this.extensionTypes = new ArrayList<String>();
        this.extensionTypes.addAll(Arrays.asList(types));
    }

    /**
     * @return
     */
    public String[] getExtensionNames() {
        return extensionNames.toArray(new String[0]);
    }

    /**
     * @param names
     */
    public void setExtensionNames(final String[] names) {
        this.extensionNames = new ArrayList<String>();
        this.extensionNames.addAll(Arrays.asList(names));
    }

    /**
     * @param extensionName
     * @param dataType
     */
    public void addExtension(final String extensionName, final JSTypes dataType) {
        this.numExtensions++;
        this.extensionNames.add(extensionName);
        this.extensionTypes.add(dataType.name);
    }

}
