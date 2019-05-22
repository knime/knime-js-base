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
 *   9 May 2019 (albrecht): created
 */
package org.knime.js.base.node.widget;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortType;
import org.knime.core.node.web.ValidationError;
import org.knime.core.node.wizard.CSSModifiable;
import org.knime.js.base.node.base.ValueControlledNodeUtil;
import org.knime.js.base.node.quickform.ValueOverwriteMode;
import org.knime.js.core.JSONViewContent;
import org.knime.js.core.node.AbstractWizardNodeModel;

/**
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 * @param <REP>
 * @param <VAL>
 * @param <CONF>
 */
public abstract class WidgetNodeModel<REP extends JSONViewContent, VAL extends JSONViewContent,
    CONF extends WidgetConfig<VAL>> extends AbstractWizardNodeModel<REP, VAL> implements CSSModifiable {

    private CONF m_config = createEmptyConfig();
    private boolean m_valueSet = false;

    /**
     * @param inPortTypes
     * @param outPortTypes
     * @param viewName
     */
    protected WidgetNodeModel(final PortType[] inPortTypes, final PortType[] outPortTypes, final String viewName) {
        super(inPortTypes, outPortTypes, viewName);
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
     * @return Empty instance of the config
     */
    public abstract CONF createEmptyConfig();

    /**
     * @return the config of this node
     */
    protected CONF getConfig() {
        return m_config;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHideInWizard() {
        return m_config.isHideInWizard();
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
    protected void performReset() {
        m_valueSet = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public REP createEmptyViewRepresentation() {
        // ignore view representation is created from config and current value on every get
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public REP getViewRepresentation() {
        synchronized (getLock()) {
            return getRepresentation();
        }
    }

    /**
     * @return The representation of this node.
     */
    protected abstract REP getRepresentation();

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationError validateViewValue(final VAL viewContent) {
        /* validates by default */
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setViewValue(final VAL value) {
        synchronized (getLock()) {
            super.setViewValue(value);
            m_valueSet = value != null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadViewValue(final VAL viewValue, final boolean useAsDefault) {
        synchronized (getLock()) {
            super.loadViewValue(viewValue, useAsDefault);
            m_valueSet = viewValue != null;
        }
    }

    /**
     * @return The value with the highest priority which is valid.
     */
    protected VAL getRelevantValue() {
        synchronized (getLock()) {
            switch (getOverwriteMode()) {
                case WIZARD:
                    return getViewValue();
                default:
                    return m_config.getDefaultValue();
            }
        }
    }

    /**
     * @return The mode in which the value is overwritten
     */
    protected ValueOverwriteMode getOverwriteMode() {
        synchronized (getLock()) {
            if (m_valueSet) {
                return ValueOverwriteMode.WIZARD;
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
    protected void loadInternals(final File nodeInternDir, final ExecutionMonitor exec) throws IOException,
        CanceledExecutionException {
        // only load value, representation is always created from config
        File valFile = new File(nodeInternDir, "widgetValue.xml");
        try (final FileInputStream fis = new FileInputStream(valFile)) {
            NodeSettingsRO valSettings = NodeSettings.loadFromXML(fis);
            VAL value = createEmptyViewValue();
            try {
                value.loadFromNodeSettings(valSettings);
            } catch (InvalidSettingsException e) {
                value = null;
            }
            setViewValue(value);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File nodeInternDir, final ExecutionMonitor exec) throws IOException,
        CanceledExecutionException {
        // only save value, representation is not needing to be saved
        NodeSettings valSettings = new NodeSettings("widgetValue");
        VAL value = getViewValue();
        if (value != null) {
            value.saveToNodeSettings(valSettings);
        }
        File valFile = new File(nodeInternDir, "widgetValue.xml");
        try (final FileOutputStream fos = new FileOutputStream(valFile)) {
            valSettings.saveToXML(fos);
        }
    }

}
