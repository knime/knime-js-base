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
package org.knime.quickform.nodes.in.selection.value;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.util.filter.StringFilterPanel;
import org.knime.core.quickform.QuickFormConfigurationPanel;
import org.knime.core.util.node.quickform.AbstractQuickFormElement;
import org.knime.core.util.node.quickform.in.AbstractQuickFormInElement;
import org.knime.core.util.node.quickform.in.ValueFilterInputQuickFormInElement;

/**
 * Panel shown in meta node dialogs, displaying a value filter panel.
 *
 * @author Thomas Gabriel, KNIME AG, Zurich, Switzerland
 * @since 2.6
 */
public class ValueFilterInputQuickFormPanel extends
    QuickFormConfigurationPanel
        <ValueFilterInputQuickFormValueInConfiguration> {

    private final DialogComponentStringSelection m_columnField;
    private final StringFilterPanel m_valueFilter;

    /** Creates anew String list selection configuration.
     * @param cfg underlying config object
     */
    public ValueFilterInputQuickFormPanel(
            final ValueFilterInputQuickFormInConfiguration cfg) {
        super(new BorderLayout());
        JLabel label = new JLabel(cfg.getDescription());
        add(label, BorderLayout.NORTH);
        String[] choices = cfg.getChoices();
        if (choices == null || choices.length == 0) {
            add(new JLabel("No domain information available."), BorderLayout.SOUTH);
            m_columnField = null;
            m_valueFilter = null;
        } else {
            m_columnField = new DialogComponentStringSelection(new SettingsModelString("column-selection",
                    choices[0]), cfg.getLabel(), choices);
            m_columnField.getModel().addChangeListener(new ChangeListener() {
                /** {@inheritDoc} */
                @Override
                public void stateChanged(final ChangeEvent ce) {
                    String column = ((SettingsModelString)
                            m_columnField.getModel()).getStringValue();
                    Set<String> values = cfg.getChoiceValues(column);
                    m_valueFilter.update(Collections.<String>emptyList(),
                            new ArrayList<String>(values),
                            values.toArray(new String[0]));
                }
            });
            m_valueFilter = new StringFilterPanel(true);
            String borderLabel = " " + cfg.getLabel() + " ";
            m_valueFilter.setBorder(BorderFactory.createTitledBorder(borderLabel));
            m_valueFilter.update(Collections.EMPTY_LIST, Collections.EMPTY_LIST,
                        cfg.getChoiceValues(choices[0]).toArray(new String[0]));
            add(m_columnField.getComponentPanel(), BorderLayout.CENTER);
            add(m_valueFilter, BorderLayout.SOUTH);
        }
        loadValueConfig(cfg.getValueConfiguration());
    }


    /** {@inheritDoc} */
    @Override
    public void saveSettings(
                final ValueFilterInputQuickFormValueInConfiguration config)
                throws InvalidSettingsException {
        if (m_columnField != null && m_valueFilter != null) {
            final String column = ((SettingsModelString) m_columnField.getModel()).getStringValue();
            config.setColumn(column);
            config.setValues(m_valueFilter.getIncludeList().toArray(new String[0]));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void loadSettings(
                final ValueFilterInputQuickFormValueInConfiguration cfg) {
        loadValueConfig(cfg);
    }


    private void loadValueConfig(
            final ValueFilterInputQuickFormValueInConfiguration cfg) {
        if (m_columnField != null && m_valueFilter != null) {
            final String column = cfg.getColumn();
            if (column != null) {
                ((SettingsModelString) m_columnField.getModel()).setStringValue(column);
            }

            // read includes from config
            String[] ins = cfg.getValues();
            List<String> includes;
            if (ins != null) {
                includes = Arrays.asList(ins);
            } else {
                includes = new ArrayList<String>();
            }

            // build list of all values
            Set<String> choices = new LinkedHashSet<String>(m_valueFilter.getIncludeList());
            choices.addAll(m_valueFilter.getAllValues());
            List<String> excludes = new ArrayList<String>(choices);
            excludes.removeAll(includes);
            m_valueFilter.update(includes, excludes,  choices.toArray(new String[0]));
        }
    }

    @Override
    public void updateQuickFormInElement(final AbstractQuickFormInElement e) throws InvalidSettingsException {
        ValueFilterInputQuickFormInElement cast = AbstractQuickFormElement.cast(
                ValueFilterInputQuickFormInElement.class, e);
        if (m_columnField != null && m_valueFilter != null) {
            final String column = ((SettingsModelString) m_columnField.getModel()).getStringValue();
            cast.setSelection(column, m_valueFilter.getIncludeList().toArray(new String[0]));
        }
    }
}
