/*
 * ------------------------------------------------------------------------
 *
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
 * ---------------------------------------------------------------------
 *
 * History
 *   May 18, 2015 (Christian Albrecht): created
 */
package org.knime.dynamic.js;

import javax.swing.event.ChangeListener;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.SettingsModel;
import org.knime.core.node.port.PortObjectSpec;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
public class SettingsModelSVGOptions extends SettingsModel {

    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";
    private static final String FULLSCREEN = "fullscreen";
    private static final String SHOW_FULLSCREEN = "showFullscreen";

    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 600;
    private static final boolean DEFAULT_FULLSCREEN = true;

    private int m_width = DEFAULT_WIDTH;
    private int m_height = DEFAULT_HEIGHT;
    private boolean m_showFullscreenOption = DEFAULT_FULLSCREEN;
    private boolean m_fullscreen = DEFAULT_FULLSCREEN;

    private final String m_configName;

    /**
     * Creates a new object holding SVG image options. The default width and height of 800 x 600px is used.
     *
     * @param configName the identifier the value is stored with in the {@link org.knime.core.node.NodeSettings} object
     */
    public SettingsModelSVGOptions(final String configName) {
        this(configName, DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_FULLSCREEN);
    }

    /**
     * Creates a new object holding SVG image options.
     *
     * @param configName the identifier the value is stored with in the {@link org.knime.core.node.NodeSettings} object
     * @param defaultWidth The initial width in px.
     * @param defaultHeight The initial height in px.
     */
    public SettingsModelSVGOptions(final String configName, final int defaultWidth, final int defaultHeight) {
        this(configName, defaultWidth, defaultHeight, DEFAULT_FULLSCREEN);
    }

