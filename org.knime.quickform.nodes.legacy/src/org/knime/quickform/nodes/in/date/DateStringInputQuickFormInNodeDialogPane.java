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
package org.knime.quickform.nodes.in.date;

import java.awt.GridBagConstraints;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFormattedTextField;
import javax.swing.JPanel;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.util.node.quickform.in.DateStringInputQuickFormInElement;
import org.knime.quickform.nodes.in.QuickFormInNodeDialogPane;

/**
 * Dialog to node.
 *
 * @author Peter Ohl, KNIME AG, Zurich, Switzerland
 */
final class DateStringInputQuickFormInNodeDialogPane
    extends QuickFormInNodeDialogPane<DateStringInputQuickFormInConfiguration> {

    private final JFormattedTextField m_valueField;

    /** Constructors, inits fields calls layout routines. */
    DateStringInputQuickFormInNodeDialogPane() {
        m_valueField = new JFormattedTextField(new SimpleDateFormat(DateStringInputQuickFormInElement.FORMAT));
        m_valueField.setColumns(DEF_TEXTFIELD_WIDTH);
        m_valueField.setValue(new Date());
        createAndAddTab();
    }

    /** {@inheritDoc} */
    @Override
    protected DateStringInputQuickFormInConfiguration createConfiguration() {
        return new DateStringInputQuickFormInConfiguration();
    }

    /** {@inheritDoc} */
    @Override
    protected void fillPanel(final JPanel panelWithGBLayout,
            final GridBagConstraints gbc) {
        addPairToPanel("Date Value: ", m_valueField, panelWithGBLayout, gbc);
    }

    /** {@inheritDoc} */
    @Override
    protected void saveAdditionalSettings(
            final DateStringInputQuickFormInConfiguration config)
            throws InvalidSettingsException {
        try {
            m_valueField.commitEdit();
        } catch (ParseException e) {
            throw new InvalidSettingsException(
                    "Unable to parse value as date", e);
        }
        Date d = (Date)m_valueField.getValue();
        config.getValueConfiguration().setValue(d);
    }

    /** {@inheritDoc} */
    @Override
    protected void loadAdditionalSettings(
            final DateStringInputQuickFormInConfiguration config) {
        Date d = config.getValueConfiguration().getValue();
        if (d == null) {
            d = new Date();
        }
        m_valueField.setValue(d);
    }
}
