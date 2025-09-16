/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.vo;

import fr.jmmc.jmal.ALX;
import fr.jmmc.jmal.CoordUtils;
import fr.jmmc.jmal.star.Star;
import fr.jmmc.jmal.star.Star.Property;
import fr.jmmc.jmal.star.StarResolver;
import fr.jmmc.jmal.star.StarResolverResult;
import fr.jmmc.jmcs.util.FileUtils;
import fr.jmmc.jmcs.util.NumberUtils;
import fr.jmmc.jmcs.util.StringUtils;
import fr.jmmc.sclgui.calibrator.StarList;
import fr.jmmc.sclgui.calibrator.StarProperty;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 376: Name(String) - Star name (identifier from HIP, HD, TYC, 2MASS, DM or coordinates 'RA DE')

 1: HD(String) - HD identifier, click to call Simbad on this object
 4: HIP(String) - HIP identifier, click to call Simbad on this object
 7: DM(String) - DM number
 10: TYC1(String) - TYC1 number from Tycho-2
 13: TYC2(String) - TYC2 number from Tycho-2
 16: TYC3(String) - TYC3 number from Tycho-2
 22: 2MASS(String) - 2MASS identifier, click to call VizieR on this object
 25: AKARI(String) - AKARI source ID number, click to call VizieR on this object
 78: SBC9(String) - SBC9 identifier, click to call VizieR on this object
 81: WDS(String) - WDS identifier, click to call VizieR on this object
 *
 * Check coords (distance):
 28: RAJ2000(String)/h:m:s - Right Ascension - J2000
 32: DEJ2000(String)/d:m:s - Declination - J2000
 * 
 * Check simbad SpType & ObjTypes:
 57: SpType(String) - MK Spectral Type
 60: ObjTypes(String) - Simbad Object Type list (separated by comma)

 * Comments on JMDC = check: done: 639 stars
 IDS: [ALL, HD, HIP, TYC1, TYC2, TYC3, 2MASS, SBC9, WDS]
 Global Mismatchs[SCL NULL]: [37, 21, 2, 7, 7, 7, 13, 1, 0]
 Global Mismatchs[SIMBAD NULL]: [43, 2, 0, 35, 35, 35, 10, 5, 0]
 Global Mismatchs[SCL <> SIMBAD]: [11, 11, 0, 0, 0, 0, 0, 0, 0]

 1/ TYCHO IDs:
 - 35 TYC NULL in Simbad: Simbad ne regarde pas dans les 2 catalogues Tycho Supplementary 1 et 2

 name[HIP 54061]: sep=0.005 as: IDS MISMATCH:
 SearchCal: [165.931 61.751] [-134.11 -34.7] [95689, 54061, 4146, 1274, 1, 11034364+6145034, 648, 11037+6145]
 Simbad[0]: [165.931 61.751] [-134.11 -34.7] [95689, 54061, NULL, NULL, NULL, 11034364+6145034, 648, 11037+6145AB]
 Mismatchs:[1, 0, 0, 2, 2, 2, 0, 0, 0]


 2/ 2MASS IDs:
 - etoiles avec forts mouvements propres: 
 name[HIP 91768]: sep=0.003 as: IDS MISMATCH:
 SearchCal: [280.694 59.63] [-1332.03 1807.48] [173739, 91768, 3930, 1791, 1, NULL, NULL, NULL]
 Simbad[0]: [280.694 59.63] [-1332.03 1807.48] [173739, 91768, 3930, 1791, 1, 18424666+5937499, NULL, 18428+5938A]
 Mismatchs:[1, 0, 0, 0, 0, 0, 1, 0, 0]

 L'entree 18424666+5937499 a la colonne opt='0' = pas de composante optique => 2MASS a échoué dans son crossmatch avec TYCHO et USNO
 => ne pas faire utiliser opt='[TU]' dans la requete 2MASS !

 3/ HD ID (vient de ASCC):
 - null ou different dans SearchCal:
 name[TYC 3304-101-1]: sep=0.007 as: IDS MISMATCH:
 SearchCal: [41.592 49.653] [37.0 -14.68] [NULL, NULL, 3304, 101, 1, 02462210+4939110, NULL, NULL]
 Simbad[0]: [41.592 49.653] [37.9 -13.6] [17092, NULL, 3304, 101, 1, 02462210+4939110, NULL, NULL]
 Mismatchs:[1, 1, 0, 0, 0, 0, 0, 0, 0]

 => ASCC n'a pas récupérer l'identifiant HD !!



 * @author bourgesl
 */
