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
package org.knime.js.base.node.widget.input.integer;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.web.ValidationError;
import org.knime.js.base.node.base.input.integer.IntegerNodeConfig;
import org.knime.js.base.node.base.input.integer.IntegerNodeRepresentation;
import org.knime.js.base.node.base.input.integer.IntegerNodeValue;
import org.knime.js.base.node.widget.WidgetFlowVariableNodeModel;

/**
 * The node model for the integer widget node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class IntegerWidgetNodeModel extends
    WidgetFlowVariableNodeModel<IntegerNodeRepresentation<IntegerNodeValue>, IntegerNodeValue, IntegerInputWidgetConfig> {

    /**
     * @param viewName the interactive view name
     */
    public IntegerWidgetNodeModel(final String viewName) {
        super(viewName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntegerNodeValue createEmptyViewValue() {
        return new IntegerNodeValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org.knime.js.base.node.widget.input.integer";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createAndPushFlowVariable() throws InvalidSettingsException {
        ValidationError error = validateViewValue(getRelevantValue());
        if (error != null) {
            throw new InvalidSettingsException(error.getError());
        }
        int value = getRelevantValue().getInteger();
        pushFlowVariableInt(getConfig().getFlowVariableName(), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntegerInputWidgetConfig createEmptyConfig() {
        return new IntegerInputWidgetConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IntegerNodeRepresentation<IntegerNodeValue> getRepresentation() {
        IntegerInputWidgetConfig config = getConfig();
        return new IntegerNodeRepresentation<IntegerNodeValue>(getRelevantValue(), config.getDefaultValue(),
            config.getIntegerConfig(), config.getLabelConfig());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void useCurrentValueAsDefault() {
        getConfig().getDefaultValue().setInteger(getViewValue().getInteger());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationError validateViewValue(final IntegerNodeValue value) {
        IntegerNodeConfig config = getConfig().getIntegerConfig();
        int integer = value.getInteger();
        if (config.isUseMin() && integer < config.getMin()) {
            return new ValidationError("The set integer " + integer
                + " is smaller than the allowed minimum of " + config.getMin());
        }
        if (config.isUseMax() && integer > config.getMax()) {
            return new ValidationError("The set integer " + integer
                + " is bigger than the allowed maximum of " + config.getMax());
        }
        return super.validateViewValue(value);
    }

}
