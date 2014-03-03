package cds.savot.tools;

//
//Copyright 2002-2014 - Universite de Strasbourg / Centre National de la
//Recherche Scientifique
//------
//
//SAVOT - Simple Access to VOTable - Parser
//
//Author:  Andre Schaaff
//Address: Centre de Donnees astronomiques de Strasbourg
//         11 rue de l'Universite
//         67000 STRASBOURG
//         FRANCE
//Contributors: Laurent Bourges (code improvement, ideas, tests, etc.), Gregory Mantelet (binary capabilities)
//Email:   cds-question@astro.unistra.fr
//
//-------
//
//In accordance with the international conventions about intellectual
//property rights this software and associated documentation files
//(the "Software") is protected. The rightholder authorizes :
//the reproduction and representation as a private copy or for educational
//and research purposes outside any lucrative use,
//subject to the following conditions:
//
//The above copyright notice shall be included.
//
//THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
//EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
//OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, NON INFRINGEMENT,
//LOSS OF DATA, LOSS OF PROFIT, LOSS OF BARGAIN OR IMPOSSIBILITY
//TO USE SUCH SOFWARE. IN NO EVENT SHALL THE RIGHTHOLDER BE LIABLE
//FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
//TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
//THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
//For any other exploitation contact the rightholder.
//
//                     -----------
//
//Conformement aux conventions internationales relatives aux droits de
//propriete intellectuelle ce logiciel et sa documentation sont proteges.
//Le titulaire des droits autorise :
//la reproduction et la representation a titre de copie privee ou des fins
//d'enseignement et de recherche et en dehors de toute utilisation lucrative.
//Cette autorisation est faite sous les conditions suivantes :
//
//La mention du copyright portee ci-dessus devra etre clairement indiquee.
//
//LE LOGICIEL EST LIVRE "EN L'ETAT", SANS GARANTIE D'AUCUNE SORTE.
//LE TITULAIRE DES DROITS NE SAURAIT, EN AUCUN CAS ETRE TENU CONTRACTUELLEMENT
//OU DELICTUELLEMENT POUR RESPONSABLE DES DOMMAGES DIRECTS OU INDIRECTS
//(Y COMPRIS ET A TITRE PUREMENT ILLUSTRATIF ET NON LIMITATIF,
//LA PRIVATION DE JOUISSANCE DU LOGICIEL, LA PERTE DE DONNEES,
//LE MANQUE A GAGNER OU AUGMENTATION DE COUTS ET DEPENSES, LES PERTES
//D'EXPLOITATION,LES PERTES DE MARCHES OU TOUTES ACTIONS EN CONTREFACON)
//POUVANT RESULTER DE L'UTILISATION, DE LA MAUVAISE UTILISATION
//OU DE L'IMPOSSIBILITE D'UTILISER LE LOGICIEL, ALORS MEME
//QU'IL AURAIT ETE AVISE DE LA POSSIBILITE DE SURVENANCE DE TELS DOMMAGES.
//
//Pour toute autre utilisation contactez le titulaire des droits.
import cds.savot.model.FieldSet;
import cds.savot.model.ParamSet;
import cds.savot.model.ResourceSet;
import cds.savot.model.SavotField;
import cds.savot.model.SavotParam;
import cds.savot.model.SavotResource;
import cds.savot.model.SavotTR;
import cds.savot.model.SavotTable;
import cds.savot.model.SavotVOTable;
import cds.savot.model.TDSet;
import cds.savot.stax.SavotStaxParser;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.StringTokenizer;
import java.util.logging.Level;
import javax.xml.stream.XMLStreamException;

/**
 * Very simple VOTable converter to CSV (comma) or TSV (tab)
 * <p>
 * Designed to use with JSR-173 compliant (Streaming Api for XML)
 * </p>
 *
 * @author bourgesl
 */
public final class SimpleVoTableTransformer {

    /** Logger associated to SavotStaxParser classes */
    private final static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(SimpleVoTableTransformer.class.getName());
    /** table size threshold to show progress information */
    private final static int LARGE_TABLE_THRESHOLD = 5000;

    public static SimpleVoTableTransformer newCSVTransformer() {
        return new SimpleVoTableTransformer(',');
    }