public class CrossIdChecker {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(CrossIdChecker.class.getName());
    /** regular expression used to match characters different than numeric/+/- (1..n) */
    private final static Pattern PATTERN_NON_NUM = Pattern.compile("[^\\+\\-0-9]+");

    private final static int CHUNK_SIZE = 512;

    private final static boolean ROUND_COORDS = false;

    private final static String NULL_ID = "NULL";

    private static final String Name_ColumnName = "Name";
    private static final String[] COORDS_ColumnNames = new String[]{
        StarList.RADegColumnName,
        StarList.DEDegColumnName,
        "pmRa",
        "pmDec"
    };
    private static final int COORDS_COLS_len = COORDS_ColumnNames.length;

    private static final String[] IDS_ColumnNames = new String[]{
        "SIMBAD",
        "HD",
        "HIP",
        "DM",
        "TYC1",
        "TYC2",
        "TYC3",
        "GAIA",
        "2MASS",
        "WISE",
        "AKARI",
        "SBC9",
        "WDS"
    };
    private static final int IDS_COLS_len = IDS_ColumnNames.length;

    private final static String SIMBAD_TYC = "TYC ";
    private final static String SIMBAD_MAIN = "MAIN_ID";

    private static final String[] SIMBAD_Prefixes = new String[]{
        SIMBAD_MAIN,
        "HD ",
        "HIP ",
        "DM ",
        "TYC ",
        "TYC ",
        "TYC ",
        "Gaia DR2 ",
        "2MASS J",
        "WISE J",
        "AKARI-IRC-V1 J",
        "SBC9 ",
        "WDS J"
    };
    private static final boolean[] IDS_CHECK = new boolean[]{
        true, // Main SIMBAD ID
        false, // HD: ASCC issue (bad crossmatch on ASCC side)
        false, // HIP: missing few HIP stars from ASCC / HIP / HIP2 catalogs
        false, // ignore DM
        true, // checked: pb simbad with TYCHO2 supplementary catalogs
        true, // checked: pb simbad with TYCHO2 supplementary catalogs
        true, // checked: pb simbad with TYCHO2 supplementary catalogs
        true, // GAIA
        true, // 2MASS
        false, // WISE
        false, // ignore AKARI
        false, // ignore SBC
        false // ignore WDS (or remove component part)
    };

    static {
        if (IDS_ColumnNames.length != SIMBAD_Prefixes.length) {
            System.out.println("Incompatible IDS_ColumnNames and SIMBAD_Prefixes arrays !");
            System.exit(1);
        }
    }

