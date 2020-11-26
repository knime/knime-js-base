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
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 */
package org.knime.ext.js.node.widget.input.molecule;

import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.port.flowvariable.FlowVariablePortObjectSpec;
import org.knime.js.base.node.widget.WidgetFlowVariableNodeModel;
import org.knime.js.core.layout.LayoutTemplateProvider;
import org.knime.js.core.layout.bs.JSONLayoutViewContent;
import org.knime.js.core.layout.bs.JSONLayoutViewContent.ResizeMethod;

/**
 * The model for the molecule string input quick form node.
 *
 * @author Daniel Bogenrieder, KNIME AG, Zurich, Switzerland
 */
public class MoleculeWidgetNodeModel
        extends WidgetFlowVariableNodeModel
        <MoleculeWidgetRepresentation,
        MoleculeWidgetValue,
        MoleculeWidgetConfig> implements LayoutTemplateProvider {

    private static NodeLogger LOGGER = NodeLogger.getLogger(MoleculeWidgetNodeModel.class);

    /**
     * The default formats shown in the molecule widget.
     */
    static final String[] DEFAULT_FORMATS = {"SDF", "SMILES", "MOL", "SMARTS", "RXN", "HELM"};
    static final String[] DEFAULT_FORMATS_WITHOUT_LINES = {"SDF", "MOL", "RXN"};

    /** Creates a new node model with no inports and a flow variable outport. */
    protected MoleculeWidgetNodeModel(final String viewName) {
        super(new PortType[0], new PortType[]{FlowVariablePortObject.TYPE}, viewName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MoleculeWidgetValue createEmptyViewValue() {
        return new MoleculeWidgetValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org.knime.ext.js.node.widget.input.molecule";
    }

    /** {@inheritDoc} */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        createAndPushFlowVariable();
        return new PortObjectSpec[]{FlowVariablePortObjectSpec.INSTANCE};
    }

    /** {@inheritDoc} */
    @Override
    protected PortObject[] performExecute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        createAndPushFlowVariable();
        return new PortObject[]{FlowVariablePortObject.INSTANCE};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createAndPushFlowVariable() throws InvalidSettingsException {
        String string = getRelevantValue().getMoleculeString();
        if (string == null) {
            string = "";
        }
        pushFlowVariableString(getConfig().getFlowVariableName(), string);
        pushFlowVariableString("molecule_format", getViewRepresentation().getFormat());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MoleculeWidgetConfig createEmptyConfig() {
        return new MoleculeWidgetConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected MoleculeWidgetRepresentation<MoleculeWidgetValue> getRepresentation() {
        MoleculeWidgetConfig config = getConfig();
        return new MoleculeWidgetRepresentation<MoleculeWidgetValue>(getRelevantValue(),
                config.getDefaultValue(),
                config,
                config.getLabelConfig());
    }

    @Override
    public JSONLayoutViewContent getLayoutTemplate() {
         JSONLayoutViewContent view = new JSONLayoutViewContent();
         view.setResizeMethod(ResizeMethod.ASPECT_RATIO_4by3);
         return view;
    }

    @Override
    protected void useCurrentValueAsDefault() {
        // TODO Auto-generated method stub
        
    }

}
