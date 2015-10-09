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
 * History
 *   Jun 22, 2011 (wiswedel): created
 */
package org.knime.quickform.nodes.in.fileupload;

import java.awt.FlowLayout;
import java.io.File;

import javax.swing.JLabel;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.util.FilesHistoryPanel;
import org.knime.core.quickform.QuickFormConfigurationPanel;
import org.knime.core.util.node.quickform.AbstractQuickFormElement;
import org.knime.core.util.node.quickform.in.AbstractQuickFormInElement;
import org.knime.core.util.node.quickform.in.FileUploadQuickFormInElement;

/**
 *
 * @author Bernd Wiswedel, KNIME.com, Zurich, Switzerland
 */
public class FileUploadQuickFormPanel extends
        QuickFormConfigurationPanel<FileUploadQuickFormValueInConfiguration> {

    private final FilesHistoryPanel m_historyPanel;

    /** Constructors, inits fields calls layout routines. */
    FileUploadQuickFormPanel(final FileUploadQuickFormInConfiguration cfg) {
        super(new FlowLayout(FlowLayout.LEFT));
        String labelString = cfg.getLabel();
        JLabel label = new JLabel(labelString);
        label.setToolTipText(cfg.getDescription());
        add(label);
        String[] extensions = cfg.getExtensions();
        String historyID = "quickform_select";
        if (extensions != null && extensions.length > 0) {
            String first = extensions[0];
            historyID = historyID.concat("_" + first);
        }
        m_historyPanel = new FilesHistoryPanel(historyID, extensions);
        add(m_historyPanel);
        loadValueConfig(cfg.getValueConfiguration());
    }

    /** {@inheritDoc} */
    @Override
    public void saveSettings(
            final FileUploadQuickFormValueInConfiguration config)
            throws InvalidSettingsException {
        String sel = m_historyPanel.getSelectedFile();
        config.setLocation(sel);
        m_historyPanel.addToHistory();
    }

    /** {@inheritDoc} */
    @Override
    public void loadSettings(
            final FileUploadQuickFormValueInConfiguration config) {
        loadValueConfig(config);
    }

    private void loadValueConfig(
            final FileUploadQuickFormValueInConfiguration config) {
        m_historyPanel.updateHistory();
        String location = config.getLocation();
        m_historyPanel.setSelectedFile(location);
    }

    /** {@inheritDoc} */
    @Override
    public void updateQuickFormInElement(final AbstractQuickFormInElement e) throws InvalidSettingsException {
        FileUploadQuickFormInElement cast =
            AbstractQuickFormElement.cast(FileUploadQuickFormInElement.class, e);
        cast.setFile(new File(m_historyPanel.getSelectedFile()));
    }

}
