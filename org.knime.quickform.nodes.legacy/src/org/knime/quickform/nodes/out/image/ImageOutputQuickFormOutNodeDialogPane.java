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
package org.knime.quickform.nodes.out.image;

import java.awt.GridBagConstraints;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.node.InvalidSettingsException;
import org.knime.quickform.nodes.out.QuickFormOutNodeDialogPane;

/**
 * Dialog for node that reads an image and provides it
 * in a quickform output.
 *
 * @author Bernd Wiswedel, KNIME AG, Zurich, Switzerland
 */
public class ImageOutputQuickFormOutNodeDialogPane extends
        QuickFormOutNodeDialogPane<ImageOutputQuickFormOutConfiguration> {

    private final JCheckBox m_enlargeOnClickChecker;
    private final JCheckBox m_maxWidthChecker;
    private final JSpinner m_maxWidthSpinner;
    private final JCheckBox m_maxHeightChecker;
    private final JSpinner m_maxHeightSpinner;

    /** Create new dialog.
     */
    public ImageOutputQuickFormOutNodeDialogPane() {
        m_enlargeOnClickChecker = new JCheckBox("Enlarge on Click");
        m_maxWidthChecker = new JCheckBox("Maximum Width", true);
        m_maxHeightChecker = new JCheckBox("Maximum Height", true);
        m_maxWidthSpinner = new JSpinner(new SpinnerNumberModel(300, 20, Integer.MAX_VALUE, 50));
        m_maxHeightSpinner = new JSpinner(new SpinnerNumberModel(300, 20, Integer.MAX_VALUE, 50));
        m_maxWidthChecker.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                m_maxWidthSpinner.setEnabled(m_maxWidthChecker.isSelected());
            }
        });
        m_maxHeightChecker.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                m_maxHeightSpinner.setEnabled(m_maxHeightChecker.isSelected());
            }
        });
        m_maxWidthChecker.doClick();
        m_maxWidthChecker.doClick();
        createAndAddTab();
    }

    /** {@inheritDoc} */
    @Override
    protected void fillPanel(
            final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        panelWithGBLayout.add(m_enlargeOnClickChecker, gbc);
        addPairToPanel(m_maxWidthChecker, m_maxWidthSpinner, panelWithGBLayout, gbc);
        addPairToPanel(m_maxHeightChecker, m_maxHeightSpinner, panelWithGBLayout, gbc);
    }

    /** {@inheritDoc} */
    @Override
    protected ImageOutputQuickFormOutConfiguration createConfiguration() {
        return new ImageOutputQuickFormOutConfiguration();
    }

    /** {@inheritDoc} */
    @Override
    protected void saveAdditionalSettings(final ImageOutputQuickFormOutConfiguration config)
            throws InvalidSettingsException {
        config.setEnlargeOnClick(m_enlargeOnClickChecker.isSelected());
        int maxWidth = m_maxWidthChecker.isSelected() ? (Integer)m_maxWidthSpinner.getValue() : -1;
        int maxHeight = m_maxHeightChecker.isSelected() ? (Integer)m_maxHeightSpinner.getValue() : -1;
        config.setEnlargeOnClick(m_enlargeOnClickChecker.isSelected());
        config.setMaxWidth(maxWidth);
        config.setMaxHeight(maxHeight);
    }

    /** {@inheritDoc} */
    @Override
    protected void loadAdditionalSettings(final ImageOutputQuickFormOutConfiguration config) {
        m_enlargeOnClickChecker.setSelected(config.isEnlargeOnClick());
        int maxWidth = config.getMaxWidth();
        int maxHeight = config.getMaxHeight();
        if ((maxHeight > 0) != m_maxHeightChecker.isSelected()) {
            m_maxHeightChecker.doClick();
        }
        if ((maxWidth > 0) != m_maxWidthChecker.isSelected()) {
            m_maxWidthChecker.doClick();
        }
        m_maxWidthSpinner.setValue(maxWidth > 0 ? maxWidth : 300);
        m_maxHeightSpinner.setValue(maxHeight > 0 ? maxHeight : 300);
    }

}
