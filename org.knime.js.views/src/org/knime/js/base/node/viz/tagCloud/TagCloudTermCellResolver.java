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
 *   25 Oct 2017 (albrecht): created
 */
package org.knime.js.base.node.viz.tagCloud;

import java.awt.Color;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.knime.base.node.util.DataArray;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.DataValue;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.StringValue;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.util.DataValueColumnFilter;
import org.knime.js.core.CSSUtils;

/**
 * Helper class to resolve term values, which are optional dependencies using reflection.
 * The class has a few static helper functions and concrete objects of this class use a builder
 * pattern to configure tag extraction and store information about extraction, once complete.
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("unchecked")
public class TagCloudTermCellResolver {

    private static boolean SUPPORTS_TERMS = false;
    private static Class<DataValue> termValueClass;
    private static Class<?> termClass;

    private boolean m_aggregateStrings = false;
    private boolean m_ignoreTermTags = false;
    private boolean m_useRowIds = false;
    private boolean m_useSizeProperty = false;
    private boolean m_extractRowColors = false;
    private String m_wordColumn;
    private String m_sizeColumn;
    private int m_maxRows = Integer.MAX_VALUE;
    private int m_numMissing = 0;
    private boolean m_clippingOccured = false;


    static {
        try {
            termValueClass = (Class<DataValue>)Class.forName("org.knime.ext.textprocessing.data.TermValue");
            termClass = Class.forName("org.knime.ext.textprocessing.data.Term");
            SUPPORTS_TERMS = true;
        } catch (Throwable throwable) {
            SUPPORTS_TERMS = false;
        }
    }

    static boolean supportsTermCells() {
        return SUPPORTS_TERMS;
    }

    static DataValueColumnFilter getWordColumnFilter() {
        if (SUPPORTS_TERMS) {
            return new DataValueColumnFilter(StringValue.class, termValueClass);
        } else {
            return new DataValueColumnFilter(StringValue.class);
        }
    }

    static boolean isTermValue(final DataColumnSpec spec) {
        if (!SUPPORTS_TERMS) {
            return false;
        }
        DataType type = spec.getType();
        return type.isCompatible(termValueClass);
    }

    TagCloudTermCellResolver() {
        /* package constructor */
    }



    TagCloudTermCellResolver aggregateStrings(final boolean aggregate) {
        m_aggregateStrings = aggregate;
        return this;
    }

    TagCloudTermCellResolver ignoreTermTags(final boolean ignore) {
        m_ignoreTermTags = ignore;
        return this;
    }

    TagCloudTermCellResolver useRowIds(final boolean use) {
        m_useRowIds = use;
        return this;
    }

    TagCloudTermCellResolver useSizeProperty(final boolean use) {
        m_useSizeProperty = use;
        return this;
    }

    TagCloudTermCellResolver extractRowColors(final boolean extract) {
        m_extractRowColors = extract;
        return this;
    }

    TagCloudTermCellResolver setWordColumn(final String col) {
        m_wordColumn = col;
        return this;
    }

    TagCloudTermCellResolver setSizeColumn(final String col) {
        m_sizeColumn = col;
        return this;
    }

    TagCloudTermCellResolver setMaxWords(final int max) {
        m_maxRows = max;
        return this;
    }

    int getMissingValueCount() {
        return m_numMissing;
    }

    boolean isClippingOccured() {
        return m_clippingOccured;
    }

    List<TagCloudData> extractWordCloudData(final DataArray data, final ExecutionContext exec) throws IllegalArgumentException {
        // sanity check
        if (!m_useRowIds && StringUtils.isEmpty(m_wordColumn)) {
            throw new IllegalArgumentException("No word column specified!");
        }
        if (!m_useSizeProperty && StringUtils.isEmpty(m_sizeColumn)) {
            throw new IllegalArgumentException("No size column specified!");
        }

        // initializing indices and types
        DataTableSpec spec = data.getDataTableSpec();
        boolean isTermType = false;
        int termColIndex = -1;
        if (!m_useRowIds && StringUtils.isNoneEmpty(m_wordColumn)) {
            termColIndex = spec.findColumnIndex(m_wordColumn);
            isTermType = isTermValue(spec.getColumnSpec(termColIndex));
        }
        int sizeColIndex = -1;
        if (StringUtils.isNoneEmpty(m_sizeColumn)) {
            sizeColIndex = data.getDataTableSpec().findColumnIndex(m_sizeColumn);
        }

        ExecutionContext creationContext = exec.createSubExecutionContext(0.8);
        creationContext.setMessage("Extracting tag cloud data...");

        //initializing generic map to hold term and string values which can be used for aggregation
        Map<Object, TagCloudData> map = new HashMap<Object, TagCloudData>();
        int rowID = 0;
        for (final DataRow row : data) {
            //ignore missing cells
            if ((termColIndex > -1 && row.getCell(termColIndex).isMissing())
                    || sizeColIndex > -1 && row.getCell(sizeColIndex).isMissing()) {
                m_numMissing++;
                continue;
            }
            Object key;
            String word;
            String rowKey = row.getKey().getString();
            if (m_useRowIds) {
                word = rowKey;
                key = word;
            } else {
                DataCell wordCell = row.getCell(termColIndex);
                word = ((StringValue)wordCell).getStringValue();
                key = word;
                if (isTermType) {
                    try {
                        Method getTermValue = termValueClass.getMethod("getTermValue");
                        Object term = getTermValue.invoke(wordCell);
                        Method getText = termClass.getMethod("getText");
                        word = (String)getText.invoke(term);
                        key = term;
                        if (m_ignoreTermTags) {
                            Constructor<?> cons = termClass.getConstructor(List.class, List.class, Boolean.class);
                            Method getWords = termValueClass.getMethod("getWords");
                            key = cons.newInstance(getWords.invoke(term), null, true);
                        }
                    } catch (Exception e) { /* do nothing */ }
                }
            }

            double size = 0;
            if (m_useSizeProperty) {
                size = spec.getRowSizeFactor(row);
            } else {
                DataCell sizeCell = row.getCell(sizeColIndex);
                size = ((DoubleValue)sizeCell).getDoubleValue();
            }

            if (m_aggregateStrings && map.containsKey(key)) {
                TagCloudData wcd = map.get(key);
                wcd.setSize(wcd.getSize() + size);
                Set<String> rowKeySet = new HashSet<String>(Arrays.asList(wcd.getRowIDs()));
                rowKeySet.add(rowKey);
                wcd.setRowIDs(rowKeySet.toArray(new String[0]));
            } else {
                TagCloudData wcd = new TagCloudData();
                wcd.setRowIDs(new String[]{rowKey});
                wcd.setText(word);
                wcd.setSize(size);
                if (m_extractRowColors) {
                    Color c = spec.getRowColor(row).getColor();
                    wcd.setColor(CSSUtils.cssHexStringFromColor(c));
                }
                map.put(key, wcd);
            }
            creationContext.setProgress(++rowID / data.size());
        }

        exec.setMessage("Sorting tag cloud data...");
        //sort and top n
        List<TagCloudData> list = map.entrySet()
            .stream()
            .sorted(Map.Entry.<Object, TagCloudData>comparingByValue((final TagCloudData v1, final TagCloudData v2) -> Double.compare(v2.getSize(), v1.getSize())))
            .limit(m_maxRows)
            .map(Map.Entry::getValue)
            .collect(Collectors.toList());
        if (list.size() < map.size()) {
            m_clippingOccured = true;
        }
        exec.setProgress(1);
        return list;
    }

}
