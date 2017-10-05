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

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.knime.core.data.DataCell;
import org.knime.core.data.image.ImageContent;
import org.knime.core.data.image.ImageValue;
import org.knime.core.data.image.png.PNGImageContent;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.image.ImagePortObject;
import org.knime.core.util.node.quickform.out.ImageOutputQuickFormOutElement;
import org.knime.quickform.nodes.out.QuickFormOutNodeModel;

/**
 * Model to image output qf.
 * @author Bernd Wiswedel, KNIME.com, Zurich, Switzerland
 */
class ImageOutputQuickFormOutNodeModel extends QuickFormOutNodeModel<ImageOutputQuickFormOutConfiguration> {

    private ImageOutputQuickFormOutElement m_element;

    /**
     *
     */
    ImageOutputQuickFormOutNodeModel() {
        super(new PortType[] {ImagePortObject.TYPE}, new PortType[] {});
    }

    /** {@inheritDoc} */
    @Override
    public ImageOutputQuickFormOutElement getQuickFormElement() {
        return m_element;
    }

    /** {@inheritDoc} */
    @Override
    protected ImageOutputQuickFormOutConfiguration createConfiguration() {
        return new ImageOutputQuickFormOutConfiguration();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        ImageOutputQuickFormOutConfiguration cfg = getConfiguration();
        if (cfg == null) {
            throw new InvalidSettingsException("No configuration available");
        }
        return new PortObjectSpec[] {};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] execute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        ImageOutputQuickFormOutConfiguration cfg = getConfiguration();
        ImagePortObject img = (ImagePortObject)inObjects[0];
        DataCell dataCell = img.toDataCell();
        if (!(dataCell instanceof ImageValue)) {
            throw new IllegalStateException("Expected image but got " + dataCell);
        }
        ImageValue imgValue = (ImageValue)dataCell;
        ImageContent imageCnt = imgValue.getImageContent();
        byte[] byteCnt;
        if (imageCnt instanceof PNGImageContent) {
            byteCnt = ((PNGImageContent)imageCnt).getByteArray();
        } else {
            throw new InvalidSettingsException("Unsupported image type: "
                    + imageCnt.getClass().getName() + " (expected PNG)");
        }
        m_element = createQuickFormElement(cfg, byteCnt);
        return new PortObject[] {};
    }

    /**
     * @param cfg
     * @param byteCnt
     * @return
     */
    private static ImageOutputQuickFormOutElement createQuickFormElement(final ImageOutputQuickFormOutConfiguration cfg,
        final byte[] byteCnt) {
        return new ImageOutputQuickFormOutElement(cfg.getLabel(), cfg.getDescription(), cfg.getWeight(),
           byteCnt, cfg.isEnlargeOnClick(), cfg.getMaxWidth(), cfg.getMaxHeight());
    }

    /** {@inheritDoc} */
    @Override
    protected void peekFlowVariable() throws InvalidSettingsException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File nodeInternDir, final ExecutionMonitor exec) throws IOException,
        CanceledExecutionException {
        File f = new File(nodeInternDir, "image.png");
        if (!f.exists()) {
            // in 2.8.2 and before there was no file saved. This is not a problem in the desktop but in
            // the KNIME WebPortal ... we leave it to fail then.
            return;
        }
        byte[] readFileToByteArray = FileUtils.readFileToByteArray(f);
        m_element = createQuickFormElement(getConfiguration(), readFileToByteArray);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File nodeInternDir, final ExecutionMonitor exec) throws IOException,
        CanceledExecutionException {
        File f = new File(nodeInternDir, "image.png");
        FileUtils.writeByteArrayToFile(f, m_element.getImageContent());
    }


}
