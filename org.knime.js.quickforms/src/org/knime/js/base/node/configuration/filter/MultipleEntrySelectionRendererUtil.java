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
 *   8 May 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.configuration.filter;

import java.util.List;

import org.knime.core.node.dialog.SubNodeDescriptionProvider;
import org.knime.core.webui.node.dialog.defaultdialog.jsonforms.renderers.ControlRendererSpec;
import org.knime.core.webui.node.dialog.defaultdialog.jsonforms.renderers.options.Alignment;
import org.knime.core.webui.node.dialog.defaultdialog.widget.choices.StringChoice;
import org.knime.core.webui.node.dialog.defaultdialog.widget.updates.StateProvider;
import org.knime.js.base.dialog.selection.multiple.MultipleSelectionsComponentFactory;
import org.knime.js.base.node.configuration.renderers.CheckboxesRenderer;
import org.knime.js.base.node.configuration.renderers.ComboboxRenderer;
import org.knime.js.base.node.configuration.renderers.ManualFilterRenderer;
import org.knime.js.base.node.configuration.renderers.MultiSelectListBoxRenderer;
import org.knime.js.base.node.configuration.renderers.ProvidedChoicesCheckboxesRenderer;
import org.knime.js.base.node.configuration.renderers.ProvidedChoicesComboboxRenderer;
import org.knime.js.base.node.configuration.renderers.ProvidedChoicesManualFilterRenderer;
import org.knime.js.base.node.configuration.renderers.ProvidedChoicesMultiSelectListBoxRenderer;
import org.knime.js.base.node.configuration.renderers.ProvidedChoicesSimpleTwinlistRenderer;
import org.knime.js.base.node.configuration.renderers.SimpleTwinlistRenderer;

/**
 * Utility method to combine common logic of single selection configuration nodes.
 *
 * @author Robin Gerling
 */
public final class MultipleEntrySelectionRendererUtil {

    private MultipleEntrySelectionRendererUtil() {
    }

    /**
     * Retrieve the single selection renderer spec by the given type
     *
     * @param nodeRep the node representation
     * @param type the component type to render
     * @param possibleValues the possible values to choose from
     * @param hasSizeLimit whether the component should limit its size
     * @param sizeLimit the size limit of possible values to display simultaneously
     * @param supportUnknownValues whether the component should support unknown values if the selected type allows it
     * @return the renderer spec based on the single selection component type
     */
    public static ControlRendererSpec getWebUIDialogControlSpecByType(
        final SubNodeDescriptionProvider<?> nodeRep, final String type, final String[] possibleValues,
        final boolean hasSizeLimit, final int sizeLimit, final boolean supportUnknownValues) {
        return switch (type) {
            case MultipleSelectionsComponentFactory.CHECK_BOXES_HORIZONTAL -> //
                    new CheckboxesRenderer(nodeRep, possibleValues, Alignment.HORIZONTAL);
            case MultipleSelectionsComponentFactory.CHECK_BOXES_VERTICAL -> //
                    new CheckboxesRenderer(nodeRep, possibleValues, Alignment.VERTICAL);
            case MultipleSelectionsComponentFactory.LIST -> //
                    new MultiSelectListBoxRenderer(nodeRep, possibleValues, hasSizeLimit, sizeLimit);
            case MultipleSelectionsComponentFactory.TWINLIST -> //
                    supportUnknownValues //
                        ? new ManualFilterRenderer(nodeRep, possibleValues, hasSizeLimit, sizeLimit)
                        : new SimpleTwinlistRenderer(nodeRep, possibleValues, hasSizeLimit, sizeLimit);
            case MultipleSelectionsComponentFactory.COMBOBOX -> //
                    new ComboboxRenderer(nodeRep, possibleValues);
            default -> throw new IllegalArgumentException(String.format("Unsupported renderer: %s", type));
        };
    }

    /**
     * Similar to {@link #getWebUIDialogControlSpecByType} but the name does not come from a node representation and the
     * possible values are provided by a state provider.
     *
     * @param name the name of the dialog element, e.g. the label above the control
     * @param type the component type to render
     * @param possibleValuesProvider the state provider for the possible values to choose from
     * @param limitNumberVisOptions whether the component should limit its size
     * @param numberVisOptions the size limit of possible values to display simultaneously
     * @param supportUnknownValues whether the component should support unknown values if the selected type allows it
     * @return the renderer spec based on the single selection component type
     */
    public static ControlRendererSpec getWebUIDialogControlSpecByType(final String name, final String type,
        final StateProvider<List<StringChoice>> possibleValuesProvider, final boolean limitNumberVisOptions,
        final Integer numberVisOptions, final boolean supportUnknownValues) {

        return switch (type) {
            case MultipleSelectionsComponentFactory.CHECK_BOXES_HORIZONTAL -> //
                    new ProvidedChoicesCheckboxesRenderer(name, possibleValuesProvider, Alignment.HORIZONTAL);
            case MultipleSelectionsComponentFactory.CHECK_BOXES_VERTICAL -> //
                    new ProvidedChoicesCheckboxesRenderer(name, possibleValuesProvider, Alignment.VERTICAL);
            case MultipleSelectionsComponentFactory.LIST -> //
                    new ProvidedChoicesMultiSelectListBoxRenderer(name, possibleValuesProvider, limitNumberVisOptions,
                        numberVisOptions);
            case MultipleSelectionsComponentFactory.TWINLIST -> //
                    supportUnknownValues //
                        ? new ProvidedChoicesManualFilterRenderer(name, possibleValuesProvider, limitNumberVisOptions,
                            numberVisOptions)
                        : new ProvidedChoicesSimpleTwinlistRenderer(name, possibleValuesProvider, limitNumberVisOptions,
                            numberVisOptions);
            case MultipleSelectionsComponentFactory.COMBOBOX -> //
                    new ProvidedChoicesComboboxRenderer(name, possibleValuesProvider);
            default -> throw new IllegalArgumentException(String.format("Unsupported renderer: %s", type));
        };
    }
}
