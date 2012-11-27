/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.calibrator;

import fr.jmmc.jmal.ALX;
import fr.jmmc.jmcs.data.preference.PreferencesException;
import fr.jmmc.sclgui.preference.Preferences;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements calibrators model comparison
 * @author bourgesl
 */
public final class DiffCalibratorsModel {

    /** Logger */
    private final static Logger _logger = LoggerFactory.getLogger(DiffCalibratorsModel.class.getName());
    /** double formatter for number values */
    private final static NumberFormat df3 = new DecimalFormat("0.0##E0");
    /* members */
    /** calibrators model of first file to compare (reference) */
    private final CalibratorsModel calibratorsModelLeft;
    /** calibrators model of second file to compare */
    private final CalibratorsModel calibratorsModelRight;
    /** calibrators model of the differences */
    private final CalibratorsModel calibratorsModelDiff;

    /**
     * Public constructor
     * @param calibratorsModelLeft
     * @param calibratorsModelRight
     * @param calibratorsModelDiff 
     */
    public DiffCalibratorsModel(final CalibratorsModel calibratorsModelLeft, final CalibratorsModel calibratorsModelRight, final CalibratorsModel calibratorsModelDiff) {
        this.calibratorsModelLeft = calibratorsModelLeft;
        this.calibratorsModelRight = calibratorsModelRight;
        this.calibratorsModelDiff = calibratorsModelDiff;
    }

    /**
     * Return the calibrators model of the given mode
     * @param mode -1 (left), 1 (right), 2 (diff)
     * @return calibrators model
     */
    private CalibratorsModel getCalibratorsModel(final int mode) {
        switch (mode) {
            default:
                return null;
            case 1:
                return calibratorsModelRight;
            case -1:
                return calibratorsModelLeft;
            case 2:
                return calibratorsModelDiff;
        }
    }

    /**
     * Find rowIdx or otherRowIdx values (see given property) in the given calibrators model
     * @param selectedIndices selected filtered star indices
     * @param mode -1 (left), 1 (right), 2 (diff)
     * @param property rowIdx or otherRowIdx
     * @return rowIdx or otherRowIdx values or null
     */
    public List<Integer> getFilteredRows(final int[] selectedIndices, final int mode, final String property) {
        if (selectedIndices == null) {
            return null;
        }
        final CalibratorsModel calModel = getCalibratorsModel(mode);
        if (calModel != null) {
            final StarList filteredStarList = calModel.getFilteredStarList();

            final int colIdx = filteredStarList.getColumnIdByName(property);

            if (colIdx != -1) {
                final int len = selectedIndices.length;
                final List<Integer> rowIndexes = new ArrayList<Integer>(len);
                Integer idx;

                for (int i = 0; i < len; i++) {
                    idx = filteredStarList.get(selectedIndices[i]).get(colIdx).getInteger();

                    if (idx != null) {
                        rowIndexes.add(idx);
                    }
                }

                if (_logger.isDebugEnabled()) {
                    _logger.debug("rowIndexes: {}", rowIndexes);
                }
                if (!rowIndexes.isEmpty()) {
                    return rowIndexes;
                }
            }
        }
        return null;
    }

    /**
     * Find row indexes corresponding to rowIdx values in the given calibrators model
     * @param rowIdxValues row idx values
     * @param mode -1 (left), 1 (right), 2 (diff)
     * @return row indexes or null
     */
    public List<Integer> findMatchingFilteredRows(final List<Integer> rowIdxValues, final int mode) {
        if (rowIdxValues == null) {
            return null;
        }
        final CalibratorsModel calModel = getCalibratorsModel(mode);
        if (calModel != null) {
            final Map<Integer, Integer> indexMap = calModel.getFilteredStarIndexMap();

            if (indexMap != null) {
                final List<Integer> matchIndexes = new ArrayList<Integer>(rowIdxValues.size());
                Integer found;

                for (Integer rowIdx : rowIdxValues) {
                    found = indexMap.get(rowIdx);

                    if (found != null) {
                        matchIndexes.add(found);
                    }
                }

                if (_logger.isDebugEnabled()) {
                    _logger.debug("matchIndexes: {}", matchIndexes);
                }
                return matchIndexes;
            }
        }
        return null;
    }

