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
 *   Aug 3, 2018 (awalter): created
 */
package org.knime.js.base.node.viz.heatmap;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import org.knime.js.core.CSSUtils;

/**
 * A {@link JComponent} for displaying continuous or discrete gradients.
 *
 * @author Alison Walter, KNIME GmbH, Konstanz, Germany
 */
public class ThreeColorGradientComponent extends JComponent {

    private static final long serialVersionUID = 1L;

    private Color start;
    private Color middle;
    private Color end;
    private boolean binMode;
    private int bins;
    private Color[] discreteColors;

    /**
     * @param first
     * @param second
     * @param third
     */
    public ThreeColorGradientComponent(final Color first, final Color second, final Color third) {
        super();
        start = first;
        middle = second;
        end = third;
        this.setPreferredSize(new Dimension(700, 50));
        binMode = false;
        bins = 0;
    }

    /**
     * @return the colors
     */
    public Color[] getColors() {
        return new Color[]{start, middle, end};
    }

    /**
     * @return the colors as hex strings
     */
    public String[] getColorsAsHex() {
        return new String[]{CSSUtils.cssHexStringFromColor(start), CSSUtils.cssHexStringFromColor(middle),
            CSSUtils.cssHexStringFromColor(end)};
    }

    /**
     * @return the left most color in the gradient
     */
    public Color getFirstColor() {
        return start;
    }

    /**
     * @return the middle color in the gradient
     */
    public Color getMiddleColor() {
        return middle;
    }

    /**
     * @return the right most color in the gradient
     */
    public Color getLastColor() {
        return end;
    }

    /**
     * @param first
     * @param second
     * @param third
     */
    public void setColors(final Color first, final Color second, final Color third) {
        start = first;
        middle = second;
        end = third;
        repaint();
    }

    /**
     * @param first
     * @param second
     * @param third
     */
    public void setColors(final String first, final String second, final String third) {
        start = CSSUtils.colorFromCssHexString(first);
        middle = CSSUtils.colorFromCssHexString(second);
        end = CSSUtils.colorFromCssHexString(third);
    }

    /**
     * @param color
     */
    public void setFirstColor(final Color color) {
        start = color;
        repaint();
    }

    /**
     * @param color
     */
    public void setMiddleColor(final Color color) {
        middle = color;
        repaint();
    }

    /**
     * @param color
     */
    public void setLastColor(final Color color) {
        end = color;
        repaint();
    }

    /**
     * @param useBins
     */
    public void useBins(final boolean useBins) {
        binMode = useBins;
        repaint();
    }

    /**
     * @param numBins
     */
    public void setNumberOfBins(final int numBins) {
        bins = numBins;
        repaint();
    }

    /**
     * @return All colors in the gradient. If the gradient discrete, this includes all discrete color. But if it is
     *         continuous this is just the three main colors.
     */
    public Color[] getAllColors() {
        if (binMode) {
            return discreteColors;
        }
        return getColors();
    }

    /**
     * @return All colors in the gradient as hex (@code String}s. If the gradient discrete, this includes all discrete
     *         color. But if it is continuous this is just the three main colors.
     */
    public String[] getAllColorsHex() {
        if (!binMode) {
            return getColorsAsHex();
        }
        final String[] hexColors = new String[discreteColors.length];
        for (int i = 0; i < hexColors.length; i++) {
            hexColors[i] = CSSUtils.cssHexStringFromColor(discreteColors[i]);
        }
        return hexColors;
    }

    @Override
     public void paint(final Graphics g) {
        if (!binMode || bins <= 0 || bins > 600) {
            paintNoBins(g);
        }
        else {
            paintBins(g);
        }
     }

    // -- Helper methods --

    private void paintNoBins(final Graphics graphics) {
        final Graphics2D g2d = (Graphics2D) graphics;
        final GradientPaint firstHalf = new GradientPaint(0, 0, start, 350, 0, middle);
        final GradientPaint secondHalf = new GradientPaint(350, 0, middle, 700, 0, end);

        g2d.setPaint(firstHalf);
        g2d.fill(new Rectangle2D.Double(0, 0, 350, 50));
        g2d.setPaint(secondHalf);
        g2d.fill(new Rectangle2D.Double(350, 0, 350, 50));
    }

    private void paintBins(final Graphics graphics) {
        discreteColors = new Color[bins];

        final int binWidth = (700 / bins) - 2;
        final double percent = 1 / (bins / 2.0);
        final boolean isOdd = bins % 2 != 0;
        final int space = 2;

        int x = 0;
        double percentFirst = 0;
        double percentSecond = percent;
        int remainder = 700 % bins;

        for (int i = 0; i < bins; i++) {
            int binWidthI = binWidth;
            Color currentColor = null;
            if (remainder > 0 && i > (Math.floor(bins / 2.0) - (remainder / 2))) {
                binWidthI++;
                remainder--;
            }

            if (isOdd && i == Math.floor(bins / 2.0)) {
                currentColor = middle;
            }
            else if (i < (bins/2)) {
                currentColor = computeColor(start, middle, percentFirst);
                percentFirst+=percent;
            }
            else {
                currentColor = computeColor(middle, end, percentSecond);
                percentSecond+=percent;
            }

            paintRectangle(graphics, binWidthI, currentColor, x);
            x+=(binWidthI + space);
            discreteColors[i] = currentColor;
        }
    }

    private static void paintRectangle(final Graphics g, final int width, final Color c, final int x) {
        g.setColor(c);
        g.fillRect(x, 0, width, 50);
    }

    private static Color computeColor(final Color firstColor, final Color secondColor, final double percent) {
        final int r = (int) (firstColor.getRed() * (1 - percent) + secondColor.getRed() * percent);
        final int g = (int) (firstColor.getGreen() * (1 - percent) + secondColor.getGreen() * percent);
        final int b = (int) (firstColor.getBlue() * (1 - percent) + secondColor.getBlue() * percent);
        return new Color(r, g, b);
    }
}
