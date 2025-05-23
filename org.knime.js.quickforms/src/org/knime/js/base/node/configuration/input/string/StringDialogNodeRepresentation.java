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
 *   22 May 2019 (albrecht): created
 */
package org.knime.js.base.node.configuration.input.string;

import org.knime.core.node.dialog.DialogNodePanel;
import org.knime.core.node.dialog.SubNodeDescriptionProvider;
import org.knime.core.webui.node.dialog.WebDialogNodeRepresentation.DefaultWebDialogNodeRepresentation;
import org.knime.core.webui.node.dialog.defaultdialog.jsonforms.renderers.ControlRendererSpec;
import org.knime.core.webui.node.dialog.defaultdialog.jsonforms.renderers.DialogElementRendererSpec;
import org.knime.js.base.node.base.input.string.StringNodeConfig;
import org.knime.js.base.node.base.input.string.StringNodeRepresentation;
import org.knime.js.base.node.configuration.renderers.TextAreaRenderer;
import org.knime.js.base.node.configuration.renderers.TextRenderer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The dialog representation of the string configuration node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class StringDialogNodeRepresentation extends StringNodeRepresentation<StringDialogNodeValue> implements
    SubNodeDescriptionProvider<StringDialogNodeValue>, DefaultWebDialogNodeRepresentation<StringDialogNodeValue> {

    @JsonCreator
    private StringDialogNodeRepresentation( //
        @JsonProperty("label") final String label, //
        @JsonProperty("description") final String description, //
        @JsonProperty("required") final boolean required, //
        @JsonProperty("defaultValue") final StringDialogNodeValue defaultValue, //
        @JsonProperty("currentValue") final StringDialogNodeValue currentValue, //
        @JsonProperty("regex") final String regex, //
        @JsonProperty("errorMessage") final String errorMessage, //
        @JsonProperty("editorType") final String editorType, //
        @JsonProperty("multilineEditorWidth") final int multilineEditorWidth, //
        @JsonProperty("multilineEditorHeight") final int multilineEditorHeight //
    ) {
        super(label, description, required, defaultValue, currentValue, regex, errorMessage, editorType,
            multilineEditorWidth, multilineEditorHeight);
    }

    /**
     * @param currentValue The value currently used by the node
     * @param config The config of the node
     */
    public StringDialogNodeRepresentation(final StringDialogNodeValue currentValue,
        final StringInputDialogNodeConfig config) {
        super(currentValue, config.getDefaultValue(), config.getStringConfig(), config.getLabelConfig());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DialogNodePanel<StringDialogNodeValue> createDialogPanel() {
        return new StringConfigurationPanel(this);
    }

    @Override
    public DialogElementRendererSpec getWebUIDialogElementRendererSpec() {
        final var isSingleLine = getEditorType().equals(StringNodeConfig.EDITOR_TYPE_SINGLE_LINE_STRING);
        final ControlRendererSpec textRenderer =
            isSingleLine ? new TextRenderer(this) : new TextAreaRenderer(this, getMultilineEditorHeight());
        return textRenderer.at("string");
    }

}