    /**
     * Compare the two given files
     * @param fileLeft first file to compare (reference)
     * @param fileRight second file to compare
     */
    public void diff(final File fileLeft, final File fileRight) {
        _logger.info("-------------------------------------------------------------------------------");
        _logger.warn("diff: fileLeft= '{}' - fileRight= '{}'", fileLeft, fileRight);

        calibratorsModelLeft.parseVOTable(fileLeft, false); // sync

        calibratorsModelRight.parseVOTable(fileRight, false); // sync

        _logger.info("-------------------------------------------------------------------------------");

        _logger.warn("Left  : scenario {}", calibratorsModelLeft._brightScenarioFlag ? "bright" : "faint");
        _logger.warn("Left  : band {}", calibratorsModelLeft._magnitudeBand);

        _logger.warn("Right : scenario {}", calibratorsModelRight._brightScenarioFlag ? "bright" : "faint");
        _logger.warn("Right : band {}", calibratorsModelRight._magnitudeBand);

        // Fix band and scenario:
        if (calibratorsModelRight._brightScenarioFlag != calibratorsModelLeft._brightScenarioFlag) {
            calibratorsModelRight._brightScenarioFlag = calibratorsModelLeft._brightScenarioFlag;
        }
        if (!calibratorsModelRight._magnitudeBand.equals(calibratorsModelLeft._magnitudeBand)) {
            calibratorsModelRight._magnitudeBand = calibratorsModelLeft._magnitudeBand;
        }

        // TODO: compare also paramSet (query params)

        // Get Star lists:
        final StarList starListLeft = calibratorsModelLeft.getOriginalStarList();
        final StarList starListRight = calibratorsModelRight.getOriginalStarList();

        _logger.info("-------------------------------------------------------------------------------");

        // compare stars and compute otherRowIdx property on both lists:
        final int matchs = crossMatchRaDec(starListLeft, starListRight);

        _logger.info("-------------------------------------------------------------------------------");

        // compare star list meta data:
        final StarListMeta diffStarListMetaData = processStarListMeta(starListLeft.getMetaData(), starListRight.getMetaData());

        // create a new diff star list (common properties only):
        final StarList diffStarList = new StarList(diffStarListMetaData);
        if (matchs > 0) {
            // prepare list:
            diffStarList.ensureCapacity(matchs);
        }

        compare(starListLeft, starListRight, diffStarList);

        // Define band and scenario to fix JTable display:
        calibratorsModelDiff._brightScenarioFlag = calibratorsModelLeft._brightScenarioFlag;
        calibratorsModelDiff._magnitudeBand = calibratorsModelLeft._magnitudeBand;

        _logger.info("-------------------------------------------------------------------------------");

        // update models because otherRowIdx property added:
        calibratorsModelLeft.updateModel(starListLeft);
        calibratorsModelRight.updateModel(starListRight);

        // display computed diff model:
        calibratorsModelDiff.updateModel(diffStarList);

        // Update views / GUI:
        String propName;
        final StringBuilder sb = new StringBuilder(1024);
        for (int i = 0, count = diffStarListMetaData.getPropertyCount(); i < count; i++) {
            propName = diffStarListMetaData.getPropertyName(i);

            if (propName.equals(StarList.RADegColumnName)
                    || propName.equals(StarList.DEDegColumnName)
                    || propName.equals(StarList.RowIdxColumnName)
                    || propName.equals(StarList.OtherRowIdxColumnName)) {
                // skip
                continue;
            }
            sb.append(propName).append(' ');
        }
        sb.insert(0, "rowIdx otherRowIdx RAdeg DEdeg ");

        final String detailedProperties = sb.toString();

        try {
            final String prefKey = "view.columns.detailed." + (calibratorsModelLeft._brightScenarioFlag ? "bright" : "faint")
                    + '.' + calibratorsModelLeft._magnitudeBand;

            Preferences.getInstance().setPreference(prefKey, detailedProperties);
        } catch (PreferencesException pe) {
            _logger.warn("setPreference exception:", pe);
        }
        
        _logger.info("-------------------------------------------------------------------------------");
    }

