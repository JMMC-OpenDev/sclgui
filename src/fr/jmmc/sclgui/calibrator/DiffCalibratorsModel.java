/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.calibrator;

import fr.jmmc.jmal.ALX;
import fr.jmmc.jmcs.data.preference.PreferencesException;
import fr.jmmc.jmcs.util.NumberUtils;
import fr.jmmc.sclgui.preference.Preferences;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;
import java.util.regex.Pattern;
import org.ivoa.util.timer.StatLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements calibrators model comparison
 * @author bourgesl
 */
public final class DiffCalibratorsModel {

    /** Logger */
    private final static Logger _logger = LoggerFactory.getLogger(DiffCalibratorsModel.class.getName());
    /** hard coded preference for ignored properties */
    public final static String IGNORE_PROPERTIES = "pmRa pmDec GLAT GLON sep1 sep2 SpType";
    /** hard coded preference for ignored properties */
    public final static Map<String, Double> THRESHOLD_PROPERTIES = new HashMap<String, Double>(8);
    /** default key */
    public final static String THRESHOLD_DEFAULT = "_default_";

    static {
        // note: Use String patterns to match multiple properties:

        // Johnson magnitudes (0.05 mag) :
        addThreshold("B", 5e-2d);
        addThreshold("V", 5e-2d);
        addThreshold("R", 5e-2d);
        addThreshold("I", 5e-2d);
        addThreshold("J", 5e-2d);
        addThreshold("H", 5e-2d);
        addThreshold("K", 5e-2d);
        addThreshold("L", 5e-2d);
        addThreshold("M", 5e-2d);
        addThreshold("N", 5e-2d);

        // Photographic magnitudes:
        addThreshold(".phg", 1e-1d);    // 0.1 mag
        // Cousin magnitudes:
        addThreshold(".cous", 5e-2d);   // 0.05 mag

        // Jy: (see http://www.stsci.edu/hst/nicmos/tools/conversion_help.html)
        // TODO: find correct values representing 0.1 mag in the band:
        addThreshold("S09", 1e-2d);       // 0.01 Jy ?
        addThreshold("S18", 1e-2d);       // 0.01 Jy ?
        addThreshold("F12", 1e-2d);       // 0.01 Jy ?

        // diameters:
        // use relative diff:
        addThreshold("UD_.", 1e-1d);      // 0.1 mas
        addThreshold("diam_.*", 1e-1d);   // 0.1 mas
        addThreshold("e_diam_.*", 5e-2d); // 5%

        // vis2:
        addThreshold("vis2", 1e-2d);      // 1%

        addThreshold(THRESHOLD_DEFAULT, 1e-3d + 1e-6d);
    }

