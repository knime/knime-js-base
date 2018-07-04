/*
 * ------------------------------------------------------------------------
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
 *  propagated with or for interoperation with KNIME. The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 *
 * History
 *   30.04.2014 (Christian Albrecht, KNIME AG, Zurich, Switzerland): created
 */
package org.knime.js.base.node.viz.generic3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.knime.base.data.xml.SvgCell;
import org.knime.base.util.flowvariable.FlowVariableProvider;
import org.knime.base.util.flowvariable.FlowVariableResolver;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.port.flowvariable.FlowVariablePortObjectSpec;
import org.knime.core.node.port.image.ImagePortObject;
import org.knime.core.node.port.image.ImagePortObjectSpec;
import org.knime.core.node.port.inactive.InactiveBranchPortObjectSpec;
import org.knime.core.node.util.dialog.field.DefaultOutFlowVariableField;
import org.knime.core.node.util.dialog.field.OutFlowVariableField;
import org.knime.core.node.web.ValidationError;
import org.knime.core.node.workflow.FlowVariable.Type;
import org.knime.js.base.node.viz.generic3.GenericJSViewValue.FlowVariableValue;
import org.knime.js.core.JSONDataTable;
import org.knime.js.core.node.AbstractSVGWizardNodeModel;
import org.knime.js.core.node.CSSModifiable;

/**
 *
 * @author Christian Albrecht, KNIME AG, Zurich, Switzerland, University of Konstanz
 */
