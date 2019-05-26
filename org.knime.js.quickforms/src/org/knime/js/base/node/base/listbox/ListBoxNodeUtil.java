/*
 * ------------------------------------------------------------------------
 *
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
 * History
 *   24 May 2019 (albrecht): created
 */
package org.knime.js.base.node.base.listbox;

import java.util.ArrayList;
import java.util.Arrays;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.InvalidSettingsException;

/**
 * Utility methods for the Date Configuration and Widget nodes
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class ListBoxNodeUtil {

    /**
     * @param variableName Name of the created column
     * @return output spec
     */
    public static DataTableSpec createSpec(final String variableName) {
        final DataColumnSpec cspec = new DataColumnSpecCreator(variableName, StringCell.TYPE).createSpec();
        return new DataTableSpec(cspec);
    }

    /**
     *
     *
     * @param config
     * @param value
     * @return List of separated values
     * @throws InvalidSettingsException
     */
    public static ArrayList<String> getSeparatedValues(final ListBoxNodeConfig config, final String value)
        throws InvalidSettingsException {
        boolean omitEmpty = config.getOmitEmpty();
        /*String value;
        if (optionalValue == null) {
            value = getRelevantValue().getString();
        } else {
            value = optionalValue.getString();
        }*/
        String seperatorString = "";
        config.setSeparatorRegex(getSeparatorRegex(config));
        final String separatorRegexp = config.getSeparatorRegex();
        final ArrayList<String> values = new ArrayList<String>();

        if (config.getSeparateEachCharacter()) {
            if (!(omitEmpty && value.isEmpty())) {
                values.addAll(Arrays.asList(value.split("")));
            }
        } else if (separatorRegexp.isEmpty()) {
            if (!(omitEmpty && value.isEmpty())) {
                values.add(value);
            }
        } else {
            String[] splitValue = value.split(separatorRegexp, -1);
            for (String val : splitValue) {
                if (!(omitEmpty && val.isEmpty())) {
                    values.add(val);
                }
            }
        }
        return values;
    }

    /**
     * @return separator regex
     */
    public static String getSeparatorRegex(final ListBoxNodeConfig config) throws InvalidSettingsException {
        String separator = config.getSeparator();
        if (config.getSeparateEachCharacter() || separator == null || separator.isEmpty()) {
            return "";
        } else {
            StringBuilder sepString = new StringBuilder();
            for (int i = 0; i < separator.length(); i++) {
                if (i > 0) {
                    sepString.append('|');
                }
                char c = separator.charAt(i);
                if (c == '\\') {
                    if (i + 1 < separator.length()) {
                        if (separator.charAt(i + 1) == 'n') {
                            sepString.append("\\n");
                            i++;
                        } else if (separator.charAt(i + 1) == 't') {
                            sepString.append("\\t");
                            i++;
                        } else {
                            // not supported
                            throw new InvalidSettingsException(
                                "A back slash must not be followed by a char other than n or t; ignore the separator.");
                        }
                    } else {
                        sepString.append("\\\\");
                    }
                } else if (c == '[' || c == '^') {
                    // these symbols are not allowed in [] (see the else-block below)
                    sepString.append("\\" + c);
                } else {
                    // a real, non-specific char
                    sepString.append("[" + c + "]");
                }
            }
            return sepString.toString();
        }
    }

}