    /**
     * Compare the two given star list using meta data provided by the diff star list (shared star properties)
     * @param starListLeft first star list
     * @param starListRight second star list
     * @param diffStarList output list (diff)
     * @return 0
     */
    private int compare(final StarList starListLeft, final StarList starListRight, final StarList diffStarList) {

        final boolean isLogDebug = _logger.isDebugEnabled();

        final long start = System.nanoTime();

        final StarListMeta starListMetaLeft = starListLeft.getMetaData();
        final StarListMeta starListMetaRight = starListRight.getMetaData();
        final StarListMeta diffStarListMetaData = diffStarList.getMetaData();

        final int propCount = diffStarListMetaData.getPropertyCount();
        // TODO: use a preference to define columns to ignore (property mask)
        // ignore rowIdx / otherRowIdx properties
        final int diffCount = diffStarListMetaData.getPropertyIndexByName(StarList.RowIdxColumnName);

        // star property mapping:
        final int[] mapIdxLeft = new int[diffCount];
        final int[] mapIdxRight = new int[diffCount];

        // statistics:
        int matchs = 0;
        int diffValues = 0;
        int onlyLeft = 0;
        int onlyRight = 0;
        final int[] mapIdxDiffValues = new int[diffCount];
        final int[] mapIdxOnlyLeft = new int[diffCount];
        final int[] mapIdxOnlyRight = new int[diffCount];
        final double[] mapIdxDiffSumValues = new double[diffCount];
        final int[] mapIdxRelDiffValues = new int[diffCount];
        final double[] mapIdxRelDiffSumValues = new double[diffCount];
        int diffOrigins = 0;
        final int[] mapIdxDiffOrigins = new int[diffCount];
        int diffConfidences = 0;
        final int[] mapIdxDiffConfidences = new int[diffCount];

        for (int i = 0; i < diffCount; i++) {
            final String propName = diffStarListMetaData.getPropertyName(i);

            mapIdxLeft[i] = starListMetaLeft.getPropertyIndexByName(propName);
            mapIdxRight[i] = starListMetaRight.getPropertyIndexByName(propName);

            mapIdxDiffValues[i] = 0;
            mapIdxOnlyLeft[i] = 0;
            mapIdxOnlyRight[i] = 0;
            mapIdxDiffSumValues[i] = 0d;
            mapIdxRelDiffValues[i] = 0;
            mapIdxRelDiffSumValues[i] = 0d;
            mapIdxDiffOrigins[i] = 0;
            mapIdxDiffConfidences[i] = 0;
        }

        Integer leftRowIdx, rightRowIdx;

        final int rowIdxIdxLeft = starListLeft.getColumnIdByName(StarList.RowIdxColumnName);
        final int otherRowIdxIdxLeft = starListLeft.getColumnIdByName(StarList.OtherRowIdxColumnName);

        // special case for deleted flag column as it is editable:
        // => always create a new editable Star Property
        final int deletedFlagColumnID = diffStarList.getDeletedFlagColumnID();

        final Map<String, String> originValues = new HashMap<String, String>(32);
        final Map<String, String> confidenceValues = new HashMap<String, String>(16);

        // Idea: add rank property ?
        StarPropertyMeta propMeta;
        Class<?> classType;
        List<StarProperty> starRight;
        List<StarProperty> star;
        StarProperty starPropertyLeft;
        StarProperty starPropertyRight;
        StarProperty starProperty;
        Object propertyValue;
        String origin, originValue, confidence, confidenceValue;
        boolean isSet;

        int res;
        final DoubleDiff dblDiff = new DoubleDiff();
        Double dblLeft, dblRight;
        String strLeft, strRight;
        Boolean boolLeft, boolRight;

        // for all stars in star list left:
        for (List<StarProperty> starLeft : starListLeft) {

            leftRowIdx = starLeft.get(rowIdxIdxLeft).getInteger();
            rightRowIdx = starLeft.get(otherRowIdxIdxLeft).getInteger();

            if (rightRowIdx != null) {
                // match found: create a new 'diff' star:
                star = new Vector<StarProperty>(propCount);

                // match found:
                starRight = starListRight.get(rightRowIdx.intValue() - 1);

                for (int i = 0; i < diffCount; i++) {
                    propMeta = diffStarListMetaData.getPropertyMeta(i);
                    starPropertyLeft = starLeft.get(mapIdxLeft[i]);
                    starPropertyRight = starRight.get(mapIdxRight[i]);

                    propertyValue = null;

                    if (i == deletedFlagColumnID) {
                        // always create a new editable Star property:
                        propertyValue = Boolean.FALSE;
                        isSet = true;
                    } else {

                        classType = propMeta.getClassType();

                        // value:
                        if (classType == Double.class) {
                            dblLeft = starPropertyLeft.getDouble();
                            dblRight = starPropertyRight.getDouble();

                            res = compareDouble(dblLeft, dblRight, dblDiff);
                            switch (res) {
                                case 0:
                                default:
                                    break;
                                case -1:
                                    onlyRight++;
                                    mapIdxOnlyRight[i]++;

                                    propertyValue = dblRight;
                                    break;
                                case 1:
                                    onlyLeft++;
                                    mapIdxOnlyLeft[i]++;

                                    propertyValue = dblLeft;
                                    break;
                                case 2:
                                    diffValues++;
                                    mapIdxDiffValues[i]++;

                                    propertyValue = Double.valueOf(dblDiff.diff);
                                    mapIdxDiffSumValues[i] += Math.abs(dblDiff.diff);

                                    if (!Double.isNaN(dblDiff.relDiff)) {
                                        mapIdxRelDiffValues[i]++;
                                        mapIdxRelDiffSumValues[i] += Math.abs(dblDiff.relDiff);
                                    }
                                    break;
                            }
                        } else if (classType == String.class) {
                            strLeft = starPropertyLeft.getString();
                            strRight = starPropertyRight.getString();

                            res = compareObject(strLeft, strRight);

                            switch (res) {
                                case 0:
                                default:
                                    break;
                                case -1:
                                    onlyRight++;
                                    mapIdxOnlyRight[i]++;

                                    propertyValue = "~ | " + strRight;
                                    break;
                                case 1:
                                    onlyLeft++;
                                    mapIdxOnlyLeft[i]++;

                                    propertyValue = strLeft + " | ~";
                                    break;
                                case 2:
                                    diffValues++;
                                    mapIdxDiffValues[i]++;

                                    propertyValue = strLeft + " | " + strRight;
                                    break;
                            }
                        } else if (classType == Boolean.class) {
                            boolLeft = starPropertyLeft.getBoolean();
                            boolRight = starPropertyRight.getBoolean();

                            res = compareObject(boolLeft, boolRight);

                            switch (res) {
                                case 0:
                                default:
                                    break;
                                case -1:
                                    onlyRight++;
                                    mapIdxOnlyRight[i]++;

                                    propertyValue = boolRight;
                                    break;
                                case 1:
                                    onlyLeft++;
                                    mapIdxOnlyLeft[i]++;

                                    propertyValue = boolLeft;
                                    break;
                                case 2:
                                    diffValues++;
                                    mapIdxDiffValues[i]++;

                                    propertyValue = Boolean.FALSE;
                                    break;
                            }
                        } else {
                            _logger.warn("unsupported data type [{}]", classType);
                        }
                        if (propertyValue == null) {
                            isSet = false;
                            matchs++;
                        } else {
                            isSet = true;
                        }
                    }

                    // origin:
                    strLeft = starPropertyLeft.getOrigin();
                    strRight = starPropertyRight.getOrigin();

                    if (strLeft.equals(strRight)) {
                        // use value for colors:
                        origin = starPropertyLeft.hasOrigin() ? strLeft : null;
                    } else {
                        origin = ((strLeft.length() != 0) ? strLeft : "~") + " | " + ((strRight.length() != 0) ? strRight : "~");

                        isSet = true;
                        diffOrigins++;
                        mapIdxDiffOrigins[i]++;

                        originValue = originValues.get(origin);
                        if (originValue == null) {
                            // cache origin:
                            originValues.put(origin, origin);
                        } else {
                            // use shared instance
                            origin = originValue;
                        }
                    }

                    // confidence:
                    strLeft = starPropertyLeft.getConfidence();
                    strRight = starPropertyRight.getConfidence();

                    if (strLeft.equals(strRight)) {
                        // use value for colors:
                        confidence = starPropertyLeft.hasConfidence() ? strLeft : null;
                    } else {
                        confidence = ((strLeft.length() != 0) ? strLeft : "~") + " | " + ((strRight.length() != 0) ? strRight : "~");

                        isSet = true;
                        diffConfidences++;
                        mapIdxDiffConfidences[i]++;

                        confidenceValue = confidenceValues.get(confidence);
                        if (confidenceValue == null) {
                            // cache confidence:
                            confidenceValues.put(confidence, confidence);
                        } else {
                            // use shared instance
                            confidence = confidenceValue;
                        }
                    }

                    if (isLogDebug) {
                        _logger.debug("Property[{}][{}]: {} [{}][{}]", i, propMeta.getName(), propertyValue, origin, confidence);
                    }

                    /*
                     * Create a new StarProperty instance from the retrieved value, origin and confidence.
                     */
                    starProperty = (isSet) ? new StarProperty(propertyValue, origin, confidence) : StarProperty.EMPTY_STAR_PROPERTY;

                    // Add the newly created star property to the star property list
                    star.add(starProperty);
                }

                // add rowIdx:
                star.add(new StarProperty(leftRowIdx));

                // add otherRowIdx:
                star.add(new StarProperty(rightRowIdx));

                // Store each diff row as a list of star properties
                diffStarList.add(star);
            }
        }

        if (_logger.isInfoEnabled()) {
            _logger.info("compare: {} matchs, {} left only, {} right only - diffs: {} values, {} origins, {} confidences - duration = {} ms.",
                    matchs, onlyLeft, onlyRight, diffValues, diffOrigins, diffConfidences, 1e-6d * (System.nanoTime() - start));

            _logger.info("-------------------------------------------------------------------------------");
            _logger.info("compare: diff properties with left/right only:");
            int diffProperties = 0;
            for (int i = 0; i < diffCount; i++) {
                if (mapIdxOnlyLeft[i] != 0 || mapIdxOnlyRight[i] != 0 || mapIdxDiffValues[i] != 0
                        || mapIdxDiffOrigins[i] != 0 || mapIdxDiffConfidences[i] != 0) {
                    diffProperties++;

                    propMeta = diffStarListMetaData.getPropertyMeta(i);

                    if (mapIdxDiffSumValues[i] != 0d) {
                        if (mapIdxRelDiffSumValues[i] != 0d) {
                            _logger.info("diff [{}]: {} left only, {} right only, {} values - {} origins - {} confidences - sum: {}, mean: {} - rel sum: {}, mean: {} [{} values]",
                                    propMeta.getName(), mapIdxOnlyLeft[i], mapIdxOnlyRight[i], mapIdxDiffValues[i], mapIdxDiffOrigins[i], mapIdxDiffConfidences[i],
                                    df3.format(mapIdxDiffSumValues[i]), df3.format(mapIdxDiffSumValues[i] / mapIdxDiffValues[i]),
                                    df3.format(mapIdxRelDiffSumValues[i]), df3.format(mapIdxRelDiffSumValues[i] / mapIdxRelDiffValues[i]), mapIdxRelDiffValues[i]);
                        } else {
                            _logger.info("diff [{}]: {} left only, {} right only, {} values - {} origins - {} confidences - sum: {}, mean: {}",
                                    propMeta.getName(), mapIdxOnlyLeft[i], mapIdxOnlyRight[i], mapIdxDiffValues[i], mapIdxDiffOrigins[i], mapIdxDiffConfidences[i],
                                    df3.format(mapIdxDiffSumValues[i]), df3.format(mapIdxDiffSumValues[i] / mapIdxDiffValues[i]));
                        }
                    } else {
                        _logger.info("diff [{}]: {} left only, {} right only, {} values - {} origins - {} confidences",
                                propMeta.getName(), mapIdxOnlyLeft[i], mapIdxOnlyRight[i], mapIdxDiffValues[i], mapIdxDiffOrigins[i], mapIdxDiffConfidences[i]);
                    }
                }
            }
            _logger.info("compare: {} diff properties", diffProperties);
            _logger.info("-------------------------------------------------------------------------------");
            _logger.info("compare: diff properties with diff only:");
            diffProperties = 0;
            for (int i = 0; i < diffCount; i++) {
                if (mapIdxDiffValues[i] != 0) {
                    diffProperties++;

                    propMeta = diffStarListMetaData.getPropertyMeta(i);

                    if (mapIdxDiffSumValues[i] != 0d) {
                        if (mapIdxRelDiffSumValues[i] != 0d) {
                            _logger.info("diff [{}]: {} values - {} origins - {} confidences - sum: {}, mean: {} - rel sum: {}, mean: {} [{} values]",
                                    propMeta.getName(), mapIdxDiffValues[i], mapIdxDiffOrigins[i], mapIdxDiffConfidences[i],
                                    df3.format(mapIdxDiffSumValues[i]), df3.format(mapIdxDiffSumValues[i] / mapIdxDiffValues[i]),
                                    df3.format(mapIdxRelDiffSumValues[i]), df3.format(mapIdxRelDiffSumValues[i] / mapIdxRelDiffValues[i]), mapIdxRelDiffValues[i]);
                        } else {
                            _logger.info("diff [{}]: {} values - {} origins - {} confidences - sum: {}, mean: {}",
                                    propMeta.getName(), mapIdxDiffValues[i], mapIdxDiffOrigins[i], mapIdxDiffConfidences[i],
                                    df3.format(mapIdxDiffSumValues[i]), df3.format(mapIdxDiffSumValues[i] / mapIdxDiffValues[i]));
                        }
                    } else {
                        _logger.info("diff [{}]: {} values - {} origins - {} confidences",
                                propMeta.getName(), mapIdxDiffValues[i], mapIdxDiffOrigins[i], mapIdxDiffConfidences[i]);
                    }
                }
            }
            _logger.info("compare: {} diff properties", diffProperties);
        }

        return diffValues;
    }

