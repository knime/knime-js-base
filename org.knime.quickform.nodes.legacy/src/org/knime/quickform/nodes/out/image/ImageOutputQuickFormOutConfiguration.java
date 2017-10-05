/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME GmbH, Konstanz, Germany
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
 */
package org.knime.quickform.nodes.out.image;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.quickform.nodes.out.QuickFormOutConfiguration;

/**
 * Base configuration for node providing a image to the quickform result.
 *
 * @author Bernd Wiswedel, KNIME.com, Zurich, Switzerland
 */
public class ImageOutputQuickFormOutConfiguration extends QuickFormOutConfiguration {

    private boolean m_enlargeOnClick;
    private int m_maxWidth;
    private int m_maxHeight;

    /** @return the enlarge on click property */
    public boolean isEnlargeOnClick() {
        return m_enlargeOnClick;
    }

    /** @param value the value to set */
    public void setEnlargeOnClick(final boolean value) {
        m_enlargeOnClick = value;
    }

    /**
     * @return the maxWidth
     */
    public int getMaxWidth() {
        return m_maxWidth;
    }

    /**
     * @param maxWidth the maxWidth to set
     */
    public void setMaxWidth(final int maxWidth) {
        m_maxWidth = maxWidth;
    }

    /**
     * @return the maxHeight
     */
    public int getMaxHeight() {
        return m_maxHeight;
    }

    /**
     * @param maxHeight the maxHeight to set
     */
    public void setMaxHeight(final int maxHeight) {
        m_maxHeight = maxHeight;
    }

    /** {@inheritDoc} */
    @Override
    public void saveSettingsTo(final NodeSettingsWO settings) {
        super.saveSettingsTo(settings);
        settings.addBoolean("enlargeOnClick", m_enlargeOnClick);
        settings.addInt("maxWidth", m_maxWidth);
        settings.addInt("maxHeight", m_maxHeight);
    }

    /** {@inheritDoc} */
    @Override
    public void loadSettingsInDialog(final NodeSettingsRO settings) {
        super.loadSettingsInDialog(settings);
        m_enlargeOnClick = settings.getBoolean("enlargeOnClick", false);
        m_maxWidth = settings.getInt("maxWidth", 300);
        m_maxHeight = settings.getInt("maxHeight", 300);
    }

    /** {@inheritDoc} */
    @Override
    public void loadSettingsInModel(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        super.loadSettingsInModel(settings);
        m_enlargeOnClick = settings.getBoolean("enlargeOnClick");
        m_maxWidth = settings.getInt("maxWidth");
        m_maxHeight = settings.getInt("maxHeight");
    }

}