final class GenericJSViewNodeModel extends AbstractSVGWizardNodeModel<GenericJSViewRepresentation, GenericJSViewValue>
        implements FlowVariableProvider, CSSModifiable {

    private final GenericJSViewConfig m_config;

    /**
     */
    GenericJSViewNodeModel(final String viewName) {
        super(new PortType[]{BufferedDataTable.TYPE_OPTIONAL}, new PortType[]{ImagePortObject.TYPE, FlowVariablePortObject.TYPE}, viewName);
        m_config = new GenericJSViewConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        if (StringUtils.isEmpty(m_config.getJsCode())) {
            throw new InvalidSettingsException("No script defined");
        }
        if (m_config.getGenerateView() && !generateImage()) {
            setWarningMessage("Generate view was set, but no script defined. View generation not possible.");
        }
        PortObjectSpec imageSpec;
        if (generateImage()) {
            imageSpec = new ImagePortObjectSpec(SvgCell.TYPE);
        } else {
            imageSpec = InactiveBranchPortObjectSpec.INSTANCE;
        }
        pushFlowVariables();
        return new PortObjectSpec[]{imageSpec, FlowVariablePortObjectSpec.INSTANCE};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performExecuteCreateView(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        synchronized (getLock()) {
            GenericJSViewRepresentation representation = getViewRepresentation();
            //create JSON table if data available
            if (inObjects[0] != null && representation.getTable() == null) {
              //construct dataset
                BufferedDataTable table = (BufferedDataTable)inObjects[0];
                if (m_config.getMaxRows() < table.size()) {
                    setWarningMessage("Only the first " + m_config.getMaxRows() + " rows are displayed.");
                }
                JSONDataTable jsonTable = JSONDataTable.newBuilder()
                        .setDataTable(table)
                        .setId(getTableId(0))
                        .setFirstRow(1)
                        .setMaxRows(m_config.getMaxRows())
                        .build(exec);
                representation.setTable(jsonTable);
            }

            representation.setJsCode(parseTextAndReplaceVariables());
            representation.setJsSVGCode(m_config.getJsSVGCode());
            representation.setCssCode(m_config.getCssCode());
            setPathsFromLibNames(m_config.getDependencies());
            setOptionalViewWaitTime((long)m_config.getWaitTime());
            setFlowVariablesInView();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] performExecuteCreatePortObjects(final PortObject svgImageFromView, final PortObject[] inObjects,
        final ExecutionContext exec) throws Exception {
        pushFlowVariables();
        return new PortObject[]{svgImageFromView, FlowVariablePortObject.INSTANCE};
    }

    private void pushFlowVariables() {
        for (OutFlowVariableField vF : m_config.getOutVarList()) {
            DefaultOutFlowVariableField variableField = (DefaultOutFlowVariableField)vF;
            FlowVariableValue varViewValue = getVariableFromValue(variableField.getKnimeName(), variableField.getKnimeType());
            switch (variableField.getKnimeType()) {
                case INTEGER:
                    int defaultInt = varViewValue == null ? variableField.getDefaultValueInt() : varViewValue.getIntValue();
                    pushFlowVariableInt(variableField.getKnimeName(), defaultInt);
                    break;
                case DOUBLE:
                    double defaultDouble = varViewValue == null ? variableField.getDefaultValueDouble() : varViewValue.getDoubleValue();
                    pushFlowVariableDouble(variableField.getKnimeName(), defaultDouble);
                    break;
                default:
                    String defaultString = varViewValue == null ? variableField.getDefaultValueString() : varViewValue.getStringValue();
                    pushFlowVariableString(variableField.getKnimeName(), defaultString);
            }
        }
    }

    private FlowVariableValue getVariableFromValue(final String varName, final Type varType) {
        GenericJSViewValue viewValue = getViewValue();
        if (viewValue != null && viewValue.getFlowVariables() != null && viewValue.getFlowVariables().size() > 0) {
            FlowVariableValue varValue = viewValue.getFlowVariables().get(varName);
            if (varValue != null && varValue.getType().equals(varType)) {
                return varValue;
            }
        }
        return null;
    }

    private String parseTextAndReplaceVariables() throws InvalidSettingsException {
        String flowVarCorrectedText = null;
        if (m_config.getJsCode() != null) {
            try {
                flowVarCorrectedText = FlowVariableResolver.parse(m_config.getJsCode(), this);
            } catch (NoSuchElementException nse) {
                throw new InvalidSettingsException(nse.getMessage(), nse);
            }
        }
        return flowVarCorrectedText;
    }

    private static final String ID_WEB_RES = "org.knime.js.core.webResources";

    private static final String ATTR_RES_BUNDLE_ID = "webResourceBundleID";

    private static final String ID_IMPORT_RES = "importResource";

    private static final String ATTR_PATH = "relativePath";

    private static final String ATTR_TYPE = "type";

    private void setPathsFromLibNames(final String[] libNames) {
        ArrayList<String> jsPaths = new ArrayList<String>();
        ArrayList<String> cssPaths = new ArrayList<String>();
        for (String lib : libNames) {
            IConfigurationElement confElement = getConfigurationFromWebResID(lib);
            if (confElement != null) {
                for (IConfigurationElement resElement : confElement.getChildren(ID_IMPORT_RES)) {
                    String path = resElement.getAttribute(ATTR_PATH);
                    String type = resElement.getAttribute(ATTR_TYPE);
                    if (path != null && type != null) {
                        if (type.equalsIgnoreCase("javascript")) {
                            jsPaths.add(path);
                        } else if (type.equalsIgnoreCase("css")) {
                            cssPaths.add(path);
                        }
                    } else {
                        setWarningMessage("Required library " + lib + " is not correctly configured");
                    }
                }
            } else {
                setWarningMessage("Required library is not registered: " + lib);
            }
        }
        GenericJSViewRepresentation representation = getViewRepresentation();
        representation.setJsDependencies(jsPaths.toArray(new String[0]));
        representation.setCssDependencies(cssPaths.toArray(new String[0]));
    }

    private IConfigurationElement getConfigurationFromWebResID(final String id) {
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IConfigurationElement[] configurationElements = registry.getConfigurationElementsFor(ID_WEB_RES);
        for (IConfigurationElement element : configurationElements) {
            if (id.equals(element.getAttribute(ATTR_RES_BUNDLE_ID))) {
                return element;
            }
        }
        return null;
    }

    private void setFlowVariablesInView() {
        Map<String, FlowVariableValue> variableMap = new HashMap<String, FlowVariableValue>();
        for (OutFlowVariableField vF : m_config.getOutVarList()) {
            // check if variable value is already defined
            FlowVariableValue oldValue = getVariableFromValue(vF.getKnimeName(), vF.getKnimeType());
            if (oldValue != null) {
                variableMap.put(vF.getKnimeName(), oldValue);
                continue;
            }

            // create new variable value
            DefaultOutFlowVariableField variableField = (DefaultOutFlowVariableField)vF;
            FlowVariableValue newVar =  new FlowVariableValue();
            newVar.setType(variableField.getKnimeType());
            switch (variableField.getKnimeType()) {
                case INTEGER:
                    newVar.setIntValue(variableField.getDefaultValueInt());
                    break;
                case DOUBLE:
                    newVar.setDoubleValue(variableField.getDefaultValueDouble());
                    break;
                default:
                    newVar.setStringValue(variableField.getDefaultValueString());
            }
            variableMap.put(variableField.getKnimeName(), newVar);
        }
        getViewValue().setFlowVariables(variableMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationError validateViewValue(final GenericJSViewValue viewContent) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GenericJSViewRepresentation createEmptyViewRepresentation() {
        return new GenericJSViewRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GenericJSViewValue createEmptyViewValue() {
        return new GenericJSViewValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "knime_generic_view_v3";
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
        new GenericJSViewConfig().loadSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_config.loadSettings(settings);
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
    public void saveCurrentValue(final NodeSettingsWO content) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void useCurrentValueAsDefault() {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean generateImage() {
        boolean js = StringUtils.isNotBlank(m_config.getJsSVGCode());
        return m_config.getGenerateView() && js;
    }

}