    /**
     * Compare Object instances
     * @param objLeft first Object
     * @param objRight second Object
     * @return 0 if equals; 1 if only left; -1 if only right; 2 if both defined but different
     */
    public static int compareObject(final Object objLeft, final Object objRight) {
        if (objLeft == null) {
            if (objRight == null) {
                return 0;
            }
            return -1;
        }
        if (objRight == null) {
            return 1;
        }
        if (objLeft.equals(objRight)) {
            return 0;
        }
        return 2;
    }

    /**
     * Compare Double instances
     * @param dblLeft first Double
     * @param dblRight second Double
     * @param dblDiff diff holder
     * @return 0 if equals; 1 if only left; -1 if only right; 2 if both defined but different
     */
    private static int compareDouble(final Double dblLeft, final Double dblRight, final DoubleDiff dblDiff) {
        if (dblLeft == null) {
            if (dblRight == null) {
                return 0;
            }
            return -1;
        }
        if (dblRight == null) {
            return 1;
        }
        final double left = dblLeft.doubleValue();
        final double diff = left - dblRight.doubleValue();

        // ignore too small diffs:
        if (Math.abs(diff) < 1e-9d) {
            return 0;
        }

        if (Math.abs(left) < 1e-9d) {
            // left value is too small (divide by zero) so consider relDiff equals to 0 or NaN ?
            dblDiff.set(diff, Double.NaN);
        } else {
            dblDiff.set(diff, diff / left);
        }
        return 2;
    }

