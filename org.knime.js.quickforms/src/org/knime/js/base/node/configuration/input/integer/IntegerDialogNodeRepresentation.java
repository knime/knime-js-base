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
package org.knime.js.base.node.configuration.input.integer;

import org.knime.core.node.dialog.DialogNodePanel;
import org.knime.core.node.dialog.SubNodeDescriptionProvider;
import org.knime.core.webui.node.dialog.WebDialogNodeRepresentation.DefaultWebDialogNodeRepresentation;
import org.knime.core.webui.node.dialog.defaultdialog.jsonforms.renderers.DialogElementRendererSpec;
import org.knime.js.base.node.base.input.integer.IntegerNodeRepresentation;
import org.knime.js.base.node.configuration.renderers.IntegerRenderer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The dialog representation of the integer configuration node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class IntegerDialogNodeRepresentation extends IntegerNodeRepresentation<IntegerDialogNodeValue> implements
    SubNodeDescriptionProvider<IntegerDialogNodeValue>, DefaultWebDialogNodeRepresentation<IntegerDialogNodeValue> {

    @JsonCreator
    private IntegerDialogNodeRepresentation( //
        @JsonProperty("label") final String label, //
        @JsonProperty("description") final String description, //
        @JsonProperty("required") final boolean required, //
        @JsonProperty("defaultValue") final IntegerDialogNodeValue defaultValue, //
        @JsonProperty("currentValue") final IntegerDialogNodeValue currentValue, //
        @JsonProperty("usemin") final boolean useMin, //
        @JsonProperty("usemax") final boolean useMax, //
        @JsonProperty("min") final int min, //
        @JsonProperty("max") final int max //
    ) {
        super(label, description, required, defaultValue, currentValue, useMin, useMax, min, max);
    }

    /**
     * @param currentValue The value currently used by the node
     * @param config The config of the node
     */
    public IntegerDialogNodeRepresentation(final IntegerDialogNodeValue currentValue,
        final IntegerInputDialogNodeConfig config) {
        super(currentValue, config.getDefaultValue(), config.getIntegerConfig(), config.getLabelConfig());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DialogNodePanel<IntegerDialogNodeValue> createDialogPanel() {
        return new IntegerConfigurationPanel(this);
    }

    @Override
    public DialogElementRendererSpec getWebUIDialogElementRendererSpec() {
        return new IntegerRenderer(this, isUseMin(), getMin(), isUseMax(), getMax()).at("integer");
    }

}
