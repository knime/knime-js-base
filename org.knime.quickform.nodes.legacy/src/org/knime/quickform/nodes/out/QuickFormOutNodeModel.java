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
 *
 */
package org.knime.quickform.nodes.out;

import java.io.File;
import java.io.IOException;

import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.quickform.out.QuickFormOutputNode;
import org.knime.core.util.node.quickform.out.AbstractQuickFormOutElement;

/**
 * Abstract super class of all nodes that represent output element forms such as
 * file downloaders or string output.
 *
 * @param <CFG> The configuration type exchanged between dialog and model.
 * @author Bernd Wiswedel, KNIME AG, Zurich, Switzerland
 */
public abstract class QuickFormOutNodeModel
    <CFG extends QuickFormOutConfiguration>
extends NodeModel implements QuickFormOutputNode {

    private CFG m_configuration;

    /**
     * Delegates to super constructor. Subclasses will need to overwrite
     * configure and execute when they use this constructor.
     *
     * @param nrInDataPorts Input count.
     * @param nrOutDataPorts Output count.
     */
    protected QuickFormOutNodeModel(final int nrInDataPorts,
            final int nrOutDataPorts) {
        super(nrInDataPorts, nrOutDataPorts);
    }

    /**
     * Delegates to super constructor. Subclasses will need to overwrite
     * configure and execute when they use this constructor.
     *
     * @param inPortTypes Input port type array.
     * @param outPortTypes Output port type array.
     */
    protected QuickFormOutNodeModel(final PortType[] inPortTypes,
            final PortType[] outPortTypes) {
        super(inPortTypes, outPortTypes);
    }

    /** Default constructor, one flow variable input port, no output. */
    protected QuickFormOutNodeModel() {
        super(new PortType[]{FlowVariablePortObject.TYPE}, new PortType[0]);
    }

    /** {@inheritDoc} */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        if (m_configuration != null) {
            m_configuration.saveSettingsTo(settings);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        CFG c = createConfiguration();
        c.loadSettingsInModel(settings);
    }

    /** {@inheritDoc} */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        CFG c = createConfiguration();
        c.loadSettingsInModel(settings);
        m_configuration = c;
    }


    /** {@inheritDoc} */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs)
            throws InvalidSettingsException {
        peekFlowVariable();
        return new PortObjectSpec[]{};
    }

    /** {@inheritDoc} */
    @Override
    protected PortObject[] execute(final PortObject[] inObjects,
            final ExecutionContext exec) throws Exception {
        peekFlowVariable();
        return new PortObject[]{};
    }

    /** {@inheritDoc} */
    @Override
    public abstract AbstractQuickFormOutElement getQuickFormElement();

    /** Get handle on current (filled) configuration or null.
     * @return The config to use.
     */
    protected final CFG getConfiguration() {
        return m_configuration;
    }

    /** Create empty instance of configuration object.
     * @return A new empty config.
     */
    protected abstract CFG createConfiguration();

    /** Subclasses will read their flow variables here. Called from
     * configure and execute.
     * @throws InvalidSettingsException If settings are invalid.
     */
    protected abstract void peekFlowVariable() throws InvalidSettingsException;

    /** {@inheritDoc} */
    @Override
    protected void reset() {
        // no op
    }

    /** {@inheritDoc} */
    @Override
    protected void loadInternals(final File nodeInternDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // no op
    }

    /** {@inheritDoc} */
    @Override
    protected void saveInternals(final File nodeInternDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // no op
    }

    /** {@inheritDoc} */
    @Override
    public boolean hideInWizard() {
        if (m_configuration == null) {
            return false;
        }
        return m_configuration.isHideInWizard();
    }
}