    /**
     * Double Difference values
     */
    private static class DoubleDiff {
        /* members */

        /** absolute difference */
        double diff;
        /** relative difference (left) */
        double relDiff;

        /**
         * Define the absolute and relative difference 
         * @param diff absolute difference
         * @param relDiff relative difference
         */
        void set(final double diff, final double relDiff) {
            this.diff = diff;
            this.relDiff = relDiff;
        }
    }

    /**
     * Process star list meta data instances to get shared properties and log missing properties
     * @param starListMetaLeft first star list meta data
     * @param starListMetaRight second star list meta data
     * @return new StarListMeta containing shared properties
     */
    private StarListMeta processStarListMeta(final StarListMeta starListMetaLeft, final StarListMeta starListMetaRight) {
        final int leftPropCount = starListMetaLeft.getPropertyCount();
        final int rightPropCount = starListMetaRight.getPropertyCount();

        final StarListMeta diffStarListMetaData = new StarListMeta(Math.max(leftPropCount, rightPropCount));

        final StarListMeta missingStarListMetaLeft = new StarListMeta(10);
        final StarListMeta missingStarListMetaRight = new StarListMeta(10);

        for (int i = 0; i < leftPropCount; i++) {
            final StarPropertyMeta propMeta = starListMetaLeft.getPropertyMeta(i);

            if (starListMetaRight.getPropertyIndexByName(propMeta.getName()) == -1) {
                // property present only in star list left:
                missingStarListMetaRight.addPropertyMeta(propMeta);
            } else {
                // property present in both star list:
                diffStarListMetaData.addPropertyMeta(propMeta);
            }
        }

        for (int i = 0; i < rightPropCount; i++) {
            final StarPropertyMeta propMeta = starListMetaRight.getPropertyMeta(i);

            if (starListMetaLeft.getPropertyIndexByName(propMeta.getName()) == -1) {
                // property present only in star list right:
                missingStarListMetaLeft.addPropertyMeta(propMeta);
            }
        }

        if (_logger.isDebugEnabled()) {
            _logger.debug("Shared properties: {}", diffStarListMetaData);
        }

        if (missingStarListMetaLeft.getPropertyCount() != 0) {
            _logger.info("Missing properties on left side : {}", missingStarListMetaLeft);
        }
        if (missingStarListMetaRight.getPropertyCount() != 0) {
            _logger.info("Missing properties on right side: {}", missingStarListMetaRight);
        }

        return diffStarListMetaData;
    }

