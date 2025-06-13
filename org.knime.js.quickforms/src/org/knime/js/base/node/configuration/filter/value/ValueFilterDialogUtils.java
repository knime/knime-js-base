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
 *   Jun 13, 2025 (Paul Bärnreuther): created
 */
package org.knime.js.base.node.configuration.filter.value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Utility for deduplication between modern and legacy implementation of the value filter configuration.
 *
 * @author Paul Bärnreuther
 */
final class ValueFilterDialogUtils {

    private ValueFilterDialogUtils() {
        // Utility class.
    }

    static Optional<List<String>> getPossibleValuedForCol(final String selectedCol,
        final Map<String, List<String>> possibleValues) {
        if (selectedCol == null) {
            return Optional.empty();
        }
        List<String> possibleValuesForCol = possibleValues.get(selectedCol);
        if (possibleValuesForCol == null) {
            return Optional.empty();
        }
        return Optional.of(possibleValuesForCol);
    }

    /**
     * This is the custom logic that needs to be executed when saving the value
     *
     * @param value the value filter dialog node value to be updated
     * @param selectedCol the possibly missing or null selected column
     * @param selectedValues the selected values for the column
     * @param possibleValuesForCol the possible values for the available column
     */
    static void setIncludesAndExcludes(final ValueFilterDialogNodeValue value, final String[] selectedValues,
        final List<String> possibleValuesForCol) {

        // 'choices' in the context of a MultipleSelectionComponent are all values that can be selected (i.e.
        // that are shown in the UI) and coincide with the possible values for the column.
        // Excludes are all values that could have been selected but were not.
        List<String> excludes = new ArrayList<>();
        Set<String> selectionSet = new HashSet<>(Arrays.asList(selectedValues));
        for (String choice : possibleValuesForCol) {
            if (!selectionSet.contains(choice)) {
                excludes.add(choice);
            }
        }
        value.setValues(selectedValues);
        value.setExcludes(excludes.toArray(String[]::new));
    }

}
