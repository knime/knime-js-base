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
 *   21 May 2019 (albrecht): created
 */
package org.knime.js.base.node.widget.input.bool;

import java.awt.GridBagConstraints;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.js.base.dialog.input.bool.BooleanInputComponentFactory;
import org.knime.js.base.node.base.input.bool.BooleanNodeConfig;
import org.knime.js.base.node.base.input.bool.BooleanNodeValue;
import org.knime.js.base.node.widget.ReExecutableWidgetNodeDialog;

/**
 * Node dialog for the boolean widget node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class BooleanWidgetNodeDialog extends ReExecutableWidgetNodeDialog<BooleanNodeValue> {

    private final BooleanInputWidgetConfig m_config;

    private final JCheckBox m_defaultField;

    private final JCheckBox m_pushIntVar;

    private final JComboBox<String> m_type;

    /**
     * Constructor, inits fields calls layout routines
     */
    public BooleanWidgetNodeDialog() {
        m_config = new BooleanInputWidgetConfig();
        m_defaultField = new JCheckBox();
        m_defaultField.setSelected(m_config.getDefaultValue().getBoolean());
        m_pushIntVar = new JCheckBox();
        m_type = new JComboBox<String>(BooleanInputComponentFactory.listBooleanInputComponents());
        createAndAddTab();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getValueString(final NodeSettingsRO settings) throws InvalidSettingsException {
        BooleanNodeValue value = new BooleanNodeValue();
        value.loadFromNodeSettings(settings);
        return "" + value.getBoolean();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
        addPairToPanel("Selection Type: ", m_type, panelWithGBLayout, gbc);
        addPairToPanel("Default Value: ", m_defaultField, panelWithGBLayout, gbc);
        addPairToPanel("Output as Integer: ", m_pushIntVar, panelWithGBLayout, gbc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
        throws NotConfigurableException {
        m_config.loadSettingsInDialog(settings);
        loadSettingsFrom(m_config);
        m_defaultField.setSelected(m_config.getDefaultValue().getBoolean());
        final BooleanNodeConfig booleanConfig = m_config.getBooleanConfig();
        m_pushIntVar.setSelected(booleanConfig.isPushIntVar());
        m_type.setSelectedItem(booleanConfig.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        m_config.getDefaultValue().setBoolean(m_defaultField.isSelected());
        saveSettingsTo(m_config);
        final BooleanNodeConfig booleanConfig = m_config.getBooleanConfig();
        booleanConfig.setPushIntVar(m_pushIntVar.isSelected());
        booleanConfig.setType(m_type.getItemAt(m_type.getSelectedIndex()));
        m_config.saveSettings(settings);
    }

}
