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
 *   2 Oct 2017 (albrecht): created
 */
package org.knime.js.base.node.viz.wordCloud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.knime.base.data.xml.SvgCell;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.image.ImagePortObject;
import org.knime.core.node.port.image.ImagePortObjectSpec;
import org.knime.core.node.port.inactive.InactiveBranchPortObjectSpec;
import org.knime.core.node.web.ValidationError;
import org.knime.js.core.JSONDataTable;
import org.knime.js.core.JSONDataTable.JSONDataTableRow;
import org.knime.js.core.layout.LayoutTemplateProvider;
import org.knime.js.core.layout.bs.JSONLayoutViewContent;
import org.knime.js.core.layout.bs.JSONLayoutViewContent.ResizeMethod;
import org.knime.js.core.node.AbstractSVGWizardNodeModel;

/**
 * Node model for the word cloud view
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class WordCloudViewNodeModel
    extends AbstractSVGWizardNodeModel<WordCloudViewRepresentation, WordCloudViewValue> implements LayoutTemplateProvider {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(WordCloudViewNodeModel.class);

    private final WordCloudViewConfig m_config;

    /**
     * @param viewName The name of the interactive view
     */
    protected WordCloudViewNodeModel(final String viewName) {
        super(new PortType[]{BufferedDataTable.TYPE}, new PortType[]{ImagePortObject.TYPE}, viewName);
        m_config = new WordCloudViewConfig();
        setOptionalViewWaitTime(1000l);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        DataTableSpec inSpec = (DataTableSpec)inSpecs[0];
        String wordCol = m_config.getWordColumn();
        if (wordCol != null && !inSpec.containsName(wordCol)) {
            throw new InvalidSettingsException("Selected word column '" + wordCol + "' does not exist!");
        }
        String sizeCol = m_config.getSizeColumn();
        if (sizeCol != null && !inSpec.containsName(sizeCol)) {
            throw new InvalidSettingsException("Selected size column '" + sizeCol + "' does not exist!");
        }
        PortObjectSpec imageSpec;
        if (generateImage()) {
            imageSpec = new ImagePortObjectSpec(SvgCell.TYPE);
        } else {
            imageSpec = InactiveBranchPortObjectSpec.INSTANCE;
        }
        return new PortObjectSpec[]{imageSpec};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WordCloudViewRepresentation createEmptyViewRepresentation() {
        return new WordCloudViewRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WordCloudViewValue createEmptyViewValue() {
        return new WordCloudViewValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org.knime.js.base.node.viz.wordCloud";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHideInWizard() {
        return m_config.getHideInWizard();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHideInWizard(final boolean hide) {
        m_config.setHideInWizard(hide);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationError validateViewValue(final WordCloudViewValue viewContent) {
        /* nothing to validate atm */
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveCurrentValue(final NodeSettingsWO content) {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performExecuteCreateView(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        WordCloudViewRepresentation representation = getViewRepresentation();
        if(representation.getData() == null) {
            copyConfigToView();
            BufferedDataTable table = (BufferedDataTable)inObjects[0];
            representation.setData(extractWordCloudData(table, exec));
        }
        representation.setImageGeneration(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] performExecuteCreatePortObjects(final PortObject svgImageFromView, final PortObject[] inObjects,
        final ExecutionContext exec) throws Exception {
        WordCloudViewRepresentation representation = getViewRepresentation();
        representation.setImageGeneration(false);
        return new PortObject[]{svgImageFromView};
    }

    private List<WordCloudData> extractWordCloudData(final BufferedDataTable table, final ExecutionContext exec) throws Exception {
        // TODO aggregate words
        String[] includeColumns = new String[]{m_config.getWordColumn(), m_config.getSizeColumn()};
        JSONDataTable jsonTable = JSONDataTable.newBuilder()
                .setDataTable(table)
                .setId(getTableId(0))
                .setFirstRow(1)
                .setMaxRows(m_config.getMaxWords())
                .setIncludeColumns(includeColumns)
                .extractRowColors(m_config.getUseColorProp())
                .extractRowSizes(m_config.getUseSizeProp())
                .excludeRowsWithMissingValues(true)
                .build(exec);
        Map<String, String> warnMessages = new HashMap<String, String>();
        if (table.size() > m_config.getMaxWords()) {
            String warnMessage = "Only the first " + m_config.getMaxWords() + " words are displayed.";
            setWarningMessage(warnMessage);
            warnMessages.put("knime_clipped_rows", warnMessage);
        }
        int missingValueRowsRemoved = jsonTable.numberRemovedRowsWithMissingValues();
        if (missingValueRowsRemoved > 0 && m_config.getReportMissingValues()) {
            String warnMessage = missingValueRowsRemoved + " rows were omitted due to missing values.";
            setWarningMessage(warnMessage);
            warnMessages.put("knime_missing_values", warnMessage);
        }
        if (warnMessages.size() > 0) {
            WordCloudViewRepresentation representation = getViewRepresentation();
            representation.setWarningMessages(warnMessages);
        }
        int numWords = Math.min(jsonTable.getSpec().getNumRows(), m_config.getMaxWords());
        List<WordCloudData> data = new ArrayList<WordCloudData>(numWords);
        JSONDataTableRow[] rows = jsonTable.getRows();
        int wordIndex = -1;
        if (m_config.getWordColumn() != null) {
            wordIndex = ArrayUtils.indexOf(jsonTable.getSpec().getColNames(), m_config.getWordColumn());
        }
        int sizeIndex = -1;
        if (!m_config.getUseSizeProp()) {
            sizeIndex = ArrayUtils.indexOf(jsonTable.getSpec().getColNames(), m_config.getSizeColumn());
        }
        String[] colors = jsonTable.getSpec().getRowColorValues();
        Double[] sizes = jsonTable.getSpec().getRowSizeValues();
        for(int i = 0; i < numWords; i++) {
            WordCloudData indData = new WordCloudData();
            if (m_config.getWordColumn() != null) {
                indData.setText((String)rows[i].getData()[wordIndex]);
            } else {
                indData.setText(rows[i].getRowKey());
            }
            if (m_config.getUseSizeProp()) {
                indData.setSize(sizes[i]);
            } else {
                indData.setSize(((double)rows[i].getData()[sizeIndex]));
            }
            if (m_config.getUseColorProp()) {
                indData.setColor(colors[i]);
            }
            //TODO the following property could be configurable
            indData.setOpacity(1);
            data.add(indData);
        }
        return data;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean generateImage() {
        return m_config.getGenerateImage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performReset() {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void useCurrentValueAsDefault() {
        copyValueToConfig();
    }

    private void copyConfigToView() {
        WordCloudViewRepresentation representation = getViewRepresentation();
        representation.setShowWarningsInView(m_config.getShowWarningsInView());
        representation.setResizeToWindow(m_config.getResizeToWindow());
        representation.setImageWidth(m_config.getImageWidth());
        representation.setImageHeight(m_config.getImageHeight());
        representation.setDisplayFullscreenButton(m_config.getDisplayFullscreenButton());
        representation.setDisplayRefreshButton(m_config.getDisplayRefreshButton());
        representation.setDisableAnimations(m_config.getDisableAnimations());
        representation.setUseColorProperty(m_config.getUseColorProp());
        representation.setFont(m_config.getFont());
        representation.setFontBold(m_config.getFontBold());
        representation.setEnableViewConfig(m_config.getEnableViewConfig());
        representation.setEnableTitleChange(m_config.getEnableTitleChange());
        representation.setEnableSubtitleChange(m_config.getEnableSubtitleChange());
        representation.setEnableFontSizeChange(m_config.getEnableFontSizeChange());
        representation.setEnableScaleTypeChange(m_config.getEnableScaleTypeChange());
        representation.setEnableSpiralTypeChange(m_config.getEnableSpiralTypeChange());
        representation.setEnableNumOrientationsChange(m_config.getEnableNumOrientationsChange());
        representation.setEnableAnglesChange(m_config.getEnableAnglesChange());

        WordCloudViewValue value = getViewValue();
        value.setTitle(m_config.getTitle());
        value.setSubtitle(m_config.getSubtitle());
        value.setMinFontSize(m_config.getMinFontSize());
        value.setMaxFontSize(m_config.getMaxFontSize());
        value.setFontScaleType(m_config.getFontScaleType());
        value.setSpiralType(m_config.getSpiralType());
        value.setNumOrientations(m_config.getNumOrientations());
        value.setStartAngle(m_config.getStartAngle());
        value.setEndAngle(m_config.getEndAngle());
    }

    private void copyValueToConfig() {
        WordCloudViewValue value = getViewValue();
        m_config.setTitle(value.getTitle());
        m_config.setSubtitle(value.getSubtitle());
        m_config.setMinFontSize(value.getMinFontSize());
        m_config.setMaxFontSize(value.getMaxFontSize());
        m_config.setFontScaleType(value.getFontScaleType());
        m_config.setSpiralType(value.getSpiralType());
        m_config.setNumOrientations(value.getNumOrientations());
        m_config.setStartAngle(value.getStartAngle());
        m_config.setEndAngle(value.getEndAngle());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        m_config.saveSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        (new WordCloudViewConfig()).loadSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_config.loadSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSONLayoutViewContent getLayoutTemplate() {
        JSONLayoutViewContent template = new JSONLayoutViewContent();
        if (m_config.getResizeToWindow()) {
            template.setResizeMethod(ResizeMethod.ASPECT_RATIO_16by9);
        } else {
            template.setResizeMethod(ResizeMethod.VIEW_LOWEST_ELEMENT);
        }
        return template;
    }

}
