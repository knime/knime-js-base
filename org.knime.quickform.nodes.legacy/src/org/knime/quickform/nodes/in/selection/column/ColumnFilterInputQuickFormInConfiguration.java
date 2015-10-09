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
 *
 */
package org.knime.quickform.nodes.in.selection.column;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.quickform.AbstractQuickFormConfiguration;

/**
 * Configuration for a column selector.
 * @author Thomas Gabriel, KNIME.com, Zurich, Switzerland
 * @since 2.6
 */
final class ColumnFilterInputQuickFormInConfiguration extends
        AbstractQuickFormConfiguration<ColumnFilterInputQuickFormValueInConfiguration> {

    private static final String KEY_ALLVALS = "allValues";

    private String[] m_allValues = new String[0];

    /**
     * @return the list of possible values out of which the value should be selected
     */
    String[] getAllValues() {
        return m_allValues.clone();
    }

    /**
     * @param allValues out of which the value should be selected
     */
    void setAllValues(final String[] allValues) {
        if (allValues == null) {
            m_allValues = new String[0];
        } else {
            m_allValues = allValues.clone();
        }
    }

    /** Save config to argument.
     * @param settings To save to.
     */
    @Override
    public void saveSettingsTo(final NodeSettingsWO settings) {
        super.saveSettingsTo(settings);
        settings.addStringArray(KEY_ALLVALS, m_allValues);
    }

    /** Load config in model.
     * @param settings To load from.
     * @throws InvalidSettingsException If that fails for any reason.
     */
    @Override
    public void loadSettingsInModel(final NodeSettingsRO settings)
        throws InvalidSettingsException {
        super.loadSettingsInModel(settings);
        m_allValues = settings.getStringArray(KEY_ALLVALS);
        if (m_allValues == null) {
            m_allValues = new String[0];
        }
    }

    /** Load settings in dialog, init defaults if that fails.
     * @param settings To load from.
     */
    @Override
    public void loadSettingsInDialog(final NodeSettingsRO settings) {
        super.loadSettingsInDialog(settings);
        m_allValues = settings.getStringArray(KEY_ALLVALS, new String[0]);
    }

    /** {@inheritDoc} */
    @Override
    public ColumnFilterInputQuickFormPanel createController() {
        return new ColumnFilterInputQuickFormPanel(this);
    }

    /** {@inheritDoc} */
    @Override
    public ColumnFilterInputQuickFormValueInConfiguration createValueConfiguration() {
        return new ColumnFilterInputQuickFormValueInConfiguration();
    }

}
