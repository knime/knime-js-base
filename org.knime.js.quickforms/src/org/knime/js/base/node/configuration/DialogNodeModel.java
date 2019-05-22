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
 *   3 May 2019 (albrecht): created
 */
package org.knime.js.base.node.configuration;

import java.io.File;
import java.io.IOException;

import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.dialog.DialogNode;
import org.knime.core.node.dialog.DialogNodeRepresentation;
import org.knime.core.node.dialog.DialogNodeValue;
import org.knime.core.node.port.PortType;
import org.knime.js.base.node.base.ValueControlledNodeUtil;
import org.knime.js.base.node.quickform.ValueOverwriteMode;

/**
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public abstract class DialogNodeModel<REP extends DialogNodeRepresentation<VAL>,
    VAL extends DialogNodeValue, CONF extends DialogNodeConfig<VAL>>
    extends NodeModel implements DialogNode<REP, VAL> {

    private final Object m_lock = new Object();
    private CONF m_config = createEmptyConfig();
    private VAL m_dialogValue = null;

    /**
     * @param inPortTypes
     * @param outPortTypes
     */
    protected DialogNodeModel(final PortType[] inPortTypes, final PortType[] outPortTypes) {
        super(inPortTypes, outPortTypes);
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
        createEmptyConfig().loadSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_config.loadSettings(settings);
    }

    /**
     * @return Empty instance of the config.
     */
    public abstract CONF createEmptyConfig();

    /**
     * @return The representation of this node.
     */
    protected abstract REP getRepresentation();

    /**
     * @return The config of this node.
     */
    protected CONF getConfig() {
        return m_config;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public REP getDialogRepresentation() {
        return getRepresentation();
    }

    /** {@inheritDoc} */
    @Override
    public String getParameterName() {
        return m_config.getParameterName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDialogValue(final VAL value) {
        synchronized (m_lock) {
            m_dialogValue = value;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VAL getDefaultValue() {
        synchronized (m_lock) {
            return m_config.getDefaultValue();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VAL getDialogValue() {
        synchronized (m_lock) {
            return m_dialogValue;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateDialogValue(final VAL value) throws InvalidSettingsException {
        /* validates by default */
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHideInDialog() {
        return m_config.getHideInDialog();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHideInDialog(final boolean hide) {
        m_config.setHideInDialog(hide);
    }

    /**
     * Returns the value that should currently be used.
     *
     * The priority of values is as follows:
     * <ol>
     * <li>Dialog value</li>
     * <li>Default value of config</li>
     * </ol>
     *
     * @return The value with the highest priority which is valid.
     */
    protected VAL getRelevantValue() {
        synchronized (m_lock) {
            switch (getOverwriteMode()) {
                case DIALOG:
                    return m_dialogValue;
                default:
                    return m_config.getDefaultValue();
            }
        }
    }

    /**
     * @return The mode in which the value is overwritten
     */
    protected ValueOverwriteMode getOverwriteMode() {
        synchronized (m_lock) {
            if (m_dialogValue != null) {
                return ValueOverwriteMode.DIALOG;
            } else {
                return ValueOverwriteMode.NONE;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveCurrentValue(final NodeSettingsWO content) {
        content.addString(ValueControlledNodeUtil.CFG_OVERWRITE_MODE, getOverwriteMode().name());
        NodeSettingsWO settings = content.addNodeSettings(ValueControlledNodeUtil.CFG_CURRENT_VALUE);
        getRelevantValue().saveToNodeSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File nodeInternDir, final ExecutionMonitor exec)
        throws IOException, CanceledExecutionException {
        /* nothing to do by default */
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File nodeInternDir, final ExecutionMonitor exec)
        throws IOException, CanceledExecutionException {
        /* nothing to do by default */
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        /* nothing to do by default */
    }

}
