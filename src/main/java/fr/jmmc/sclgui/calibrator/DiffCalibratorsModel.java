/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.calibrator;

import fr.jmmc.jmal.ALX;
import fr.jmmc.jmal.CoordUtils;
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
import fr.jmmc.jmcs.util.timer.StatLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements calibrators model comparison
 * @author bourgesl
 */
public final class DiffCalibratorsModel {

    /** Logger */
    private final static Logger _logger = LoggerFactory.getLogger(DiffCalibratorsModel.class.getName());
    
    /** shared null star property to reduce memory footprint */
    private final static StarProperty NULL_STAR_PROPERTY = new ObjectStarProperty(null, Origin.KEY_ORIGIN_COMPUTED, Confidence.KEY_CONFIDENCE_HIGH);
    
    /** hard coded preference for ignored properties */
    public final static String IGNORE_PROPERTIES;
    /** hard coded preference for ignored properties */
    private final static Map<String, Threshold> THRESHOLD_PROPERTIES = new HashMap<String, Threshold>(32);
    /** 
     * To avoid declination key duplicates in TreeMap (dec index), key = dec + ra / normFactor;
     * normFactor  is very big to ensure (ra / normFactor) < 0.5 mas
     */
    private final static double RA_NORM_FACTOR = ALX.MILLI_ARCSEC_IN_DEGREES / 360d;
    /** default key */
    public final static String THRESHOLD_DEFAULT = "_default_";

    static {
        // Ignore properties (Dec 2012):
        // pmRa/Dec because HIP2
        // GLAT/GLON computed now
        // WDS sep1/sep2 may change in CDS
        // SpType may change ?
        // Bphg Rphg (wrong columns in 2MASS)
        // Icous no more filled by JB
        IGNORE_PROPERTIES = "pmRa pmDec GLAT GLON sep1 sep2 SpType Bphg Rphg Teff_SpType logg_SpType";

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

        // Photographic magnitudes (0.05 mag) :
        addThreshold(".phg", 5e-2d);

        // Cousin magnitudes (0.05 mag) :
        addThreshold(".cous", 5e-2d);

        // Jy: (see http://www.stsci.edu/hst/nicmos/tools/conversion_help.html)
        // TODO: find correct values representing 0.1 mag in the band:
        addThreshold("S09", 1e-2d);       // 0.01 Jy ?
        addThreshold("S18", 1e-2d);       // 0.01 Jy ?
        addThreshold("F12", 1e-2d);       // 0.01 Jy ?

        // diameters:
        // use relative diff:
        addThreshold("UD_.", 1e-2d, false);     // 1%
        addThreshold("diam_.*", 1e-2d, false);  // 1%
        addThreshold("e_diam_.*", 5e-2d);       // 5%

        // vis2:
        addThreshold("vis2", 1e-2d);    // 1%

        addThreshold(THRESHOLD_DEFAULT, 1e-3d + 1e-6d); // 1/1000
    }

