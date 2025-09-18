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
 *   18 Sept 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.configuration.selection;

import java.util.Arrays;
import java.util.List;

import org.knime.js.base.dialog.selection.single.SingleSelectionComponentFactory;
import org.knime.js.base.node.configuration.selection.column.ColumnSelectionDialogNodeParameters;
import org.knime.js.base.node.configuration.selection.single.SingleSelectionDialogNodeParameters;
import org.knime.node.parameters.NodeParametersInput;
import org.knime.node.parameters.updates.EffectPredicate;
import org.knime.node.parameters.updates.EffectPredicateProvider;
import org.knime.node.parameters.updates.ParameterReference;
import org.knime.node.parameters.widget.choices.StringChoicesProvider;

/**
 * Utility class to deduplicate titles, descriptions, effects, references, and providers of node parameters including
 * selection (i.e. {@link ColumnSelectionDialogNodeParameters}, and {@link SingleSelectionDialogNodeParameters}).
 *
 * @author Robin Gerling
 */
public final class SelectionNodeParametersUtil {

    private SelectionNodeParametersUtil() {
        // utility
    }

    /**
     * Use as a widget title on a string field with a {@link SelectionTypeValueReference} and a
     * {@link SelectionTypeChoicesProvider}.
     */
    public static final String SELECTION_TYPE_TITLE = "Selection type";

    /**
     * Use as a widget description on a string field with a {@link SelectionTypeValueReference} and a
     * {@link SelectionTypeChoicesProvider}.
     */
    public static final String SELECTION_TYPE_DESCRIPTION = """
            The type of the selection element. This can be either radio buttons with a vertical or horizontal layout,
            a list or a dropdown selection.
            """;

    /**
     * Use as a widget title on a string field with a {@link LimitNumberOfVisibleOptionsValueReference}.
     */
    public static final String LIMIT_VIS_OPT_TITLE = "Limit number of visible options";

    /**
     * Use as a widget description on a string field with a {@link LimitNumberOfVisibleOptionsValueReference}.
     */
    public static final String LIMIT_VIS_OPT_DESCRIPTION = """
            By default the List component adjusts its height to display all possible choices without a scroll bar.
            If the setting is enabled, you will be able to limit the number of visible options in case you have too
            many of them. The setting is available only for List selection type.
            """;

    /**
     * Use as a widget title on a string field with the {@link ShowNumberOfVisibleOptions} effect.
     */
    public static final String NUM_VIS_OPT_TITLE = "Number of visible options";

    /**
     * Use as a widget description on a string field with the {@link ShowNumberOfVisibleOptions} effect.
     */
    public static final String NUM_VIS_OPT_DESCRIPTION = """
            A number of options visible in the List component without a vertical scroll bar. Changing this value will
            also affect the component's height. The setting is available only for List selection type.
            """;

    /**
     * Parameter reference which should be attached to a string field with a {@link SelectionTypeChoicesProvider}
     */
    public static final class SelectionTypeValueReference implements ParameterReference<String> {
    }

    /**
     * Parameter reference which should be attached to a boolean field which is responsible for limiting the number of
     * visible options
     */
    public static final class LimitNumberOfVisibleOptionsValueReference implements ParameterReference<Boolean> {
    }

    /**
     * Effect which determines whether a list is used as single selection component. Must be used with a
     * {@link SelectionTypeValueReference}
     */
    public static final class IsListSelectionType implements EffectPredicateProvider {
        @Override
        public EffectPredicate init(final PredicateInitializer i) {
            return i.getString(SelectionTypeValueReference.class).isEqualTo(SingleSelectionComponentFactory.LIST);
        }
    }

    /**
     * Effect which determines whether to show a number input field for specifying the number of visible options. Must
     * be used with a {@link SelectionTypeValueReference} and a {@link LimitNumberOfVisibleOptionsValueReference}.
     */
    public static final class ShowNumberOfVisibleOptions implements EffectPredicateProvider {
        @Override
        public EffectPredicate init(final PredicateInitializer i) {
            return i.getPredicate(IsListSelectionType.class)
                .and(i.getBoolean(LimitNumberOfVisibleOptionsValueReference.class).isTrue());
        }
    }

    /**
     * Choices provider returning the single selection component which should be attached to a String field with a
     * {@link SelectionTypeValueReference}
     */
    public static final class SelectionTypeChoicesProvider implements StringChoicesProvider {

        @Override
        public List<String> choices(final NodeParametersInput context) {
            return Arrays.asList(SingleSelectionComponentFactory.listSingleSelectionComponents());
        }
    }
}