    /**
     * Creates a new object holding SVG image options.
     *
     * @param configName the identifier the value is stored with in the {@link org.knime.core.node.NodeSettings} object
     * @param defaultWidth The initial width in px.
     * @param defaultHeight The initial height in px.
     * @param defaultFullscreen The initial value of the flag setting view to fullscreen.
     *
     */
    public SettingsModelSVGOptions(final String configName, final int defaultWidth, final int defaultHeight,
        final boolean defaultFullscreen) {
        if ((configName == null) || "".equals(configName)) {
            throw new IllegalArgumentException("The configName must be a " + "non-empty string");
        }
        m_configName = configName;
        m_width = defaultWidth;
        m_height = defaultHeight;
        m_fullscreen = defaultFullscreen;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected SettingsModelSVGOptions createClone() {
        return new SettingsModelSVGOptions(m_configName, m_width, m_height, m_fullscreen);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getModelTypeID() {
        return "SMID_svg";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getConfigName() {
        return m_configName;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return m_width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(final int width) {
        int prevWidth = m_width;
        m_width = width;
        if (prevWidth != m_width) {
            notifyChangeListeners();
        }
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return m_height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(final int height) {
        int prevHeight = m_height;
        m_height = height;
        if (prevHeight != m_height) {
            notifyChangeListeners();
        }
    }

    /**
     * @return The allow fullscreen flag.
     */
    public boolean getShowFullscreenOption() {
        return m_showFullscreenOption;
    }

    /**
     * @param showFullscreenOption the showFullscreenOption to set
     */
    public void setShowFullscreenOption(final boolean showFullscreenOption) {
        m_showFullscreenOption = showFullscreenOption;
    }

    /**
     * @return The allow fullscreen flag.
     */
    public boolean getAllowFullscreen() {
        return m_fullscreen;
    }

    /**
     * @param fullscreen the fullscreen to set
     */
    public void setAllowFullscreen(final boolean fullscreen) {
        boolean prevFullscreen = m_fullscreen;
        m_fullscreen = fullscreen;
        if (prevFullscreen != m_fullscreen) {
            notifyChangeListeners();
        }
    }

    /**
     * @return an object serializable as JSON string
     */
    public JSONSVGOptions getJSONSerializableObject() {
        JSONSVGOptions options = new JSONSVGOptions();
        options.setWidth(getWidth());
        options.setHeight(getHeight());
        options.setFullscreen(getAllowFullscreen());
        return options;
    }

    /**
     * Sets the values from a JSON deserialized object.
     * @param options the JSON object
     */
    public void setFromJSON(final JSONSVGOptions options) {
        setWidth(options.getWidth());
        setHeight(options.getHeight());
        setAllowFullscreen(options.getFullscreen());
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsForDialog(final NodeSettingsRO settings, final PortObjectSpec[] specs)
        throws NotConfigurableException {
        NodeSettingsRO svgSettings;
        try {
            svgSettings = settings.getNodeSettings(m_configName);
        } catch (InvalidSettingsException e) {
            // if settings not found: keep the old value.
            return;
        }
        try {
            // use the current value, if no value is stored in the settings
            setWidth(svgSettings.getInt(WIDTH, m_width));
            setHeight(svgSettings.getInt(HEIGHT, m_height));
            setAllowFullscreen(svgSettings.getBoolean(FULLSCREEN, m_fullscreen));
            setShowFullscreenOption(svgSettings.getBoolean(SHOW_FULLSCREEN, m_showFullscreenOption));
        } catch (final IllegalArgumentException iae) {
            // if the argument is not accepted: keep the old value.
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsForDialog(final NodeSettingsWO settings) throws InvalidSettingsException {
        NodeSettingsWO svgSettings = settings.addNodeSettings(m_configName);
        svgSettings.addInt(WIDTH, getWidth());
        svgSettings.addInt(HEIGHT, getHeight());
        svgSettings.addBoolean(FULLSCREEN, getAllowFullscreen());
        svgSettings.addBoolean(SHOW_FULLSCREEN, getShowFullscreenOption());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettingsForModel(final NodeSettingsRO settings) throws InvalidSettingsException {
        NodeSettingsRO svgSettings = settings.getNodeSettings(m_configName);
        svgSettings.getInt(WIDTH);
        svgSettings.getInt(HEIGHT);
        svgSettings.getBoolean(FULLSCREEN);
        svgSettings.getBoolean(SHOW_FULLSCREEN);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsForModel(final NodeSettingsRO settings) throws InvalidSettingsException {
        try {
            NodeSettingsRO svgSettings = settings.getNodeSettings(m_configName);
            // no default value, throw an exception instead
            setWidth(svgSettings.getInt(WIDTH));
            setHeight(svgSettings.getInt(HEIGHT));
            setAllowFullscreen(svgSettings.getBoolean(FULLSCREEN));
            setShowFullscreenOption(svgSettings.getBoolean(SHOW_FULLSCREEN));
        } catch (final IllegalArgumentException iae) {
            throw new InvalidSettingsException(iae.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsForModel(final NodeSettingsWO settings) {
        NodeSettingsWO svgSettings = settings.addNodeSettings(m_configName);
        svgSettings.addInt(WIDTH, getWidth());
        svgSettings.addInt(HEIGHT, getHeight());
        svgSettings.addBoolean(FULLSCREEN, getAllowFullscreen());
        svgSettings.addBoolean(SHOW_FULLSCREEN, getShowFullscreenOption());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + " ('" + m_configName + "')";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void prependChangeListener(final ChangeListener l) {
        // make method visible in this package
        super.prependChangeListener(l);
    }

    /**
     * Wrapper for JSON serialization
     * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
     */
    @JsonAutoDetect
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
    public static class JSONSVGOptions {

        int m_width;
        int m_height;
        boolean m_fullscreen;

        /**
         * @return the width
         */
        public int getWidth() {
            return m_width;
        }

        /**
         * @param width the width to set
         */
        public void setWidth(final int width) {
            m_width = width;
        }

        /**
         * @return the height
         */
        public int getHeight() {
            return m_height;
        }

        /**
         * @param height the height to set
         */
        public void setHeight(final int height) {
            m_height = height;
        }

        /**
         * @return true if fullscreen
         */
        public boolean getFullscreen() {
            return m_fullscreen;
        }

        /**
         * @param fullscreen the fullscreen to set
         */
        public void setFullscreen(final boolean fullscreen) {
            m_fullscreen = fullscreen;
        }

        /**
         * @param settings
         */
        public void saveToNodeSettings(final NodeSettingsWO settings) {
            settings.addInt(WIDTH, m_width);
            settings.addInt(HEIGHT, m_height);
            settings.addBoolean(FULLSCREEN, m_fullscreen);
        }

        /**
         * @param settings
         * @return
         * @throws InvalidSettingsException
         */
        public static JSONSVGOptions loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
            JSONSVGOptions options = new JSONSVGOptions();
            options.setWidth(settings.getInt(WIDTH));
            options.setHeight(settings.getInt(HEIGHT));
            options.setFullscreen(settings.getBoolean(FULLSCREEN));
            return options;
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
            SettingsModelSVGOptions other = (SettingsModelSVGOptions)obj;
            return new EqualsBuilder()
                    .append(m_width, other.m_width)
                    .append(m_height, other.m_height)
                    .append(m_fullscreen, other.m_fullscreen)
                    .isEquals();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                    .append(m_width)
                    .append(m_height)
                    .append(m_fullscreen)
                    .toHashCode();
        }

    }

}