    public static SimpleVoTableTransformer newTSVTransformer() {
        return new SimpleVoTableTransformer('\t');
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            logger.severe("Missing args: " + SimpleVoTableTransformer.class.getName() + " <inputFile> <outputFile> !");
            return;
        }
        final String inputFile = args[0];
        final String outputFile = args[1];
        logger.info(String.format("Transforming '%s' to '%s' ...", inputFile, outputFile));
        try {
            newTSVTransformer().transform(inputFile, outputFile);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "fatal error", e);
        }
    }


    /* members */
    private final char separator;

    public SimpleVoTableTransformer(final char separator) {
        this.separator = separator;
    }

    private void transform(final String inFile, final String outputFile) throws IOException, XMLStreamException {

        final long start = System.nanoTime();

        final SavotStaxParser parser = new SavotStaxParser(inFile, SavotStaxParser.ROWREAD);

        // start parsing document and get first TR if data are present:
        SavotTR tr = parser.getNextTR();

        // Get the VOTable
        final SavotVOTable savotVoTable = parser.getVOTable();
        final ResourceSet resources = savotVoTable.getResources();

        SavotResource resource = null;
        SavotTable table = null;

        if (resources.getItemCount() == 1) {
            // VOTable must have 1 resource:
            resource = resources.getItemAt(0);

            if (resource.getTables().getItemCount() == 1) {
                // resource must have ony 1 table:
                table = resource.getTables().getItemAt(0);
            }
        }

        // check that the votable has one resource / table containing groups and fields:
        if (resource == null || table == null) {
            throw new IllegalArgumentException("Incorrect VOTable format (1 mandatory table) !");
        }

        final char sep = separator;

        final FileOutputStream outputStream = new FileOutputStream(new File(outputFile));

        final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream), 64 * 1024); // 64K text buffer
        try {
            bw.append("#TABLE: ").append(inFile).write("\n");

            writeDescription(bw, table.getDescription());

            writeParamSet(bw, table.getParams());

            final FieldSet fields = table.getFields();
            writeFieldSet(bw, fields);

            final int tableRows = table.getNrowsValue(); // optional
            final int rowLen = fields.getItemCount();

            int nRow = 0, i, tdLen;
            String value;
            TDSet row;

            // step for progress info for very large tables:
            final int step = (tableRows > LARGE_TABLE_THRESHOLD) ? tableRows / 20 : Integer.MAX_VALUE;

            // Iterate on rows:
            do {
                if ((nRow % step == 0) && (nRow != 0)) {
                    // progress bar:
                    logger.info(String.format("processing row: %d / %d ...", nRow, tableRows));
                }

                // Get the data corresponding to the current row
                // note: use TDSet that ensures range checks
                row = tr.getTDSet();

                tdLen = row.getItemCount();

                for (i = 0; i < rowLen; i++) {
                    // Add separator
                    if (i != 0) {
                        bw.write(sep);
                    }
                    if (i < tdLen) {
                        // write the value directly:
                        value = row.getRawContent(i);
                        if (value != null) {
                            bw.write(value);
                        }
                    }
                }

                bw.write('\n');

                nRow++;

            } while ((tr = parser.getNextTR()) != null);

            logger.info(String.format("transform: %d rows processed in %.3f ms.", nRow, 1e-6d * (System.nanoTime() - start)));
        } finally {
            parser.close();
            bw.close();
        }
    }

    private void writeDescription(final BufferedWriter bw, final String description) throws IOException {
        if (description.length() != 0) {
            final StringTokenizer t = new StringTokenizer(description, "\n");
            while (t.hasMoreTokens()) {
                bw.append("#  ").append(t.nextToken()).write('\n');
            }
        }
    }

    private void writeParamSet(final BufferedWriter bw, final ParamSet paramSet) throws IOException {
        bw.write("#\n#PARAMS:\n");

        final int len = paramSet.getItemCount();
        if (len == 0) {
            return;
        }

        SavotParam param;

        // 1. write param IDs:
        for (int i = 0; i < len; i++) {
            param = paramSet.getItemAt(i);

            bw.append("# ").append(param.getName()).append(" = ").append(param.getValue()).write('\n');

            writeDescription(bw, param.getDescription());
        }
    }

    private void writeFieldSet(final BufferedWriter bw, final FieldSet fieldSet) throws IOException {
        bw.write("#\n#FIELDS:\n");

        final int len = fieldSet.getItemCount();
        if (len == 0) {
            return;
        }
        final char sep = separator;
        SavotField field;

        // 1. write field IDs:
        bw.write("# ");
        for (int i = 0; i < len; i++) {
            field = fieldSet.getItemAt(i);

            bw.append(field.getId()).write(sep);
        }
        bw.write('\n');

        // 2. write field names:
        for (int i = 0; i < len; i++) {
            field = fieldSet.getItemAt(i);

            bw.append(field.getName()).write(sep);
        }
        bw.write('\n');

        // 3. write field UCD:
        bw.write("# ");
        for (int i = 0; i < len; i++) {
            field = fieldSet.getItemAt(i);

            bw.append(field.getUcd()).write(sep);
        }
        bw.write('\n');

        // 4. write field unit:
        bw.write("# ");
        for (int i = 0; i < len; i++) {
            field = fieldSet.getItemAt(i);

            bw.append(field.getUnit()).write(sep);
        }
        bw.write('\n');
    }
}