    /**
     * Return the threshold for the given property name
     * @param name property name to match
     * @return threshold
     */
    private static double findThreshold(final String name) {
        for (Map.Entry<String, Double> entry : THRESHOLD_PROPERTIES.entrySet()) {
            if (Pattern.matches(entry.getKey(), name)) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("regexp '{}' matched by {}", entry.getKey(), name);
                }
                return entry.getValue().doubleValue();
            }
        }

        return THRESHOLD_PROPERTIES.get(THRESHOLD_DEFAULT).doubleValue();
    }

    /**
     * Add threshold for the given String matcher
     * @param matcher string matcher
     * @param threshold value
     */
    private static void addThreshold(final String matcher, final double threshold) {
        THRESHOLD_PROPERTIES.put(matcher, threshold);
    }
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
        _logger.info("------------------------------------------------------------");

        _logger.warn("diff: fileLeft= '{}' - fileRight= '{}'", fileLeft, fileRight);

        // disable auto update:
        calibratorsModelLeft.setAutoUpdate(false);
        calibratorsModelRight.setAutoUpdate(false);
        calibratorsModelDiff.setAutoUpdate(false);
        try {

            calibratorsModelLeft.parseVOTable(fileLeft, false); // sync

            calibratorsModelRight.parseVOTable(fileRight, false); // sync

            _logger.info("------------------------------------------------------------");

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

            _logger.info("------------------------------------------------------------");

            // compare stars and compute otherRowIdx property on both lists:
            final int matchs = crossMatchRaDec(starListLeft, starListRight);

            _logger.info("------------------------------------------------------------");

            final StarList diffStarList;

            // block to reduce scope of diffStarListMetaData:
            {
                // compare star list meta data:
                final StarListMeta diffStarListMetaData = processStarListMeta(starListLeft.getMetaData(), starListRight.getMetaData());

                // create a new diff star list (common properties only):
                diffStarList = new StarList(diffStarListMetaData);
                if (matchs > 0) {
                    // prepare list:
                    diffStarList.ensureCapacity(matchs);
                }
            }

            // TODO: use command line argument or preference:
            final String diffProps = compare(starListLeft, starListRight, diffStarList, IGNORE_PROPERTIES);

            // Define band and scenario to fix JTable display:
            calibratorsModelDiff._brightScenarioFlag = calibratorsModelLeft._brightScenarioFlag;
            calibratorsModelDiff._magnitudeBand = calibratorsModelLeft._magnitudeBand;

            _logger.info("------------------------------------------------------------");

            // update models because otherRowIdx property added:
            calibratorsModelLeft.updateModel(starListLeft);
            calibratorsModelRight.updateModel(starListRight);

            // display computed diff model:
            calibratorsModelDiff.updateModel(diffStarList);

            // Update views / GUI:
            updateDetailedViewPreference(diffStarList.getMetaData(), diffProps);

        } finally {
            calibratorsModelLeft.setAutoUpdate(true);
            calibratorsModelRight.setAutoUpdate(true);
            calibratorsModelDiff.setAutoUpdate(true);
        }

        _logger.info("------------------------------------------------------------");
    }

    /**
     * Update the view.columns.detailed.<bright|faint>.<band> preference
     * @param diffStarListMetaData 
     * @param diffProps
     */
    private void updateDetailedViewPreference(final StarListMeta diffStarListMetaData, final String diffProps) {
        String propName;
        final StringBuilder sb = new StringBuilder(1024);
        for (int i = 0, count = diffStarListMetaData.getPropertyCount(); i < count; i++) {
            propName = diffStarListMetaData.getPropertyName(i);

            if (propName.equals(StarList.RAJ2000ColumnName)
                    || propName.equals(StarList.DEJ2000ColumnName)
                    || propName.equals(StarList.RowIdxColumnName)
                    || propName.equals(StarList.OtherRowIdxColumnName)
                    || propName.equals(StarList.DistColumnName)
                    || propName.equals(StarList.LeftColumnName)
                    || propName.equals(StarList.RightColumnName)
                    || propName.equals(StarList.DiffColumnName)) {
                // skip
                continue;
            }

            sb.append(propName).append(' ');
        }
        sb.insert(0, "rowIdx otherRowIdx left right diff dist RAJ2000 DEJ2000 ");

        final String detailedProperties = sb.toString();

        try {
            final String prefKey = "view.columns.detailed." + (calibratorsModelLeft._brightScenarioFlag ? "bright" : "faint")
                    + '.' + calibratorsModelLeft._magnitudeBand;

            Preferences.getInstance().setPreference(prefKey, detailedProperties);
        } catch (PreferencesException pe) {
            _logger.warn("setPreference exception:", pe);
        }

        // TODO: refine custom view:
        if (false) {
            // Hack diff view:
            calibratorsModelDiff._customPropertyView = "rowIdx otherRowIdx left right diff " + diffProps;
        }
    }

    /**
     * Compute the comparison mask as interger array
     * @param diffStarListMetaData diff star list meta data
     * @param diffCount index of the last compared property
     * @param ignorePropertyNames ignore property names as String array
     * @return comparison mask as interger array
     */
    private boolean[] computeCompareMask(final StarListMeta diffStarListMetaData, final int diffCount, final List<String> ignorePropertyNames) {
        final boolean[] compareMask = new boolean[diffCount];

        _logger.info("Ignore star properties:\n{}", ignorePropertyNames);

        final int ignoreLength = (ignorePropertyNames != null) ? ignorePropertyNames.size() : 0;

        // Use list to keep only valid columns:
        final List<Integer> ignoreIndex = new ArrayList<Integer>(ignoreLength);

        if (ignoreLength != 0) {
            String propName;
            int propIdx;

            for (int i = 0, len = ignoreLength; i < len; i++) {
                propName = ignorePropertyNames.get(i);

                if (propName != null) {
                    // Get each property index from its name:
                    propIdx = diffStarListMetaData.getPropertyIndexByName(propName);

                    // If no column Id was found for the given column name
                    if (propIdx != -1) {
                        ignoreIndex.add(NumberUtils.valueOf(propIdx));
                    }
                }
            }
        }

        for (int i = 0; i < diffCount; i++) {
            compareMask[i] = (!ignoreIndex.contains(NumberUtils.valueOf(i)));
        }
        return compareMask;
    }

    /**
     * Prepare the ignored star property list
     * @param ignoreProperties optional string containing star property names
     * @return ignored star property list
     */
    private List<String> prepareIgnoreProperties(final String ignoreProperties) {
        final List<String> ignorePropertyNames = new ArrayList<String>(10);

        // always ignore RA/DEC coordinates (crossmatch done elsewhere):
        ignorePropertyNames.add(StarList.RAJ2000ColumnName);
        ignorePropertyNames.add(StarList.DEJ2000ColumnName);

        ignorePropertyNames.add(StarList.RADegColumnName);
        ignorePropertyNames.add(StarList.DEDegColumnName);

        if (ignoreProperties != null) {
            final String[] propertyNames = ignoreProperties.split(" ");
            ignorePropertyNames.addAll(Arrays.asList(propertyNames));
        }
        return ignorePropertyNames;
    }

    /**
     * Compare the two given star list using meta data provided by the diff star list (shared star properties)
     * @param starListLeft first star list
     * @param starListRight second star list
     * @param diffStarList output list (diff)
     * @param ignoreProperties property names to ignore in star comparison separated by ' '
     * @return String containing property names where important differences are present
     */
    private String compare(final StarList starListLeft, final StarList starListRight, final StarList diffStarList, final String ignoreProperties) {

        final boolean isLogDebug = _logger.isDebugEnabled();

        final long start = System.nanoTime();

        final StarListMeta starListMetaLeft = starListLeft.getMetaData();
        final StarListMeta starListMetaRight = starListRight.getMetaData();
        final StarListMeta diffStarListMetaData = diffStarList.getMetaData();

        // prepare star properties:
        diffStarListMetaData.addPropertyMeta(new StarPropertyMeta(StarList.LeftColumnName, Integer.class, "left only count", "ID_MAIN:1", "", ""));
        diffStarListMetaData.addPropertyMeta(new StarPropertyMeta(StarList.RightColumnName, Integer.class, "right only count", "ID_MAIN:2", "", ""));
        diffStarListMetaData.addPropertyMeta(new StarPropertyMeta(StarList.DiffColumnName, Integer.class, "diff count", "ID_MAIN:3", "", ""));

        // total number of properties in diff stars:
        final int propCount = diffStarListMetaData.getPropertyCount();

        // Compare anyway all properties except rowIdx, otherRowIdx and next dynamic properties:
        final int diffCount = diffStarListMetaData.getPropertyIndexByName(StarList.RowIdxColumnName);

        final boolean[] propMask = computeCompareMask(diffStarListMetaData, diffCount, prepareIgnoreProperties(ignoreProperties));

        // star property mapping:
        final int[] mapIdxLeft = new int[diffCount];
        final int[] mapIdxRight = new int[diffCount];

        // star property threshold mapping:
        final double[] mapIdxThreshold = new double[diffCount];

        // global statistics:
        int matchs = 0;
        int onlyLeft = 0;
        int onlyRight = 0;
        int diffValues = 0;
        int diffOrigins = 0;
        int diffConfidences = 0;

        int nStarDiff = 0;

        // statistics per property:
        final int[] mapIdxOnlyLeft = new int[diffCount];
        final int[] mapIdxOnlyRight = new int[diffCount];
        final int[] mapIdxDiffValues = new int[diffCount];

        final int[] mapIdxDiffOrigins = new int[diffCount];
        final int[] mapIdxDiffConfidences = new int[diffCount];

        final StatLong[] mapIdxStats = new StatLong[diffCount];
        final StatLong[] mapIdxRelStats = new StatLong[diffCount];

        String propName;
        for (int i = 0; i < diffCount; i++) {
            propName = diffStarListMetaData.getPropertyName(i);

            mapIdxLeft[i] = starListMetaLeft.getPropertyIndexByName(propName);
            mapIdxRight[i] = starListMetaRight.getPropertyIndexByName(propName);

            mapIdxThreshold[i] = findThreshold(propName);

            mapIdxOnlyLeft[i] = 0;
            mapIdxOnlyRight[i] = 0;
            mapIdxDiffValues[i] = 0;

            mapIdxDiffOrigins[i] = 0;
            mapIdxDiffConfidences[i] = 0;

            mapIdxStats[i] = new StatLong();
            mapIdxRelStats[i] = new StatLong();
        }

        final int rowIdxIdxLeft = starListLeft.getColumnIdByName(StarList.RowIdxColumnName);
        final int otherRowIdxIdxLeft = starListLeft.getColumnIdByName(StarList.OtherRowIdxColumnName);

        // special case for deleted flag column as it is editable:
        // => always create a new editable Star Property
        final int deletedFlagColumnID = diffStarList.getDeletedFlagColumnID();

        final Map<String, String> originValues = new HashMap<String, String>(32);
        final Map<String, String> confidenceValues = new HashMap<String, String>(16);

        StarProperty starPropertyLeftRowIdx, starPropertyRightRowIdx;
        Integer rightRowIdx;
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

        int left, right, diff;
        boolean lessTh;

        // for all stars in star list left:
        for (List<StarProperty> starLeft : starListLeft) {

            starPropertyLeftRowIdx = starLeft.get(rowIdxIdxLeft);
            starPropertyRightRowIdx = starLeft.get(otherRowIdxIdxLeft);
            rightRowIdx = starPropertyRightRowIdx.getInteger();

            if (rightRowIdx != null) {
                // match found:
                starRight = starListRight.get(rightRowIdx.intValue() - 1);

                // create a new 'diff' star:
                star = new Vector<StarProperty>(propCount);

                left = 0;
                right = 0;
                diff = 0;

                for (int i = 0; i < diffCount; i++) {
                    propMeta = diffStarListMetaData.getPropertyMeta(i);
                    starPropertyLeft = starLeft.get(mapIdxLeft[i]);
                    starPropertyRight = starRight.get(mapIdxRight[i]);

                    propertyValue = null;
                    res = 0;
                    lessTh = false;

                    if (i == deletedFlagColumnID) {
                        // always create a new modifiable Star property for the deleted flag column:
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
                                default:
                                    break;
                                case -1:
                                    propertyValue = dblRight;
                                    break;
                                case 1:
                                    propertyValue = dblLeft;
                                    break;
                                case 2:
                                    propertyValue = Double.valueOf(dblDiff.diff);

                                    if (propMask[i]) {
                                        if (Math.abs(dblDiff.diff) < mapIdxThreshold[i]) {
                                            lessTh = true;
                                            if (isLogDebug) {
                                                _logger.debug("{} = {} less than threshold [{}]", propMeta.getName(), NumberUtils.format(dblDiff.diff), mapIdxThreshold[i]);
                                            }
                                            break;
                                        }

                                        // absolute diff:
                                        mapIdxStats[i].add(Math.abs(dblDiff.diff));

                                        if (!Double.isNaN(dblDiff.relDiff)) {
                                            // relative diff:
                                            mapIdxRelStats[i].add(Math.abs(dblDiff.relDiff));
                                        }
                                    }
                                    break;
                            }
                        } else if (classType == String.class) {
                            strLeft = starPropertyLeft.getString();
                            strRight = starPropertyRight.getString();

                            res = compareObject(strLeft, strRight);

                            switch (res) {
                                default:
                                    break;
                                case -1:
                                    propertyValue = "~ | " + strRight;
                                    break;
                                case 1:
                                    propertyValue = strLeft + " | ~";
                                    break;
                                case 2:
                                    propertyValue = strLeft + " | " + strRight;
                                    break;
                            }
                        } else if (classType == Boolean.class) {
                            boolLeft = starPropertyLeft.getBoolean();
                            boolRight = starPropertyRight.getBoolean();

                            res = compareObject(boolLeft, boolRight);

                            switch (res) {
                                default:
                                    break;
                                case -1:
                                    propertyValue = boolRight;
                                    break;
                                case 1:
                                    propertyValue = boolLeft;
                                    break;
                                case 2:
                                    propertyValue = Boolean.FALSE;
                                    break;
                            }
                        } else {
                            _logger.warn("unsupported data type [{}]", classType);
                        }

                        if (propertyValue == null) {
                            isSet = false;

                            if (propMask[i]) {
                                matchs++;
                            }
                        } else {
                            isSet = true;

                            if (propMask[i] && !lessTh) {
                                switch (res) {
                                    default:
                                        break;
                                    case -1:
                                        right++;
                                        mapIdxOnlyRight[i]++;
                                        break;
                                    case 1:
                                        left++;
                                        mapIdxOnlyLeft[i]++;
                                        break;
                                    case 2:
                                        diff++;
                                        mapIdxDiffValues[i]++;
                                        break;
                                }
                            }
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

                        if (propMask[i]) {
                            diffOrigins++;
                            mapIdxDiffOrigins[i]++;
                        }
                    }

                    if (origin != null) {
                        switch (res) {
                            default:
                                break;
                            case -1:
                                origin = "RIGHT: " + origin;
                                break;
                            case 1:
                                origin = "LEFT: " + origin;
                                break;
                            case 2:
                                origin = "DIFF: " + origin;
                                break;
                        }
                        if (!propMask[i]) {
                            origin = "SKIP " + origin;
                        } else if (lessTh) {
                            origin = "LESS " + origin;
                        }

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

                        if (propMask[i]) {
                            diffConfidences++;
                            mapIdxDiffConfidences[i]++;
                        }
                    }

                    if (confidence != null) {
                        switch (res) {
                            default:
                                break;
                            case -1:
                                confidence = "RIGHT: " + confidence;
                                break;
                            case 1:
                                confidence = "LEFT: " + confidence;
                                break;
                            case 2:
                                confidence = "DIFF: " + confidence;
                                break;
                        }
                        if (!propMask[i]) {
                            confidence = "SKIP " + confidence;
                        } else if (lessTh) {
                            confidence = "LESS " + confidence;
                        }

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
                star.add(starPropertyLeftRowIdx);

                // add otherRowIdx:
                star.add(starPropertyRightRowIdx);

                // add star stats (left, right, diff):
                star.add(new StarProperty(left));
                star.add(new StarProperty(right));
                star.add(new StarProperty(diff));

                // Store each diff row as a list of star properties
                diffStarList.add(star);

                // update global stats:
                onlyRight += right;
                onlyLeft += left;
                diffValues += diff;

                if (diff != 0) {
                    nStarDiff++;
                }
            }
        }

        if (_logger.isInfoEnabled()) {
            _logger.info("\ncompare: {} matchs, {} left only, {} right only - diffs: {} values, {} origins, {} confidences - duration = {} ms.",
                    matchs, onlyLeft, onlyRight, diffValues, diffOrigins, diffConfidences, 1e-6d * (System.nanoTime() - start));

            _logger.info("\ncompare: {} different stars among {} crossmatching stars", nStarDiff, diffStarList.size());

            _logger.info("------------------------------------------------------------");

            final boolean full = false;

            final StringBuilder sb = new StringBuilder(16 * 1024);
            int diffProperties = 0;

            for (int i = 0; i < diffCount; i++) {

                if (mapIdxOnlyLeft[i] != 0 || mapIdxOnlyRight[i] != 0 || mapIdxDiffValues[i] != 0 || mapIdxDiffOrigins[i] != 0 || mapIdxDiffConfidences[i] != 0) {
                    diffProperties++;

                    propName = diffStarListMetaData.getPropertyName(i);

                    sb.append('[').append(propName).append("]:\t").append(mapIdxOnlyLeft[i]).append(" left,\t");
                    sb.append(mapIdxOnlyRight[i]).append(" right,\t").append(mapIdxDiffValues[i]).append(" diffs,\t");
                    sb.append(mapIdxDiffOrigins[i]).append(" origins,\t").append(mapIdxDiffConfidences[i]).append(" confidences");

                    if (mapIdxStats[i].getCounter() != 0) {
                        sb.append(",\tabsolute: ");
                        mapIdxStats[i].toString(sb, full);

                        if (mapIdxRelStats[i].getCounter() != 0) {
                            sb.append(",\trelative: ");
                            mapIdxRelStats[i].toString(sb, full);
                        }
                    }
                    sb.append("\n");
                }
            }
            _logger.info("compare: diff properties with left/right only:\n{} {} diff properties", sb.toString(), diffProperties);

            _logger.info("------------------------------------------------------------");
            sb.setLength(0);
            diffProperties = 0;

            final StringBuilder sbProp = new StringBuilder(256);

            for (int i = 0; i < diffCount; i++) {

                if (mapIdxDiffValues[i] != 0) {
                    diffProperties++;

                    propName = diffStarListMetaData.getPropertyName(i);

                    sbProp.append(propName).append(' ');

                    sb.append('[').append(propName).append("]:\t");

                    if (mapIdxStats[i].getCounter() == 0) {
                        sb.append(mapIdxDiffValues[i]).append(" diffs");
                    } else {
                        sb.append("absolute: ");
                        mapIdxStats[i].toString(sb, full);

                        if (mapIdxRelStats[i].getCounter() != 0) {
                            sb.append(",\trelative: ");
                            mapIdxRelStats[i].toString(sb, full);
                        }
                    }
                    sb.append("\n");
                }
            }
            _logger.info("compare: diff properties with diff only:\n{} {} diff properties", sb.toString(), diffProperties);

            return sbProp.toString();
        }

        return null;
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
        starListMetaLeft.addPropertyMeta(new StarPropertyMeta(StarList.OtherRowIdxColumnName, Integer.class, "row index in other list", "ID_CROSSID", "", ""));
        starListMetaRight.addPropertyMeta(new StarPropertyMeta(StarList.OtherRowIdxColumnName, Integer.class, "row index in other list", "ID_CROSSID", "", ""));

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