    public static void check(final StarList starList) {
        _logger.info("check: start: {} stars", starList.size());

        // Get the ID of the column containing 'Name' star properties
        final int idxColName = starList.getColumnIdByName(Name_ColumnName);
        if (idxColName != -1) {
            String colName;
            final int[] idxColCoords = new int[COORDS_COLS_len];
            for (int i = 0; i < COORDS_COLS_len; i++) {
                colName = COORDS_ColumnNames[i];
                idxColCoords[i] = starList.getColumnIdByName(colName);
            }
            _logger.info("columns coords: " + Arrays.toString(COORDS_ColumnNames));
            _logger.info("columns idx: " + Arrays.toString(idxColCoords));

            final int[] idxColIds = new int[IDS_COLS_len];
            for (int i = 0; i < IDS_COLS_len; i++) {
                colName = IDS_ColumnNames[i];
                idxColIds[i] = starList.getColumnIdByName(colName);
            }
            _logger.info("columns ids: " + Arrays.toString(IDS_ColumnNames));
            _logger.info("columns idx: " + Arrays.toString(idxColIds));

            // Process chunks:
            final List<String> nameList = new ArrayList<String>(CHUNK_SIZE);
            final double[][] dataCoords = new double[CHUNK_SIZE][COORDS_COLS_len];
            final String[][] dataIds = new String[CHUNK_SIZE][IDS_COLS_len];
            final int[] globalMismatchs_NULL_1 = new int[IDS_COLS_len + 1];
            final int[] globalMismatchs_NULL_2 = new int[IDS_COLS_len + 1];
            final int[] globalMismatchs_VALS = new int[IDS_COLS_len + 1];

            final AtomicInteger countError = new AtomicInteger();
            final AtomicInteger countOK = new AtomicInteger();
            final AtomicInteger countKO = new AtomicInteger();
            final AtomicInteger countBadDist = new AtomicInteger();
            int ns = 0;
            int nc = 0;

            final int step = 20 * CHUNK_SIZE;

            // Open writer file:
            Writer dumpWriter = null;
            Writer reportWriter = null;
            try {
                dumpWriter = prepareDump();
                reportWriter = prepareReport();

                for (List<StarProperty> star : starList) {
                    ns++;

                    // Get star name (SearchCal Identifier):
                    final String name = star.get(idxColName).getStringValue();
                    if (name.isEmpty()) {
                        _logger.info("Missing star Name at row={}", ns);
                        continue;
                    }
                    nameList.add(name);

                    // Get Coords:
                    final double[] coords = dataCoords[nc];

                    for (int i = 0; i < COORDS_COLS_len; i++) {
                        coords[i] = (idxColCoords[i] != -1)
                                ? star.get(idxColCoords[i]).getDoubleValue()
                                : Double.NaN;
                    }
                    // Fix searchcal ra [-180; 180] to simbad range[0;360]:
                    if (coords[0] < 0.0) {
                        coords[0] += 360.0;
                    }

                    // Get all IDS:
                    final String[] ids = dataIds[nc];

                    for (int i = 0; i < IDS_COLS_len; i++) {
                        final String val = (idxColIds[i] != -1)
                                ? star.get(idxColIds[i]).getString()
                                : null;
                        ids[i] = (val != null) ? StringUtils.cleanWhiteSpaces(val) : NULL_ID;
                    }

                    nc++;
                    if (nc == CHUNK_SIZE) {
                        // if failure: exit:
                        if (!checkTargets(dumpWriter, reportWriter, nameList, dataCoords, dataIds,
                                globalMismatchs_NULL_1, globalMismatchs_NULL_2, globalMismatchs_VALS,
                                countError, countOK, countKO, countBadDist)) {
                            return;
                        }
                        // reset
                        nc = 0;
                        nameList.clear();
                    }

                    if (ns % step == 0) {
                        _logger.info("check intermediate: {} stars\nIDS: {}"
                                + "\nGlobal Match count: {}"
                                + "\nGlobal Mismatch count: {}"
                                + "\nTotal checked count: {}"
                                + "\nTotal simbad error: {}"
                                + "\nTotal bad distance: {}"
                                + "\nGlobal Mismatchs[SCL NULL]: {}"
                                + "\nGlobal Mismatchs[SIMBAD NULL]: {}"
                                + "\nGlobal Mismatchs[SCL <> SIMBAD]: {}",
                                ns,
                                Arrays.toString(IDS_ColumnNames),
                                countOK.get(),
                                countKO.get(),
                                countOK.get() + countKO.get(),
                                countError.get(),
                                countBadDist.get(),
                                Arrays.toString(globalMismatchs_NULL_1),
                                Arrays.toString(globalMismatchs_NULL_2),
                                Arrays.toString(globalMismatchs_VALS)
                        );
                    }
                }
                // last chunk:
                if (nc != 0) {
                    checkTargets(dumpWriter, reportWriter, nameList, dataCoords, dataIds,
                            globalMismatchs_NULL_1, globalMismatchs_NULL_2, globalMismatchs_VALS,
                            countError, countOK, countKO, countBadDist);
                }
            } catch (IOException ioe) {
                _logger.error("IO failure:", ioe);
            } finally {
                FileUtils.closeFile(reportWriter);
                FileUtils.closeFile(dumpWriter);

                _logger.info("check done: {} stars\nIDS: {}"
                        + "\nGlobal Match count: {}"
                        + "\nGlobal Mismatch count: {}"
                        + "\nTotal checked count: {}"
                        + "\nTotal simbad error: {}"
                        + "\nTotal bad distance: {}"
                        + "\nGlobal Mismatchs[SCL NULL]: {}"
                        + "\nGlobal Mismatchs[SIMBAD NULL]: {}"
                        + "\nGlobal Mismatchs[SCL <> SIMBAD]: {}",
                        ns,
                        Arrays.toString(IDS_ColumnNames),
                        countOK.get(),
                        countKO.get(),
                        countOK.get() + countKO.get(),
                        countError.get(),
                        countBadDist.get(),
                        Arrays.toString(globalMismatchs_NULL_1),
                        Arrays.toString(globalMismatchs_NULL_2),
                        Arrays.toString(globalMismatchs_VALS)
                );
            }
        }
    }

