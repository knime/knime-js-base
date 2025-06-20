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
 *   2 May 2019 (albrecht): created
 */
package org.knime.js.base.node.configuration;

import java.util.regex.Pattern;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.dialog.DialogNode;
import org.knime.core.node.dialog.DialogNodeValue;
import org.knime.core.node.util.CheckUtils;
import org.knime.core.node.workflow.SubNodeContainer;

/**
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 * @param <VAL>
 */
public abstract class DialogNodeConfig<VAL extends DialogNodeValue> {

    private static final String CFG_HIDE_IN_DIALOG = "hideInDialog";
    public static final boolean DEFAULT_HIDE_IN_DIALOG = false;
    private boolean m_hideInDialog = DEFAULT_HIDE_IN_DIALOG;

    private static final String CFG_DEFAULT_VALUE = "defaultValue";
    private VAL m_defaultValue;

    private static final String CFG_PARAMETER_NAME = "parameterName";
    private String m_parameterName = SubNodeContainer.getDialogNodeParameterNameDefault(getClass());

    /**
     * @return the hideInDialog
     */
    public boolean getHideInDialog() {
        return m_hideInDialog;
    }

    /**
     * @param hideInDialog the hideInDialog to set
     */
    public void setHideInDialog(final boolean hideInDialog) {
        m_hideInDialog = hideInDialog;
    }

    /**
     * @return the default value
     */
    public synchronized VAL getDefaultValue() {
        if (m_defaultValue == null) {
            m_defaultValue = createEmptyValue();
        }
        return m_defaultValue;
    }

    /**
     * Creates an instance of a value used for the default value of this config.
     *
     * @return Create a value instance
     */
    protected abstract VAL createEmptyValue();

    /**
     * @return the parameterName
     */
    public String getParameterName() {
        return m_parameterName;
    }

    /**
     * @param s the parameterName to set
     * @throws InvalidSettingsException If null or invalid
     */
    public void setParameterName(final String s) throws InvalidSettingsException {
        CheckUtils.checkSettingNotNull(s, "Parameter name must not be null");
        Pattern pattern = DialogNode.PARAMETER_NAME_PATTERN;
        CheckUtils.checkSetting("".equals(s) || pattern.matcher(s).matches(),
            "Parameter name \"%s\" is invalid - only letters, digits and single dash "
            + "characters are allowed and the name must start and end with a letter.", s);
        m_parameterName = s;
    }

    /**
     * @param settings The settings to save to
     */
    public void saveSettings(final NodeSettingsWO settings) {
        NodeSettingsWO defaultValueSettings = settings.addNodeSettings(CFG_DEFAULT_VALUE);
        getDefaultValue().saveToNodeSettings(defaultValueSettings);
        settings.addBoolean(CFG_HIDE_IN_DIALOG, m_hideInDialog);
        settings.addString(CFG_PARAMETER_NAME, m_parameterName);
    }

    /**
     * @param settings The settings to load from
     * @throws InvalidSettingsException If the settings are not valid
     */
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        NodeSettingsRO defaultValueSettings = settings.getNodeSettings(CFG_DEFAULT_VALUE);
        m_defaultValue = createEmptyValue();
        m_defaultValue.loadFromNodeSettings(defaultValueSettings);
        m_hideInDialog = settings.getBoolean(CFG_HIDE_IN_DIALOG);
        setParameterName(settings.getString(CFG_PARAMETER_NAME));
    }

    /**
     * @param settings The settings to load from
     */
    public void loadSettingsInDialog(final NodeSettingsRO settings) {
        m_defaultValue = createEmptyValue();
        NodeSettingsRO defaultValueSettings;
        try {
            defaultValueSettings = settings.getNodeSettings(CFG_DEFAULT_VALUE);
            m_defaultValue.loadFromNodeSettingsInDialog(defaultValueSettings);
        } catch (InvalidSettingsException e) {
            // Stay with defaults
        }
        m_hideInDialog = settings.getBoolean(CFG_HIDE_IN_DIALOG, DEFAULT_HIDE_IN_DIALOG);
        final String defaultParName = SubNodeContainer.getDialogNodeParameterNameDefault(getClass());
        String parName = settings.getString(CFG_PARAMETER_NAME, defaultParName);
        try {
            setParameterName(parName);
        } catch (InvalidSettingsException ise) {
            m_parameterName = defaultParName;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("hideInDialog=");
        sb.append(m_hideInDialog);
        sb.append(", ");
        sb.append("paramaterName=");
        sb.append(m_parameterName);
        sb.append(", ");
        sb.append("defaultValue=");
        sb.append("{");
        sb.append(getDefaultValue());
        sb.append("}");
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_hideInDialog)
                .append(m_parameterName)
                .append(getDefaultValue())
                .toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        DialogNodeConfig<VAL> other = (DialogNodeConfig<VAL>)obj;
        return new EqualsBuilder()
                .append(m_hideInDialog, other.m_hideInDialog)
                .append(m_parameterName, other.m_parameterName)
                .append(getDefaultValue(), other.getDefaultValue())
                .isEquals();
    }

}
