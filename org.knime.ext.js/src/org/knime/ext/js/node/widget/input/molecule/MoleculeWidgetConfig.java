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
 *   Jun 12, 2014 (winter): created
 */
package org.knime.ext.js.node.widget.input.molecule;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.base.node.widget.LabeledFlowVariableWidgetConfig;

/**
 * The config for the molecule string input quick form node.
 *
 * @author Daniel Bogenrieder, KNIME AG, Zurich, Switzerland
 */
public class MoleculeWidgetConfig extends LabeledFlowVariableWidgetConfig<MoleculeWidgetValue> {

    private static final String CFG_FORMAT = "format";
    private static final String DEFAULT_FORMAT = MoleculeWidgetNodeModel.DEFAULT_FORMATS[0];
    private String m_format = DEFAULT_FORMAT;

    private static final String CFG_SKETCHER_PATH = "sketcherPath";
    private static final String DEFAULT_SKETCHER_PATH = "";
    private String m_sketcherPath = DEFAULT_SKETCHER_PATH;

    private static final String CFG_DISABLE_LINE_NOTIFICATIONS = "disableLineNotifications";
    private static final boolean DEFAULT_DISABLE_LINE_NOTIFICATIONS = false;
    private boolean m_disableLineNotification;
    

    /**
     * @return the format
     */
    String getFormat() {
        return m_format;
    }

    /**
     * @param format the format to set
     */
    void setFormat(final String format) {
        m_format = format;
    }

    public String getSketcherPath() {
		return m_sketcherPath;
	}
    
    public void setSketcherPath(String sketcherPath) {
		m_sketcherPath = sketcherPath;
	}

    public void setDisableLineNotifications(boolean disableLineNotifications) {
        m_disableLineNotification = disableLineNotifications;
    }

    public boolean isDisableLineNotifications() {
        return m_disableLineNotification;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveSettings(final NodeSettingsWO settings) {
        super.saveSettings(settings);
        settings.addString(CFG_FORMAT, m_format);
        settings.addString(CFG_SKETCHER_PATH, m_sketcherPath);
        settings.addBoolean(CFG_DISABLE_LINE_NOTIFICATIONS, m_disableLineNotification);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        super.loadSettings(settings);
        m_format = settings.getString(CFG_FORMAT);

        m_sketcherPath = settings.getString(CFG_SKETCHER_PATH, DEFAULT_SKETCHER_PATH);

       m_disableLineNotification = settings.getBoolean(CFG_DISABLE_LINE_NOTIFICATIONS, DEFAULT_DISABLE_LINE_NOTIFICATIONS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettingsInDialog(final NodeSettingsRO settings) {
        super.loadSettingsInDialog(settings);
        m_format = settings.getString(CFG_FORMAT, DEFAULT_FORMAT);

        m_sketcherPath = settings.getString(CFG_SKETCHER_PATH, DEFAULT_SKETCHER_PATH);

        m_disableLineNotification = settings.getBoolean(CFG_DISABLE_LINE_NOTIFICATIONS, DEFAULT_DISABLE_LINE_NOTIFICATIONS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected MoleculeWidgetValue createEmptyValue() {
        return new MoleculeWidgetValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(", ");
        sb.append("format=");
        sb.append(m_format);
        sb.append(m_disableLineNotification);
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode())
                .append(m_format)
                .append(m_sketcherPath)
                .append(m_disableLineNotification)
                .toHashCode();
    }

    /**
     * {@inheritDoc}
     */
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
        MoleculeWidgetConfig other = (MoleculeWidgetConfig)obj;
        return new EqualsBuilder().appendSuper(super.equals(obj))
                .append(m_format, other.m_format)
                .append(m_sketcherPath, other.m_sketcherPath)
                .append(m_disableLineNotification, other.m_disableLineNotification)
                .isEquals();
    }

}