    private static boolean checkTargets(final Writer dumpWriter, final Writer reportWriter,
                                        final List<String> nameList, double[][] dataCoords, final String[][] dataIds,
                                        final int[] globalMismatchs_NULL_1, final int[] globalMismatchs_NULL_2,
                                        final int[] globalMismatchs_VALS, final AtomicInteger countError,
                                        final AtomicInteger countOK, final AtomicInteger countKO,
                                        final AtomicInteger countBadDist) throws IOException {

        final int len = nameList.size();
        if (len != 0) {
            // Wait for StarResolver task done:
            final Object rawResult = StarResolver.waitFor(new StarResolver().multipleResolve(nameList));
            if ((rawResult == null) || !(rawResult instanceof StarResolverResult)) {
                _logger.error("Unable to resolve target identifiers: " + nameList, "Star resolver problem");
                return false;
            }
            final StarResolverResult result = (StarResolverResult) rawResult;
            // Report errors:
            switch (result.getStatus()) {
                case ERROR_IO:
                case ERROR_PARSING:
                    _logger.error("Simbad error: {}", result.getErrorMessage());
                    return false;

                case ERROR_SERVER:
                    _logger.error("Simbad error: {}", result.getServerErrorMessage());
                    break;
                default:
                    break;
            }

            String name, rawIds;
            List<Star> stars;
            Star star;
            final String[] simbadIds = new String[IDS_COLS_len];
            final double[] simbadCoords = new double[COORDS_COLS_len];
            final int[] mismatchs = new int[IDS_COLS_len + 1];
            double sep;

            // check targets:
            for (int i = 0, j, ns; i < len; i++) {
                name = nameList.get(i);
                stars = result.getStars(name);

                if (stars == null) {
                    // count missing simbad result:
                    countError.incrementAndGet();

                    report(reportWriter, name, true, mismatchs, Double.NaN);

                } else {
                    for (j = 0, ns = stars.size(); j < ns; j++) {
                        star = stars.get(j);

                        // reset and extract simbad coords:
                        simbadCoords[0] = getDouble(star, Property.RA_d);
                        simbadCoords[1] = getDouble(star, Property.DEC_d);
                        simbadCoords[2] = getDouble(star, Property.PROPERMOTION_RA);
                        simbadCoords[3] = getDouble(star, Property.PROPERMOTION_DEC);

                        sep = CoordUtils.computeDistanceInDegrees(
                                dataCoords[i][0], dataCoords[i][1],
                                simbadCoords[0], simbadCoords[1]) * ALX.DEG_IN_ARCSEC;

                        // extract simbad identifiers:
                        rawIds = star.getPropertyAsString(Star.Property.IDS);
                        extractIds(star.getMainId(), rawIds, simbadIds);

                        compare(dumpWriter, reportWriter, name, sep, dataCoords[i], dataIds[i], j, simbadCoords, rawIds, simbadIds, mismatchs,
                                globalMismatchs_NULL_1, globalMismatchs_NULL_2, globalMismatchs_VALS,
                                countOK, countKO, countBadDist);
                    }
                }
            }
        }
        return true;
    }

