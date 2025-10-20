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

import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;
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

    private final LazyInitializer<CONF> m_configInitializer = new LazyInitializer<CONF>() {

        @Override
        protected CONF initialize() throws ConcurrentException {
            return createEmptyConfig();
        }
    };

    private CONF m_previousConfig;

    // don't use viewValue from super class to be able check if defaultValue was overwritten @see #getOverwriteMode()
    private VAL m_viewValue;

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
        getConfig().saveSettings(settings);
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
        getConfig().loadSettings(settings);
    }

    /**
     * @return Empty instance of the config
     */
    public abstract CONF createEmptyConfig();

    /**
     * @return the config of this node
     */
    protected CONF getConfig() {
        try {
            return m_configInitializer.get();
        } catch (ConcurrentException e) {
            throw new IllegalStateException("Couldn't create empty config.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHideInWizard() {
        return getConfig().isHideInWizard();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHideInWizard(final boolean hide) {
        getConfig().setHideInWizard(hide);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCssStyles() {
        return getConfig().getCustomCSS();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCssStyles(final String styles) {
        getConfig().setCustomCSS(styles);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canTriggerReExecution() {
        return getConfig().canTriggerReExecution();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performReset() {
        m_viewValue = null;
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
     * @return the viewValue
     */
    @Override
    public VAL getViewValue() {
        return getRelevantValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setViewValue(final VAL value) {
        synchronized (getLock()) {
            m_viewValue = value;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadViewValue(final VAL viewValue, final boolean useAsDefault) {
        synchronized (getLock()) {
            m_viewValue = viewValue;
            if (useAsDefault) {
                useCurrentValueAsDefault();
            }
        }
    }

    /**
     * @return The value with the highest priority which is valid.
     */
    protected VAL getRelevantValue() {
        synchronized (getLock()) {
            switch (getOverwriteMode()) {
                case WIZARD:
                    return m_viewValue;
                default:
                    return getConfig().getDefaultValue();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void preExecute() {
        if (m_previousConfig == null) {
            m_previousConfig = createEmptyConfig();
            transferConfig(getConfig(), m_previousConfig);
        }

        if (m_viewValue != null) {
            m_viewValue = copyConfigToViewValue(m_viewValue, getConfig(), m_previousConfig);
        }

        transferConfig(getConfig(), m_previousConfig);
    }

    private void transferConfig(final CONF from, final CONF to) {
        var copy = new NodeSettings("copy");
        from.saveSettings(copy);
        try {
            to.loadSettings(copy);
        } catch (InvalidSettingsException e) {
            // should never happen - just copying stuff over
            throw new IllegalStateException(e);
        }
    }

    /**
     * Transfers values from the configuration to the view value, possibly based on whether a certain config value has
     * been changed compared to the previous configuration. A config value changes (only?) if it is controlled by a flow
     * variable.
     *
     * This implementation replaces the entire view value with {@link WidgetConfig#getDefaultValue()} if at least one
     * 'sub-value' was changed (in most cases the default value is really just one value). Overwrite this method for a
     * more fine-grain control.
     *
     * @param currentViewValue the current view value
     * @param config the current configuration
     * @param previousConfig the previous configuration
     * @return the possibly adopted or replaced view value
     */
    protected VAL copyConfigToViewValue(final VAL currentViewValue, final CONF config, final CONF previousConfig) {
        // if the config's default value changed, we use the new default value instead
        // (because it is (most likely) controlled by at least one flow variable)
        if (!config.getDefaultValue().equals(previousConfig.getDefaultValue())) {
            return getConfig().getDefaultValue();
        } else {
            return currentViewValue;
        }
    }

    /**
     * @return The mode in which the value is overwritten
     */
    protected ValueOverwriteMode getOverwriteMode() {
        synchronized (getLock()) {
            if (m_viewValue != null) {
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
        if (valFile.exists()) {
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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File nodeInternDir, final ExecutionMonitor exec) throws IOException,
        CanceledExecutionException {
        // only save value, representation is not needing to be saved
        if (m_viewValue == null) {
            return;
        }
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

    /**
     * @return whether the default value is currently overwritten
     */
    public boolean hasOverwrittenDefaultValue() {
        return getOverwriteMode() == ValueOverwriteMode.WIZARD;
    }

}
