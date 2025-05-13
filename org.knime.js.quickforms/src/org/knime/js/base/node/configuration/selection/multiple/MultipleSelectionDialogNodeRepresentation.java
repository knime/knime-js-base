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
 *   1 Jun 2019 (albrecht): created
 */
package org.knime.js.base.node.configuration.selection.multiple;

import org.knime.core.node.dialog.DialogNodePanel;
import org.knime.core.node.dialog.SubNodeDescriptionProvider;
import org.knime.core.webui.node.dialog.WebDialogNodeRepresentation.DefaultWebDialogNodeRepresentation;
import org.knime.core.webui.node.dialog.defaultdialog.jsonforms.renderers.LocalizedControlRendererSpec;
import org.knime.core.webui.node.dialog.defaultdialog.jsonforms.renderers.options.Alignment;
import org.knime.js.base.dialog.selection.multiple.MultipleSelectionsComponentFactory;
import org.knime.js.base.node.base.selection.singleMultiple.SingleMultipleSelectionNodeRepresentation;
import org.knime.js.base.node.configuration.renderers.CheckboxesRenderer;
import org.knime.js.base.node.configuration.renderers.ComboboxRenderer;
import org.knime.js.base.node.configuration.renderers.MultiSelectListBoxRenderer;
import org.knime.js.base.node.configuration.renderers.SimpleTwinlistRenderer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The dialog representation of the multiple selection configuration node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class MultipleSelectionDialogNodeRepresentation
    extends SingleMultipleSelectionNodeRepresentation<MultipleSelectionDialogNodeValue>
    implements SubNodeDescriptionProvider<MultipleSelectionDialogNodeValue>,
    DefaultWebDialogNodeRepresentation<MultipleSelectionDialogNodeValue> {

    private static final String WEB_UI_DATA_PATH = "value";

    @JsonCreator
    private MultipleSelectionDialogNodeRepresentation(@JsonProperty("label") final String label, //
        @JsonProperty("description") final String description, //
        @JsonProperty("required") final boolean required, //
        @JsonProperty("defaultValue") final MultipleSelectionDialogNodeValue defaultValue, //
        @JsonProperty("currentValue") final MultipleSelectionDialogNodeValue currentValue, //
        @JsonProperty("possibleChoices") final String[] possibleChoices, //
        @JsonProperty("type") final String type, //
        @JsonProperty("limitNumberVisOptions") final boolean limitNumberVisOptions, //
        @JsonProperty("numberVisOptions") final Integer numberVisOptions) {
        super(label, description, required, defaultValue, currentValue, possibleChoices, type, limitNumberVisOptions,
            numberVisOptions);
    }

    /**
     * @param currentValue The value currently used by the node
     * @param config The config of the node
     */
    public MultipleSelectionDialogNodeRepresentation(final MultipleSelectionDialogNodeValue currentValue,
        final MultipleSelectionDialogNodeConfig config) {
        super(currentValue, config.getDefaultValue(), config.getSelectionConfig(), config.getLabelConfig());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DialogNodePanel<MultipleSelectionDialogNodeValue> createDialogPanel() {
        return new MultipleSelectionConfigurationPanel(this);
    }

    @Override
    public LocalizedControlRendererSpec getWebUIDialogControlSpec() {
        return switch (getType()) {
            case MultipleSelectionsComponentFactory.CHECK_BOXES_HORIZONTAL -> //
                    new CheckboxesRenderer(this, getPossibleChoices(), Alignment.HORIZONTAL).at(WEB_UI_DATA_PATH);
            case MultipleSelectionsComponentFactory.CHECK_BOXES_VERTICAL -> //
                    new CheckboxesRenderer(this, getPossibleChoices(), Alignment.VERTICAL).at(WEB_UI_DATA_PATH);
            case MultipleSelectionsComponentFactory.LIST -> //
                    new MultiSelectListBoxRenderer(this, getPossibleChoices(), getLimitNumberVisOptions(),
                        getNumberVisOptions()).at(WEB_UI_DATA_PATH);
            case MultipleSelectionsComponentFactory.TWINLIST -> //
                    new SimpleTwinlistRenderer(this, getPossibleChoices(), getLimitNumberVisOptions(),
                        getNumberVisOptions()).at(WEB_UI_DATA_PATH);
            case MultipleSelectionsComponentFactory.COMBOBOX -> //
                    new ComboboxRenderer(this, getPossibleChoices()).at(WEB_UI_DATA_PATH);
            default -> throw new IllegalArgumentException(String.format("Unsupported renderer: %s", getType()));
        };
    }

}
