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
 *   Sep 17, 2020 (Christian Albrecht, KNIME GmbH, Konstanz, Germany): created
 */
package org.knime.js.base.node.widget.output.text;

import static org.knime.core.node.workflow.NodeContext.getContext;

import java.util.NoSuchElementException;
import java.util.Set;

import org.knime.base.util.flowvariable.FlowVariableProvider;
import org.knime.base.util.flowvariable.FlowVariableResolver;
import org.knime.base.util.flowvariable.FlowVariableResolver.FlowVariableEscaper;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.web.ValidationError;
import org.knime.core.node.wizard.CSSModifiable;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.js.base.node.widget.output.text.TextOutputWidgetConfig.OutputTextFormat;
import org.knime.js.core.JSCorePlugin;
import org.knime.js.core.StringSanitizationSerializer;
import org.knime.js.core.node.AbstractWizardNodeModel;

/**
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class TextOutputWidgetNodeModel extends AbstractWizardNodeModel<TextOutputWidgetRepresentation, TextOutputWidgetValue>
        implements FlowVariableProvider, CSSModifiable {

    private static final boolean SHOULD_SANITIZE_GLOBAL =
            Boolean.parseBoolean(System.getProperty(JSCorePlugin.SYS_PROPERTY_SANITIZE_CLIENT_HTML)) &&
            Boolean.parseBoolean(System.getProperty(JSCorePlugin.SYS_PROPERTY_SANITIZE_GENERIC_JS_VIEW));

    private TextOutputWidgetConfig m_config = new TextOutputWidgetConfig();
    private boolean m_isTextOverwrittenByFlowVariable;
    private StringSanitizationSerializer m_stringSanitizer;

    /**
     * Creates a new file download node model.
     * @param viewName the view name
     */
    public TextOutputWidgetNodeModel(final String viewName) {
        super(new PortType[]{FlowVariablePortObject.TYPE_OPTIONAL}, new PortType[0], viewName);
        m_stringSanitizer = SHOULD_SANITIZE_GLOBAL ? new StringSanitizationSerializer() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        return new PortObjectSpec[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] performExecute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        synchronized (getLock()) {
            TextOutputWidgetRepresentation representation = getViewRepresentation();
            if (representation == null) {
                representation = createEmptyViewRepresentation();
            }
            representation.setLabel(m_config.getLabel());
            representation.setDescription(m_config.getDescription());
            representation.setTextFormat(m_config.getTextFormat().toString());
            var sanitize = m_stringSanitizer != null;
            String flowVarCorrectedText = m_config.getText();
            if (sanitize && m_config.getTextFormat() == OutputTextFormat.Html && m_isTextOverwrittenByFlowVariable) {
                flowVarCorrectedText = m_stringSanitizer.sanitize(m_config.getText());
            }
            try {
                flowVarCorrectedText =
                    FlowVariableResolver.parse(flowVarCorrectedText, this, new FlowVariableEscaper() {

                        @Override
                        public String readString(final FlowVariableProvider model, final String varName) {
                            String flowVarString = super.readString(model, varName);
                            if (sanitize) {
                                flowVarString = m_stringSanitizer.sanitize(flowVarString);
                            }
                            return flowVarString;
                        }
                    });
            } catch (NoSuchElementException nse) {
                throw new InvalidSettingsException(nse.getMessage(), nse);
            }
            representation.setText(flowVarCorrectedText);
        }
        return new PortObject[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationError validateViewValue(final TextOutputWidgetValue viewContent) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TextOutputWidgetRepresentation createEmptyViewRepresentation() {
        return new TextOutputWidgetRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TextOutputWidgetValue createEmptyViewValue() {
        return new TextOutputWidgetValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org.knime.js.base.node.widget.output.text";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHideInWizard() {
        return m_config.getHideInWizard();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHideInWizard(final boolean hide) {
        m_config.setHideInWizard(hide);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCssStyles() {
        return m_config.getCustomCSS();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCssStyles(final String styles) {
        m_config.setCustomCSS(styles);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        m_config.saveSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        new TextOutputWidgetConfig().loadSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_config.loadSettings(settings);
        setTextOverwrittenByFlowVariable();
        var shouldSanitize = SHOULD_SANITIZE_GLOBAL || m_config.isSanitizeInput();
        m_stringSanitizer = shouldSanitize ? new StringSanitizationSerializer() : null;

    }

    private void setTextOverwrittenByFlowVariable() throws InvalidSettingsException {
        m_isTextOverwrittenByFlowVariable = false;
        var context = getContext();
        if (context != null && context.getNodeContainer() instanceof NativeNodeContainer nodeContainer) {
            Set<String> overwritten = nodeContainer.getVariableControlledModelSettings();
            if (overwritten.contains(TextOutputWidgetConfig.CFG_TEXT)) {
                m_isTextOverwrittenByFlowVariable = true;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performReset() {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveCurrentValue(final NodeSettingsWO content) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void useCurrentValueAsDefault() {
        // do nothing
    }

}