    /**
     * Return the threshold for the given property name
     * @param name property name to match
     * @return threshold or null
     */
    private static Threshold findThreshold(final String name) {
        for (Map.Entry<String, Threshold> entry : THRESHOLD_PROPERTIES.entrySet()) {
            if (Pattern.matches(entry.getKey(), name)) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("regexp '{}' matched by {}", entry.getKey(), name);
                }
                return entry.getValue();
            }
        }

        return THRESHOLD_PROPERTIES.get(THRESHOLD_DEFAULT);
    }

    /**
     * Add threshold for the given String matcher
     * @param matcher string matcher
     * @param threshold value
     */
    private static void addThreshold(final String matcher, final double threshold) {
        addThreshold(matcher, threshold, true);
    }

    /**
     * Add threshold for the given String matcher
     * @param matcher string matcher
     * @param threshold value
     * @param isAbsolute true means absolute threshold; false a relative threshold
     */
    private static void addThreshold(final String matcher, final double threshold, final boolean isAbsolute) {
        THRESHOLD_PROPERTIES.put(matcher, new Threshold(threshold, isAbsolute));
    }
    /* members */
    /** file on left side */
    private File _fileLeft = null;
    /** file on right side */
    private File _fileRight = null;
    /** calibrators model of first file to compare (reference) */
    private final CalibratorsModel _calibratorsModelLeft;
    /** calibrators model of second file to compare */
    private final CalibratorsModel _calibratorsModelRight;
    /** calibrators model of the differences */
    private final CalibratorsModel _calibratorsModelDiff;

    /**
     * Public constructor
     * @param calibratorsModelLeft
     * @param calibratorsModelRight
     * @param calibratorsModelDiff 
     */
    public DiffCalibratorsModel(final CalibratorsModel calibratorsModelLeft, final CalibratorsModel calibratorsModelRight, final CalibratorsModel calibratorsModelDiff) {
        this._calibratorsModelLeft = calibratorsModelLeft;
        this._calibratorsModelRight = calibratorsModelRight;
        this._calibratorsModelDiff = calibratorsModelDiff;
    }

    /** @return file on left side */
    public File getFileLeft() {
        return _fileLeft;
    }

    /** @return file on right side */
    public File getFileRight() {
        return _fileRight;
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
                return _calibratorsModelRight;
            case -1:
                return _calibratorsModelLeft;
            case 2:
                return _calibratorsModelDiff;
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
        _logger.info("diff: fileLeft= '{}' - fileRight= '{}'", fileLeft, fileRight);

        // disable auto update:
        _calibratorsModelLeft.setAutoUpdate(false);
        _calibratorsModelRight.setAutoUpdate(false);
        _calibratorsModelDiff.setAutoUpdate(false);
        try {

            _calibratorsModelLeft.parseVOTable(fileLeft, false); // sync

            _calibratorsModelRight.parseVOTable(fileRight, false); // sync

            _fileLeft = fileLeft;
            _fileRight = fileRight;

            _logger.info("------------------------------------------------------------");

            _logger.info("Left  : scenario {}", _calibratorsModelLeft._brightScenarioFlag ? "bright" : "faint");
            _logger.info("Left  : band {}", _calibratorsModelLeft._magnitudeBand);

            _logger.info("Right : scenario {}", _calibratorsModelRight._brightScenarioFlag ? "bright" : "faint");
            _logger.info("Right : band {}", _calibratorsModelRight._magnitudeBand);

            // Fix band and scenario:
            if (_calibratorsModelRight._brightScenarioFlag != _calibratorsModelLeft._brightScenarioFlag) {
                _calibratorsModelRight._brightScenarioFlag = _calibratorsModelLeft._brightScenarioFlag;
            }
            if (!_calibratorsModelRight._magnitudeBand.equals(_calibratorsModelLeft._magnitudeBand)) {
                _calibratorsModelRight._magnitudeBand = _calibratorsModelLeft._magnitudeBand;
            }

            // TODO: compare also paramSet (query params)
            // Get Star lists:
            final StarList starListLeft = _calibratorsModelLeft.getOriginalStarList();
            final StarList starListRight = _calibratorsModelRight.getOriginalStarList();

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
            _calibratorsModelDiff._brightScenarioFlag = _calibratorsModelLeft._brightScenarioFlag;
            _calibratorsModelDiff._magnitudeBand = _calibratorsModelLeft._magnitudeBand;

            _logger.info("------------------------------------------------------------");

            // update models because otherRowIdx property added:
            _calibratorsModelLeft.updateModel(starListLeft);
            _calibratorsModelRight.updateModel(starListRight);

            // display computed diff model:
            _calibratorsModelDiff.updateModel(diffStarList);

            // Update views / GUI:
            updateDetailedViewPreference(diffStarList.getMetaData(), diffProps);

        } finally {
            _calibratorsModelLeft.setAutoUpdate(true);
            _calibratorsModelRight.setAutoUpdate(true);
            _calibratorsModelDiff.setAutoUpdate(true);
        }

        _logger.info("------------------------------------------------------------");
    }

    /**
     * Update the view.columns.detailed.<bright|faint>.<band> preference
     * @param diffStarListMetaData 
     * @param diffProps unused arg (only diff properties)
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
            final String prefKey = Preferences.PREFIX_VIEW_COLUMNS_DETAILED + (_calibratorsModelLeft._brightScenarioFlag ? "bright" : "faint")
                    + '.' + _calibratorsModelLeft._magnitudeBand;

            Preferences.getInstance().setPreference(prefKey, detailedProperties);
        } catch (PreferencesException pe) {
            _logger.warn("setPreference exception:", pe);
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
        diffStarListMetaData.addPropertyMeta(new StarPropertyMeta(StarList.LeftColumnName, StarPropertyMeta.TYPE_INTEGER, "left only count", "ID_MAIN:1", "", ""));
        diffStarListMetaData.addPropertyMeta(new StarPropertyMeta(StarList.RightColumnName, StarPropertyMeta.TYPE_INTEGER, "right only count", "ID_MAIN:2", "", ""));
        diffStarListMetaData.addPropertyMeta(new StarPropertyMeta(StarList.DiffColumnName, StarPropertyMeta.TYPE_INTEGER, "diff count", "ID_MAIN:3", "", ""));

        // total number of properties in diff stars:
        final int propCount = diffStarListMetaData.getPropertyCount();

        // Compare anyway all properties except rowIdx, otherRowIdx and next dynamic properties:
        final int diffCount = diffStarListMetaData.getPropertyIndexByName(StarList.RowIdxColumnName);

        final boolean[] propMask = computeCompareMask(diffStarListMetaData, diffCount, prepareIgnoreProperties(ignoreProperties));

        // star property mapping:
        final int[] mapIdxLeft = new int[diffCount];
        final int[] mapIdxRight = new int[diffCount];

        // star property threshold mapping:
        final Threshold[] mapIdxThreshold = new Threshold[diffCount];

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

        StarProperty starPropertyLeftRowIdx, starPropertyRightRowIdx;
        Integer rightRowIdx;
        StarPropertyMeta propMeta;
        Class<?> classType;
        Vector<StarProperty> starRight;
        Vector<StarProperty> star;
        StarProperty starPropertyLeft;
        StarProperty starPropertyRight;
        StarProperty starProperty;
        Object propertyValue;
        String originValue, confidenceValue;
        Origin origin;
        Confidence confidence;
        boolean isSet;

        int valCmpRes;
        final DoubleDiff dblDiff = new DoubleDiff();
        Double dblLeft, dblRight;
        String strLeft, strRight;
        Boolean boolLeft, boolRight;
        Integer intLeft, intRight;
        Threshold th;

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
                    valCmpRes = 0;
                    lessTh = false;

                    if (i == deletedFlagColumnID) {
                        // always create a new modifiable Star property for the deleted flag column:
                        propertyValue = Boolean.FALSE;
                        isSet = true;
                    } else {

                        classType = propMeta.getClassType();

                        // TODO: handle new column types (int)
                        // value:
                        if (classType == Double.class) {
                            dblLeft = starPropertyLeft.getDouble();
                            dblRight = starPropertyRight.getDouble();

                            valCmpRes = compareDouble(dblLeft, dblRight, dblDiff);

                            switch (valCmpRes) {
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
                                        th = mapIdxThreshold[i];

                                        if (th.isAbsolute) {
                                            // absolute diff:
                                            if (Math.abs(dblDiff.diff) < th.threshold) {
                                                lessTh = true;
                                                if (isLogDebug) {
                                                    _logger.debug("{} = {} less than threshold [{}]", propMeta.getName(), NumberUtils.format(dblDiff.diff), th.threshold);
                                                }
                                                break;
                                            }
                                        } else {
                                            // relative diff:
                                            if (!Double.isNaN(dblDiff.relDiff) && Math.abs(dblDiff.relDiff) < th.threshold) {
                                                lessTh = true;
                                                if (isLogDebug) {
                                                    _logger.debug("{} = {} less than threshold [{}]", propMeta.getName(), NumberUtils.format(dblDiff.relDiff), th.threshold);
                                                }
                                                break;
                                            }
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

                            valCmpRes = compareObject(strLeft, strRight);

                            switch (valCmpRes) {
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

                            valCmpRes = compareObject(boolLeft, boolRight);

                            switch (valCmpRes) {
                                default:
                                    break;
                                case -1:
                                    propertyValue = boolRight;
                                    break;
                                case 1:
                                    propertyValue = boolLeft;
                                    break;
                                case 2:
                                    propertyValue = boolRight; // show right value (true or false)
                                    break;
                            }
                        } else if (classType == Integer.class) {
                            intLeft = starPropertyLeft.getInteger();
                            intRight = starPropertyRight.getInteger();

                            valCmpRes = compareObject(intLeft, intRight);

                            switch (valCmpRes) {
                                default:
                                    break;
                                case -1:
                                    propertyValue = intRight;
                                    break;
                                case 1:
                                    propertyValue = intLeft;
                                    break;
                                case 2:
                                    propertyValue = NumberUtils.valueOf(intLeft.intValue() - intRight.intValue());
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
                                switch (valCmpRes) {
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
                    origin = Origin.ORIGIN_NONE;

                    strLeft = starPropertyLeft.getOrigin().toString();
                    strRight = starPropertyRight.getOrigin().toString();

                    if (strLeft.equals(strRight)) {
                        // use value for colors:
                        originValue = (starPropertyLeft.hasOrigin()) ? strLeft : null;
                    } else {
                        originValue = strLeft + " | " + strRight;

                        isSet = true;

                        if (propMask[i]) {
                            diffOrigins++;
                            mapIdxDiffOrigins[i]++;
                        }
                    }

                    if (originValue != null) {
                        switch (valCmpRes) {
                            default:
                                break;
                            case -1:
                                originValue = "RIGHT: " + originValue;
                                break;
                            case 1:
                                originValue = "LEFT: " + originValue;
                                break;
                            case 2:
                                originValue = "DIFF: " + originValue;
                                break;
                        }
                        if (!propMask[i]) {
                            originValue = "SKIP " + originValue;
                        } else if (lessTh) {
                            originValue = "LESS " + originValue;
                        }

                        origin = Origin.parseCustomOrigin(originValue);
                    }

                    // confidence:
                    confidence = Confidence.CONFIDENCE_NO;

                    // hack for former undefined Confidence indexes:
                    if (starPropertyLeft.getConfidenceIndex() == Confidence.KEY_CONFIDENCE_NO
                            || starPropertyRight.getConfidenceIndex() == Confidence.KEY_CONFIDENCE_NO) {
                        strLeft = strRight = Confidence.CONFIDENCE_NO.toString();
                    } else {
                        strLeft = starPropertyLeft.getConfidence().toString();
                        strRight = starPropertyRight.getConfidence().toString();
                    }

                    if (strLeft.equals(strRight)) {
                        // use value for colors:
                        confidenceValue = starPropertyLeft.getConfidence().toString();
                    } else {
                        confidenceValue = strLeft + " | " + strRight;

                        isSet = true;

                        if (propMask[i]) {
                            diffConfidences++;
                            mapIdxDiffConfidences[i]++;
                        }
                    }

                    if (confidenceValue != null) {
                        switch (valCmpRes) {
                            default:
                                break;
                            case -1:
                                confidenceValue = "RIGHT: " + confidenceValue;
                                break;
                            case 1:
                                confidenceValue = "LEFT: " + confidenceValue;
                                break;
                            case 2:
                                confidenceValue = "DIFF: " + confidenceValue;
                                break;
                        }
                        if (!propMask[i]) {
                            confidenceValue = "SKIP " + confidenceValue;
                        } else if (lessTh) {
                            confidenceValue = "LESS " + confidenceValue;
                        }

                        confidence = Confidence.parseCustomConfidence(confidenceValue);
                    }

                    if (isLogDebug) {
                        _logger.debug("Property[{}][{}]: {} [{}][{}]", i, propMeta.getName(), propertyValue, origin, confidence);
                    }

                    /*
                     * Create a new StarProperty instance from the retrieved value, origin and confidence.
                     */
                    starProperty = (isSet) ? new ObjectStarProperty(propertyValue, origin.getKey(), confidence.getKey()) : StarProperty.EMPTY_STAR_PROPERTY;

                    // Add the newly created star property to the star property list
                    star.add(starProperty);
                }

                // add rowIdx:
                star.add(starPropertyLeftRowIdx);

                // add otherRowIdx:
                star.add(starPropertyRightRowIdx);

                // add star stats (left, right, diff):
                star.add(new ObjectStarProperty(left));
                star.add(new ObjectStarProperty(right));
                star.add(new ObjectStarProperty(diff));

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

        if (!diffStarList.isEmpty()) {
            // First star:
            final Vector<StarProperty> first = (Vector<StarProperty>) diffStarList.get(0);

            _logger.info("{} Compared Star Properties.", first.size());

            if (first.size() != first.capacity()) {
                _logger.warn("Incorrect Property capacity: {} != {} ", first.size(), first.capacity());
            }
        }

        if (_logger.isInfoEnabled()) {
            _logger.info("compare: {} matchs, {} left only, {} right only - diffs: {} values, {} origins, {} confidences - duration = {} ms.",
                    matchs, onlyLeft, onlyRight, diffValues, diffOrigins, diffConfidences, 1e-6d * (System.nanoTime() - start));

            _logger.info("compare: {} different stars among {} crossmatching stars", nStarDiff, diffStarList.size());

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
                    sb.append('\n');
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
                    sb.append('\n');
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
        /* 1 arcsec separation criteria */
        final double criteriaPos = 1d * ALX.ARCSEC_IN_DEGREES;

        final boolean isLogDebug = _logger.isDebugEnabled();

        final long start = System.nanoTime();

        int matchs = 0;
        int onlyLeft = 0;
        int onlyRight = 0;

        final StarListMeta starListMetaLeft = starListLeft.getMetaData();
        final StarListMeta starListMetaRight = starListRight.getMetaData();

        // Add otherRowIdx property:
        starListMetaLeft.addPropertyMeta(new StarPropertyMeta(StarList.OtherRowIdxColumnName, StarPropertyMeta.TYPE_INTEGER, "row index in other list", "ID_CROSSID", "", ""));
        starListMetaRight.addPropertyMeta(new StarPropertyMeta(StarList.OtherRowIdxColumnName, StarPropertyMeta.TYPE_INTEGER, "row index in other list", "ID_CROSSID", "", ""));

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
            final TreeMap<Double, List<StarProperty>> starIndexLeft = createStarDecIndex(starListLeft, raDegIdxLeft, decDegIdxLeft, isLogDebug);
            final TreeMap<Double, List<StarProperty>> starIndexRight = createStarDecIndex(starListRight, raDegIdxRight, decDegIdxRight, isLogDebug);

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
                                starFoundRight.add(new ObjectStarProperty(rowIdxLeft));
                            }

                            // avoid reentrance:
                            if (starFoundLeft.size() != nPropLeft) {
                                // add otherRowIdx value to star found left:
                                starFoundLeft.add(new ObjectStarProperty(rowIdxRight));
                            }
                        }
                    }

                    if (starFoundLeft == null || starFoundRight == null || starFoundRight != star) {
                        // update stats:
                        onlyRight++;

                        // add NULL otherRowIdx value to star right (NO MATCH):
                        star.add(NULL_STAR_PROPERTY);
                    }
                }
            }

            // for all stars in star list right:
            for (List<StarProperty> star : starListLeft) {
                if (star.size() != nPropLeft) {
                    // update stats:
                    onlyLeft++;

                    // add NULL otherRowIdx value:
                    star.add(NULL_STAR_PROPERTY);
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

                dist = CoordUtils.computeDistanceInDegrees(raRef, decRef, ra, dec);

                if (dist <= criteriaPos) {
                    distMap.put(Double.valueOf(dist), star);
                }
            }

            if (distMap.isEmpty()) {
                if (isLogDebug) {
                    _logger.debug("no star matching position : {} {} +/- {}", raRef, decRef, criteriaPos * ALX.DEG_IN_ARCSEC);
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
     * @param raDegId
     * @param decDegId
     * @param isLogDebug
     * @return 
     */
    private static TreeMap<Double, List<StarProperty>> createStarDecIndex(final StarList starList, final int raDegId, final int decDegId,
                                                                          final boolean isLogDebug) {
        if (decDegId != -1) {
            // Map can not contain duplicate keys ie duplicated dec values:
            // to avoid duplicates, key = dec + ra / normFactor
            final TreeMap<Double, List<StarProperty>> decIndex = new TreeMap<Double, List<StarProperty>>();

            Double ra, dec, key;
            // fill sorted set:
            for (List<StarProperty> star : starList) {
                ra = star.get(raDegId).getDouble();
                dec = star.get(decDegId).getDouble();

                key = Double.valueOf(dec + ra * RA_NORM_FACTOR);
                if (decIndex.put(key, star) != null) {
                    _logger.warn("duplicated key found in star dec index: {}", key);
                }
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

    /**
     * Threshold configuration
     */
    private final static class Threshold {

        /** threshold value */
        final double threshold;
        /** true means absolute threshold; false a relative threshold */
        final boolean isAbsolute;

        /**
         * Protected constructor
         * @param threshold threshold value
         * @param isAbsolute true means absolute threshold; false a relative threshold
         */
        Threshold(final Double threshold, final boolean isAbsolute) {
            this.threshold = threshold;
            this.isAbsolute = isAbsolute;
        }
    }
}
