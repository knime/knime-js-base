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
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
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
 * History:
 * 24-Febr-2011: created
 */
package org.knime.quickform.nodes.in.selection.column;

import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import javax.swing.JPanel;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.filter.column.DataColumnSpecFilterConfiguration;
import org.knime.core.node.util.filter.column.DataColumnSpecFilterPanel;
import org.knime.quickform.nodes.in.QuickFormInNodeDialogPane;

/**
 * Dialog to node.
 *
 * @author Thomas Gabriel, KNIME AG, Zurich, Switzerland
 * @since 2.6
 */
final class ColumnFilterInputQuickFormInNodeDialogPane
          extends QuickFormInNodeDialogPane
                <ColumnFilterInputQuickFormInConfiguration> {

     private final DataColumnSpecFilterPanel m_columnFilter;

     /** Constructors, inits fields calls layout routines. */
     ColumnFilterInputQuickFormInNodeDialogPane() {
          m_columnFilter = new DataColumnSpecFilterPanel(true);
          createAndAddTab();
     }

     /** {@inheritDoc} */
     @Override
     protected ColumnFilterInputQuickFormInConfiguration
                createConfiguration() {
          return new ColumnFilterInputQuickFormInConfiguration();
     }

     /** {@inheritDoc} */
     @Override
     protected void fillPanel(final JPanel panelWithGBLayout,
                final GridBagConstraints gbc) {
          addPairToPanel("Column Filter: ", m_columnFilter,
                     panelWithGBLayout, gbc);
     }

     /** {@inheritDoc} */
     @Override
     protected void loadSettingsFrom(final NodeSettingsRO settings,
         final PortObjectSpec[] specs) throws NotConfigurableException {
         DataTableSpec spec = (DataTableSpec)specs[0];
         DataColumnSpecFilterConfiguration config =
                 new DataColumnSpecFilterConfiguration("columnFilter", null, 0);
         config.loadDefaults(spec, false);
         m_columnFilter.loadConfiguration(config, spec);
         super.loadSettingsFrom(settings, specs);
     }

    /** {@inheritDoc} */
    @Override
    protected void saveAdditionalSettings(
            final ColumnFilterInputQuickFormInConfiguration config)
            throws InvalidSettingsException {
        Set<DataColumnSpec> includes = m_columnFilter.getIncludeList();
        String[] includedNames = new String[includes.size()];
        int i = 0;
        for (DataColumnSpec dcs : includes) {
            includedNames[i++] = dcs.getName();
        }
        config.getValueConfiguration().setValues(includedNames);
        config.setAllValues(m_columnFilter.getAllValues()
                .toArray(new String[0]));
    }

    /** {@inheritDoc} */
    @Override
    protected void loadAdditionalSettings(
            final ColumnFilterInputQuickFormInConfiguration config) {
        ArrayList<String> incl = new ArrayList<String>();
        if (config.getValueConfiguration().getValues() != null) {
            incl.addAll(Arrays.asList(
                    config.getValueConfiguration().getValues()));
        }
        Set<String> allValues = m_columnFilter.getAllValues();
        ArrayList<String> excl = new ArrayList<String>(allValues);
        excl.removeAll(incl);
        m_columnFilter.update(incl, excl,
                allValues.toArray(new String[0]));
    }
}
