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
 */
package org.knime.js.base.node.widget.reexecution.refresh;

import java.util.NoSuchElementException;

import org.knime.base.util.flowvariable.FlowVariableProvider;
import org.knime.base.util.flowvariable.FlowVariableResolver;
import org.knime.core.node.InvalidSettingsException;
import org.knime.js.base.node.widget.WidgetFlowVariableNodeModel;

/**
 * The node model for the refresh button node.
 *
 * @author Ben Laney, KNIME GmbH, Konstanz, Germany
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class RefreshButtonWidgetNodeModel extends
    WidgetFlowVariableNodeModel<RefreshButtonWidgetViewRepresentation<RefreshButtonWidgetViewValue>,
    RefreshButtonWidgetViewValue, RefreshButtonWidgetNodeConfig> implements FlowVariableProvider {

    /**
     * Creates a new refresh button widget node model.
     * @param viewName the view name
     */
    public RefreshButtonWidgetNodeModel(final String viewName) {
        super(viewName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createAndPushFlowVariable() throws InvalidSettingsException {
        var value = getRelevantValue();
        int refreshCounter = value.getRefreshCounter();
        String refreshTimestamp = value.getRefreshTimestamp();
        if (refreshTimestamp == null) {
            refreshTimestamp = "";
        }
        String flowVariableName = getConfig().getFlowVariableName();
        String counterVariable = flowVariableName + "-counter";
        String timestampVariable = flowVariableName + "-timestamp";
        pushFlowVariableInt(counterVariable, refreshCounter);
        pushFlowVariableString(timestampVariable, refreshTimestamp);

        // legacy flow variable exposes variable escaped button text as well
        String flowVarCorrectedText = flowVariableEscapeButtonText();
        pushFlowVariableString(RefreshButtonWidgetNodeConfig.FLOW_VARIABLE_NAME, flowVarCorrectedText);
    }

    /**
     * @return
     * @throws InvalidSettingsException
     */
    private String flowVariableEscapeButtonText() throws InvalidSettingsException {
        String flowVarCorrectedText;
        try {
            // replaces $${S ‘<variable name here>’}$$ with flow variable value for in-line replacement
            flowVarCorrectedText = FlowVariableResolver.parse(getConfig().getButtonText(), this);
        } catch (NoSuchElementException nse) {
            throw new InvalidSettingsException(nse.getMessage(), nse);
        }
        return flowVarCorrectedText;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RefreshButtonWidgetNodeConfig createEmptyConfig() {
        return new RefreshButtonWidgetNodeConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected RefreshButtonWidgetViewRepresentation<RefreshButtonWidgetViewValue> getRepresentation() {
        var config = getConfig();
        String buttonText = config.getButtonText();
        try {
            buttonText = flowVariableEscapeButtonText();
        } catch (InvalidSettingsException e) { /* handled in createAndPushFlowVariable */ }

        return new RefreshButtonWidgetViewRepresentation<RefreshButtonWidgetViewValue>(getRelevantValue(),
            config.getDefaultValue(), config.getLabelConfig(), config.getTriggerReExecution(), buttonText);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RefreshButtonWidgetViewValue createEmptyViewValue() {
        return new RefreshButtonWidgetViewValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org.knime.js.base.node.widget.reexecution.refresh";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void useCurrentValueAsDefault() {
        // do nothing
    }
}