    private static void compare(final Writer dumpWriter, final Writer reportWriter, String name, double sep, double[] sclCoords, String[] sclIds,
                                int ns, double[] simbadCoords,
                                final String rawIds, String[] simbadIds,
                                final int[] mismatchs,
                                final int[] globalMismatchs_NULL_1, final int[] globalMismatchs_NULL_2,
                                final int[] globalMismatchs_VALS,
                                final AtomicInteger countOK, final AtomicInteger countKO,
                                final AtomicInteger countBadDist) throws IOException {

        boolean dump = false;

        if (sep >= 1.0) {
            dump = true;

            countBadDist.incrementAndGet();
            _logger.info("name[{}]: bad sep={} as:\nSearchCal: {}\nSimbad[{}]: {}",
                    name,
                    NumberUtils.trimTo3Digits(sep),
                    Arrays.toString(sclCoords),
                    ns,
                    Arrays.toString(simbadCoords)
            );
        }

        mismatchs[0] = 0;

        String s1, s2;
        for (int i = 0; i < IDS_COLS_len; i++) {
            s1 = sclIds[i];
            s2 = simbadIds[i];

            if ((s1 == s2) || (s1.equals(s2))) {
                // reset:
                mismatchs[i + 1] = 0;
            } else {
                // mismatch:
                if (IDS_CHECK[i]) {
                    mismatchs[0] = 1;
                    dump = true;
                }
                // mismatch count per column ID:
                mismatchs[i + 1] = (s1 == NULL_ID) ? 1 : ((s2 == NULL_ID) ? 2 : 4);
            }
        }

        if (dump) {
            dump(dumpWriter, name, sep, sclCoords, sclIds, simbadCoords, simbadIds);
        }

        if (mismatchs[0] == 0) {
            countOK.incrementAndGet();

            _logger.debug("name[{}]: Simbad[{}]: IDS MATCH ALL",
                    name,
                    ns);
        } else {
            mismatchs[0] = 1;
            countKO.incrementAndGet();

            report(reportWriter, name, false, mismatchs, sep);

            _logger.info("name[{}]: sep={} as: IDS MISMATCH:"
                    + "\nSearchCal: [{} {}] [{} {}] {}"
                    + "\nSimbad[{}]: [{} {}] [{} {}] {}\nMismatchs:{}\nraw Ids: [{}]",
                    name,
                    NumberUtils.trimTo3Digits(sep),
                    roundCoord(sclCoords[0]),
                    roundCoord(sclCoords[1]),
                    roundCoord(sclCoords[2]),
                    roundCoord(sclCoords[3]),
                    Arrays.toString(sclIds),
                    ns,
                    roundCoord(simbadCoords[0]),
                    roundCoord(simbadCoords[1]),
                    roundCoord(simbadCoords[2]),
                    roundCoord(simbadCoords[3]),
                    Arrays.toString(simbadIds),
                    Arrays.toString(mismatchs),
                    rawIds
            );

            boolean set1 = true;
            boolean set2 = true;
            boolean set4 = true;

            for (int i = 1; i <= IDS_COLS_len; i++) {
                switch (mismatchs[i]) {
                    default:
                        continue;
                    case 1:
                        if (set1) {
                            set1 = false;
                            globalMismatchs_NULL_1[0]++;
                        }
                        globalMismatchs_NULL_1[i]++;
                        continue;
                    case 2:
                        if (set2) {
                            set2 = false;
                            globalMismatchs_NULL_2[0]++;
                        }
                        globalMismatchs_NULL_2[i]++;
                        continue;
                    case 4:
                        if (set4) {
                            set4 = false;
                            globalMismatchs_VALS[0]++;
                        }
                        globalMismatchs_VALS[i]++;
                        continue;
                }
            }
        }
    }

    static Writer prepareReport() throws IOException {
        final File file = new File("CrossIdChecker-" + new Date() + ".log");
        final Writer w = FileUtils.openFile(file);

        if (w == null) {
            throw new IllegalStateException("Unable to write file: " + file.getAbsolutePath());
        }
        _logger.info("Writing report into: {}", file.getAbsolutePath());

        w.write("Name,");
        w.write("NoMatch,");
        for (int i = 0; i < IDS_COLS_len; i++) {
            w.write(IDS_ColumnNames[i]);
            w.write(',');
        }
        w.write("sep(arcsec)\n");
        return w;
    }

    static void report(final Writer w, final String name, final boolean noMatch,
                       final int[] mismatchs, final double sep) throws IOException {
        w.write(name);
        w.write(',');
        if (noMatch) {
            // no Simbad match:
            w.write("1,");
            for (int i = 1; i <= IDS_COLS_len; i++) {
                w.write("0,");
            }
        } else {
            w.write("0,");
            for (int i = 1; i <= IDS_COLS_len; i++) {
                w.write(('0' + mismatchs[i]));
                w.write(',');
            }
        }
        w.write(Double.toString(sep));
        w.write('\n');
    }