    /**
     * Compare both star list using crossmatch on RA/DEC and fills the otherRowIdx property of each star list (matchs)
     * @param starListLeft first star list
     * @param starListRight second star list
     * @return -1 if failure or number of matches
     */
    private int crossMatchRaDec(final StarList starListLeft, final StarList starListRight) {
        final double criteriaPos = 2d * ALX.ARCSEC_IN_DEGREES;

        final boolean isLogDebug = _logger.isDebugEnabled();

        final long start = System.nanoTime();

        int matchs = 0;
        int onlyLeft = 0;
        int onlyRight = 0;

        final StarListMeta starListMetaLeft = starListLeft.getMetaData();
        final StarListMeta starListMetaRight = starListRight.getMetaData();

        // Add otherRowIdx property:
        starListMetaLeft.addPropertyMeta(new StarPropertyMeta(StarList.OtherRowIdxColumnName, Integer.class, "row index in other list", "ID_NUMBER", "", ""));
        starListMetaRight.addPropertyMeta(new StarPropertyMeta(StarList.OtherRowIdxColumnName, Integer.class, "row index in other list", "ID_NUMBER", "", ""));

        final int nPropLeft = starListMetaLeft.getPropertyCount();
        final int nPropRight = starListMetaRight.getPropertyCount();

        final int rowIdxIdxLeft = starListLeft.getColumnIdByName(StarList.RowIdxColumnName);
        final int raDegIdxLeft = starListLeft.getColumnIdByName(StarList.RADegColumnName);
        final int decDegIdxLeft = starListLeft.getColumnIdByName(StarList.DEDegColumnName);

        final int rowIdxIdxRight = starListRight.getColumnIdByName(StarList.RowIdxColumnName);
        final int raDegIdxRight = starListRight.getColumnIdByName(StarList.RADegColumnName);
        final int decDegIdxRight = starListRight.getColumnIdByName(StarList.DEDegColumnName);

        if (rowIdxIdxLeft != -1 && raDegIdxLeft != -1
                && decDegIdxLeft != -1 && rowIdxIdxRight != -1
                && raDegIdxRight != -1 && decDegIdxRight != -1) {

            // create star indexes (declination):
            final TreeMap<Double, List<StarProperty>> starIndexLeft = createStarDecIndex(starListLeft, decDegIdxLeft, isLogDebug);
            final TreeMap<Double, List<StarProperty>> starIndexRight = createStarDecIndex(starListRight, decDegIdxRight, isLogDebug);

            final TreeMap<Double, List<StarProperty>> distMap = new TreeMap<Double, List<StarProperty>>();

            double raR, decR, raL, decL;
            List<StarProperty> starFoundLeft, starFoundRight;
            Integer rowIdxLeft, rowIdxRight;

            // for all stars in star list right:
            for (List<StarProperty> star : starListRight) {
                // avoid reentrance:
                if (star.size() != nPropRight) {

                    decR = star.get(decDegIdxRight).getDoubleValue();
                    raR = star.get(raDegIdxRight).getDoubleValue();

                    if (isLogDebug) {
                        _logger.debug("star right : {} - {}", raR, decR);
                    }

                    starFoundLeft = GetStarMatchingCriteria(raR, decR, criteriaPos, raDegIdxLeft, decDegIdxLeft, starIndexLeft, distMap, isLogDebug);
                    starFoundRight = null;

                    if (starFoundLeft != null) {
                        decL = starFoundLeft.get(decDegIdxLeft).getDoubleValue();
                        raL = starFoundLeft.get(raDegIdxLeft).getDoubleValue();

                        if (isLogDebug) {
                            _logger.debug("star left : {} - {}", raL, decL);
                        }

                        // check that the star found (left) has its closest match on right side:
                        starFoundRight = GetStarMatchingCriteria(raL, decL, criteriaPos, raDegIdxRight, decDegIdxRight, starIndexRight, distMap, isLogDebug);

                        if (starFoundRight != null) {
                            // update stats:
                            matchs++;

                            rowIdxLeft = starFoundLeft.get(rowIdxIdxLeft).getInteger();
                            rowIdxRight = starFoundRight.get(rowIdxIdxRight).getInteger();

                            if (isLogDebug) {
                                _logger.debug("star match: rowIdx: {} - {}", rowIdxLeft, rowIdxRight);
                            }

                            // avoid reentrance:
                            if (starFoundRight.size() != nPropRight) {
                                // add otherRowIdx value to star found right:                
                                starFoundRight.add(new StarProperty(rowIdxLeft));
                            }

                            // avoid reentrance:
                            if (starFoundLeft.size() != nPropLeft) {
                                // add otherRowIdx value to star found left:
                                starFoundLeft.add(new StarProperty(rowIdxRight));
                            }
                        }
                    }

                    if (starFoundLeft == null || starFoundRight == null || starFoundRight != star) {
                        // update stats:
                        onlyRight++;

                        // add NULL otherRowIdx value to star right (NO MATCH):
                        star.add(new StarProperty(null));
                    }
                }
            }

            // for all stars in star list right:
            for (List<StarProperty> star : starListLeft) {
                if (star.size() != nPropLeft) {
                    // update stats:
                    onlyLeft++;

                    // add NULL otherRowIdx value:
                    star.add(new StarProperty(null));
                }
            }

            if (_logger.isInfoEnabled()) {
                _logger.info("crossMatchRaDec: {} matchs, {} left only, {} right only - duration = {} ms.",
                        matchs, onlyLeft, onlyRight, 1e-6d * (System.nanoTime() - start));
            }

            return matchs;
        }


        return -1;
    }

