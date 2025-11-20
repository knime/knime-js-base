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
 *   28 Oct 2025 (robin): created
 */
package org.knime.js.base.node.parameters.filterandselection;

import java.util.Arrays;
import java.util.List;

import org.knime.core.webui.node.dialog.defaultdialog.internal.widget.PersistWithin;
import org.knime.js.base.dialog.selection.multiple.MultipleSelectionsComponentFactory;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.NodeParametersInput;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.updates.EffectPredicate;
import org.knime.node.parameters.updates.EffectPredicateProvider;
import org.knime.node.parameters.updates.ParameterReference;
import org.knime.node.parameters.updates.ValueReference;
import org.knime.node.parameters.widget.choices.ChoicesProvider;
import org.knime.node.parameters.widget.choices.StringChoicesProvider;

/**
 * The common settings of multiple selection configuration/widgets nodes regarding the type of component and the
 * limitation of visible options in the List/Twinlist frontend component.
 *
 * @author Robin Gerling
 */
@SuppressWarnings("restriction")
public class MultipleSelectionComponentParameters implements NodeParameters {

    /**
     * The config key for the type setting.
     */
    public static final String CFG_TYPE = "type";

    /**
     * The default value for the type setting.
     */
    public static final String DEFAULT_TYPE = MultipleSelectionsComponentFactory.TWINLIST;

    @Widget(title = "Selection type", description = """
            Type of the selection panel. This can be either checkboxes with a vertical or horizontal \
            layout, a list, a combobox, or a twinlist selection.""")
    @ChoicesProvider(SelectionTypeChoicesProvider.class)
    @Persist(configKey = CFG_TYPE)
    @ValueReference(SelectionTypeValueReference.class)
    String m_selectionType = DEFAULT_TYPE;

    @PersistWithin.PersistEmbedded
    LimitVisibleOptionsParameters m_limitVisibleOptions = new LimitVisibleOptionsParameters();

    private static final class SelectionTypeChoicesProvider implements StringChoicesProvider {

        @Override
        public List<String> choices(final NodeParametersInput context) {
            return Arrays.asList(MultipleSelectionsComponentFactory.listMultipleSelectionsComponents());
        }
    }

    static final class SelectionTypeValueReference implements ParameterReference<String> {
    }

    /**
     * Effect predicate provider for testing whether the current selected multiple selection component is the combobox.
     */
    public static final class IsComboboxSelectionType implements EffectPredicateProvider {
        @Override
        public EffectPredicate init(final PredicateInitializer i) {
            return i.getString(SelectionTypeValueReference.class)
                .isEqualTo(MultipleSelectionsComponentFactory.COMBOBOX);
        }
    }

}
