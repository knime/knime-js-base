/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
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
 */
package org.knime.quickform.nodes.in.selection.column;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.util.filter.StringFilterPanel;
import org.knime.core.quickform.QuickFormConfigurationPanel;
import org.knime.core.util.node.quickform.in.AbstractQuickFormInElement;
import org.knime.core.util.node.quickform.in.TwinStringListInputQuickFormInElement;

/**
 * Panel shown in meta node dialogs, displaying a column filter panel.
 *
 * @author Thomas Gabriel, KNIME.com, Zurich, Switzerland
 * @since 2.6
 */
public class ColumnFilterInputQuickFormPanel extends
        QuickFormConfigurationPanel
        <ColumnFilterInputQuickFormValueInConfiguration> {

    private final StringFilterPanel m_columnFilter;

    private final String[] m_allChoices;

    /** Creates anew String list selection configuration.
     * @param config underlying config object
     */
    public ColumnFilterInputQuickFormPanel(
            final ColumnFilterInputQuickFormInConfiguration config) {
        super(new BorderLayout());
        JLabel label = new JLabel(config.getDescription());
        add(label, BorderLayout.NORTH);
        m_columnFilter = new StringFilterPanel(true);
        String borderLabel = " " + config.getLabel() + " ";
        m_columnFilter.setBorder(BorderFactory.createTitledBorder(borderLabel));
        if (config.getAllValues() != null && config.getAllValues().length > 0) {
            m_allChoices = config.getAllValues();
            add(m_columnFilter, BorderLayout.CENTER);
        } else {
            m_allChoices = new String[0];
            final JLabel warnLabel = new JLabel(
            "No choices defined in quickform node");
            add(warnLabel, BorderLayout.CENTER);
        }
        loadValueConfig(config.getValueConfiguration());
    }

    /** {@inheritDoc} */
    @Override
    public void saveSettings(
                final ColumnFilterInputQuickFormValueInConfiguration config)
                throws InvalidSettingsException {
        config.setValues(getValues());
    }

    /** {@inheritDoc} */
    @Override
    public void loadSettings(
                final ColumnFilterInputQuickFormValueInConfiguration cfg) {
        loadValueConfig(cfg);
    }

    private void loadValueConfig(
            final ColumnFilterInputQuickFormValueInConfiguration cfg) {
        ArrayList<String> exs = new ArrayList<String>();
        exs.addAll(Arrays.asList(m_allChoices));
        String[] ins = cfg.getValues();
        if (ins != null) {
            exs.removeAll(Arrays.asList(ins));
        } else {
            ins = new String[0];
        }
        m_columnFilter.update(Arrays.asList(ins), exs, m_allChoices);
    }
    
    /** {@inheritDoc} */
    @Override
    public void updateQuickFormInElement(final AbstractQuickFormInElement e) throws InvalidSettingsException {
        TwinStringListInputQuickFormInElement cast =
            AbstractQuickFormInElement.cast(
                    TwinStringListInputQuickFormInElement.class, e);
        cast.setValues(getValues());
    }

    private String[] getValues() {
        Set<String> inList = m_columnFilter.getIncludeList();
        return inList.toArray(new String[inList.size()]);
    }

}