    static Writer prepareDump() throws IOException {
        final File file = new File("CrossIdChecker-" + new Date() + "-dump.tst");
        final Writer w = FileUtils.openFile(file);

        if (w == null) {
            throw new IllegalStateException("Unable to write file: " + file.getAbsolutePath());
        }
        _logger.info("Writing dump into: {}", file.getAbsolutePath());

        w.write("#File generated by SearchCal CrossIdChecker (tst format for topcat / stilts)\n");
        w.write("Name\t");
        w.write("sep_as\t");

        for (int i = 0; i < COORDS_COLS_len; i++) {
            w.write("SearchCal_");
            w.write(COORDS_ColumnNames[i]);
            w.write('\t');
            w.write("Simbad_");
            w.write(COORDS_ColumnNames[i]);
            w.write('\t');
        }

        for (int i = 0; i < IDS_COLS_len; i++) {
            w.write("SearchCal_");
            w.write(IDS_ColumnNames[i]);
            w.write('\t');
            w.write("Simbad_");
            w.write(IDS_ColumnNames[i]);
            w.write('\t');
        }
        w.write('\n');

        for (int i = 0; i < IDS_COLS_len + COORDS_COLS_len + 2; i++) {
            w.write("--");
            w.write('\t');
        }
        w.write('\n');
        return w;
    }

    static void dump(final Writer w, final String name, final double sep,
                     double[] sclCoords, String[] sclIds,
                     double[] simbadCoords, String[] simbadIds) throws IOException {

        w.write(name);
        w.write('\t');
        w.write(Double.toString(sep));
        w.write('\t');

        for (int i = 0; i < COORDS_COLS_len; i++) {
            // SearchCal:
            w.write(Double.toString(sclCoords[i]));
            w.write('\t');
            // Simbad:
            w.write(Double.toString(simbadCoords[i]));
            w.write('\t');
        }

        for (int i = 0; i < IDS_COLS_len; i++) {
            // SearchCal:
            if (sclIds[i] != NULL_ID) {
                w.write(sclIds[i]);
            }
            w.write('\t');
            // Simbad:
            if (simbadIds[i] != NULL_ID) {
                w.write(simbadIds[i]);
            }
            w.write('\t');
        }

        w.write('\n');
    }

    private static void extractIds(final String mainId, final String ids, final String[] simbadIds) {
        int tycN = 0;
        String id, prefix;
        int from;

        // extract simbad ids:
        for (int i = 0, pos, end; i < IDS_COLS_len; i++) {
            // reset:
            simbadIds[i] = NULL_ID;

            prefix = SIMBAD_Prefixes[i];

            if (prefix == SIMBAD_MAIN) {
                simbadIds[i] = mainId;
            } else {
                from = 0;
                do {
                    // System.out.println("Parsing: "+ids + " from: "+from);
                    pos = ids.indexOf(prefix, from);
                    // reset:
                    from = -1;

                    if (pos != -1) {
                        // Check if previous char is ',' or pos=0
                        if (pos > 0) {
                            // check previous character:
                            if (ids.charAt(pos - 1) != ',') {
                                // search next token:
                                from = pos + prefix.length();
                                // System.out.println("invalid position: " + ids.substring(0, from));
                            }
                        }
                    }

                } while (from != -1);

                if (pos != -1) {
                    pos += prefix.length();

                    end = ids.indexOf(',', pos);

                    if (end == -1) {
                        end = ids.length();
                    }

                    id = ids.substring(pos, end);

                    // remove any invalid character:
                    id = replaceNonNumericChars(id);

                    // System.out.println("ID[" + prefix + "]: " + id);
                    if (SIMBAD_TYC == prefix) {
                        switch (tycN) {
                            case 0:
                                pos = id.indexOf('-');
                                simbadIds[i] = id.substring(0, pos);
                                break;
                            case 1:
                                pos = id.indexOf('-');
                                end = id.indexOf('-', pos + 1);
                                simbadIds[i] = id.substring(pos + 1, end);
                                break;
                            case 2:
                                pos = id.lastIndexOf('-');
                                simbadIds[i] = id.substring(pos + 1);
                                break;
                            default:
                        }
                        tycN++;
                    } else {
                        simbadIds[i] = StringUtils.cleanWhiteSpaces(id);
                    }
                }
            }
        }
        // System.out.println("\nIds=" + ids + "\nParsed:" + Arrays.toString(simbadIds));
    }

    static double getDouble(Star star, final Property property) {
        final Double value = star.getPropertyAsDouble(property);
        return (value != null) ? value.doubleValue() : Double.NaN;
    }

    static double roundCoord(double val) {
        return (ROUND_COORDS) ? NumberUtils.trimTo3Digits(val) : val;
    }

    public static String replaceNonNumericChars(final String value) {
        return PATTERN_NON_NUM.matcher(value).replaceAll("");
    }

    private CrossIdChecker() {
    }
}