    /**
     * Get matching star
     * @param raRef
     * @param decRef
     * @param criteriaPos
     * @param raDegIdx
     * @param decDegIdx
     * @param starIndex
     * @param distMap
     * @param isLogDebug
     * @return 
     */
    private static List<StarProperty> GetStarMatchingCriteria(final double raRef, final double decRef,
                                                              final double criteriaPos,
                                                              final int raDegIdx, final int decDegIdx,
                                                              final TreeMap<Double, List<StarProperty>> starIndex,
                                                              final TreeMap<Double, List<StarProperty>> distMap,
                                                              final boolean isLogDebug) {
        // GetStarMatchingCriteria:
        distMap.clear();
        List<StarProperty> starFound = null;

        final SortedMap<Double, List<StarProperty>> decMap = starIndex.subMap(decRef - criteriaPos, decRef + criteriaPos);

        if (decMap.isEmpty()) {
            if (isLogDebug) {
                _logger.debug("no star matching dec range : {} +/- {}", decRef, criteriaPos * ALX.DEG_IN_ARCSEC);
            }
        } else {
            double ra, dec, dist;

            for (List<StarProperty> star : decMap.values()) {
                ra = star.get(raDegIdx).getDoubleValue();
                dec = star.get(decDegIdx).getDoubleValue();

                dist = ALX.computeDistanceInDegrees(raRef, decRef, ra, dec);

                if (dist <= criteriaPos) {
                    distMap.put(Double.valueOf(dist), star);
                }
            }

            if (distMap.isEmpty()) {
                if (isLogDebug) {
                    _logger.debug("no star matching distance : {} +/- {}", decRef, criteriaPos * ALX.DEG_IN_ARCSEC);
                }
            } else {
                if (isLogDebug) {
                    for (Entry<Double, List<StarProperty>> entry : distMap.entrySet()) {
                        _logger.debug("{} = {}", entry.getKey(), entry.getValue());
                    }
                }

                // Use first one:
                starFound = distMap.get(distMap.firstKey());
                ra = starFound.get(raDegIdx).getDoubleValue();
                dec = starFound.get(decDegIdx).getDoubleValue();

                if (isLogDebug) {
                    _logger.debug("star found : {} - {} @ {} arcsec", ra, dec, distMap.firstKey() * ALX.DEG_IN_ARCSEC);
                }
            }
        }

        return starFound;
    }

    /**
     * Create star index (dec)
     * @param starList
     * @param decDegId
     * @param isLogDebug
     * @return 
     */
    private static TreeMap<Double, List<StarProperty>> createStarDecIndex(final StarList starList, final int decDegId,
                                                                          final boolean isLogDebug) {
        if (decDegId != -1) {
            final TreeMap<Double, List<StarProperty>> decIndex = new TreeMap<Double, List<StarProperty>>();

            Double dec;
            // fill sorted set:
            for (List<StarProperty> star : starList) {
                dec = star.get(decDegId).getDouble();
                decIndex.put(dec, star);
            }

            if (isLogDebug) {
                _logger.debug("decIndex:");

                for (Entry<Double, List<StarProperty>> entry : decIndex.entrySet()) {
                    _logger.debug("{} = {}", entry.getKey(), entry.getValue());
                }
            }
            return decIndex;
        }
        return null;
    }
}
