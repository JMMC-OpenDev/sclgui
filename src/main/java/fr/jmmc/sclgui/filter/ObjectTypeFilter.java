/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.filter;

import fr.jmmc.sclgui.calibrator.StarList;
import fr.jmmc.sclgui.calibrator.StarProperty;
import java.util.List;

/**
 * Object Type filter.
 */
public final class ObjectTypeFilter extends Filter {

    /* members */
    /* filter execution variables */
    /** the Simbad ObjectTypes column Id */
    private int _ObjTypesID = -1;

    /**
     * Default constructor.
     */
    public ObjectTypeFilter() {
        super();
    }

    /**
     * Reset the filter
     */
    @Override
    public void reset() {
        setEnabled(Boolean.TRUE);
    }

    /**
     * Return the filter name.
     *
     * @return the name of the filter.
     */
    @Override
    public String getName() {
        return "Reject invalid Object Types";
    }

    /**
     * Prepare the filter execution with the given star list.
     *
     * @param starList the list of star to get column information
     */
    @Override
    public void onPrepare(final StarList starList) {
        // Get the id of the current column name
        _ObjTypesID = starList.getColumnIdByName(StarList.ObjTypesColumnName);
    }

    /**
     * Return whether the given row should be removed or not.
     *
     * @param row the star properties to be evaluated.
     *
     * @return true if the given row should be rejected, false otherwise.
     */
    @Override
    public boolean shouldRemoveRow(final List<StarProperty> row) {
        StarProperty cell;

        // If the desired column name exists
        if (_ObjTypesID != -1) {
            // Get the cell of the desired column
            cell = row.get(_ObjTypesID);

            // if the Simbad ObjectTypes exist
            if (cell.hasValue()) {
                // Check the value:
                return filterObjTypes(cell.getString());
            }
        }

        // Otherwise this row should be kept
        return false;
    }

    private static boolean filterObjTypes(final String ObjTypes) {
        assert ObjTypes != null;

        // expression taken from:
        // http://apps.jmmc.fr/exist/apps/myapp/jsdc-otypes.html
        return contains(ObjTypes, ",Unknown,")
                || contains(ObjTypes, ",?,")
                || contains(ObjTypes, ",Transient,")
                || contains(ObjTypes, ",ev,")
                || contains(ObjTypes, ",Radio,")
                || contains(ObjTypes, ",Rad,")
                || contains(ObjTypes, ",Radio(m),")
                || contains(ObjTypes, ",mR,")
                || contains(ObjTypes, ",Radio(cm),")
                || contains(ObjTypes, ",cm,")
                || contains(ObjTypes, ",Radio(mm),")
                || contains(ObjTypes, ",mm,")
                || contains(ObjTypes, ",Radio(sub-mm),")
                || contains(ObjTypes, ",smm,")
                || contains(ObjTypes, ",HI,")
                || contains(ObjTypes, ",radioBurst,")
                || contains(ObjTypes, ",rB,")
                || contains(ObjTypes, ",Maser,")
                || contains(ObjTypes, ",Mas,")
                || contains(ObjTypes, ",IR>30um,")
                || contains(ObjTypes, ",FIR,")
                || contains(ObjTypes, ",IR<10um,")
                || contains(ObjTypes, ",NIR,")
                || contains(ObjTypes, ",Red,")
                || contains(ObjTypes, ",red,")
                || contains(ObjTypes, ",RedExtreme,")
                || contains(ObjTypes, ",ERO,")
                || contains(ObjTypes, ",Blue,")
                || contains(ObjTypes, ",blu,")
                || contains(ObjTypes, ",X,")
                || contains(ObjTypes, ",ULX?,")
                || contains(ObjTypes, ",UX?,")
                || contains(ObjTypes, ",ULX,")
                || contains(ObjTypes, ",gamma,")
                || contains(ObjTypes, ",gam,")
                || contains(ObjTypes, ",gammaBurst,")
                || contains(ObjTypes, ",gB,")
                || contains(ObjTypes, ",Inexistent,")
                || contains(ObjTypes, ",err,")
                || contains(ObjTypes, ",Gravitation,")
                || contains(ObjTypes, ",grv,")
                || contains(ObjTypes, ",LensingEv,")
                || contains(ObjTypes, ",Lev,")
                || contains(ObjTypes, ",Candidate_LensSystem,")
                || contains(ObjTypes, ",LS?,")
                || contains(ObjTypes, ",Candidate_Lens,")
                || contains(ObjTypes, ",Le?,")
                || contains(ObjTypes, ",Possible_lensImage,")
                || contains(ObjTypes, ",LI?,")
                || contains(ObjTypes, ",GravLens,")
                || contains(ObjTypes, ",gLe,")
                || contains(ObjTypes, ",GravLensSystem,")
                || contains(ObjTypes, ",gLS,")
                || contains(ObjTypes, ",Candidates,")
                || contains(ObjTypes, ",..?,")
                || contains(ObjTypes, ",Possible_G,")
                || contains(ObjTypes, ",G?,")
                || contains(ObjTypes, ",Possible_SClG,")
                || contains(ObjTypes, ",SC?,")
                || contains(ObjTypes, ",Possible_ClG,")
                || contains(ObjTypes, ",C?G,")
                || contains(ObjTypes, ",Possible_GrG,")
                || contains(ObjTypes, ",Gr?,")
                || contains(ObjTypes, ",Candidate_**,")
                || contains(ObjTypes, ",**?,")
                || contains(ObjTypes, ",Candidate_EB*,")
                || contains(ObjTypes, ",EB?,")
                || contains(ObjTypes, ",Candidate_CV*,")
                || contains(ObjTypes, ",CV?,")
                || contains(ObjTypes, ",Candidate_XB*,")
                || contains(ObjTypes, ",XB?,")
                || contains(ObjTypes, ",Candidate_LMXB,")
                || contains(ObjTypes, ",LX?,")
                || contains(ObjTypes, ",Candidate_HMXB,")
                || contains(ObjTypes, ",HX?,")
                || contains(ObjTypes, ",Candidate_YSO,")
                || contains(ObjTypes, ",Y*?,")
                || contains(ObjTypes, ",Candidate_pMS*,")
                || contains(ObjTypes, ",pr?,")
                || contains(ObjTypes, ",Candidate_TTau*,")
                || contains(ObjTypes, ",TT?,")
                || contains(ObjTypes, ",Candidate_C*,")
                || contains(ObjTypes, ",C*?,")
                || contains(ObjTypes, ",Candidate_S*,")
                || contains(ObjTypes, ",S*?,")
                || contains(ObjTypes, ",Candidate_OH,")
                || contains(ObjTypes, ",OH?,")
                || contains(ObjTypes, ",Candidate_CH,")
                || contains(ObjTypes, ",CH?,")
                || contains(ObjTypes, ",Candidate_WR*,")
                || contains(ObjTypes, ",WR?,")
                || contains(ObjTypes, ",Candidate_Be*,")
                || contains(ObjTypes, ",Be?,")
                || contains(ObjTypes, ",Candidate_Ae*,")
                || contains(ObjTypes, ",Ae?,")
                || contains(ObjTypes, ",Candidate_HB*,")
                || contains(ObjTypes, ",HB?,")
                || contains(ObjTypes, ",Candidate_RRLyr,")
                || contains(ObjTypes, ",RR?,")
                || contains(ObjTypes, ",Candidate_Cepheid,")
                || contains(ObjTypes, ",Ce?,")
                || contains(ObjTypes, ",Candidate_post-AGB*,")
                || contains(ObjTypes, ",pA?,")
                || contains(ObjTypes, ",Candidate_BSS,")
                || contains(ObjTypes, ",BS?,")
                || contains(ObjTypes, ",Candidate_BH,")
                || contains(ObjTypes, ",BH?,")
                || contains(ObjTypes, ",Candidate_SN*,")
                || contains(ObjTypes, ",SN?,")
                || contains(ObjTypes, ",multiple_object,")
                || contains(ObjTypes, ",mul,")
                || contains(ObjTypes, ",Region,")
                || contains(ObjTypes, ",reg,")
                || contains(ObjTypes, ",Void,")
                || contains(ObjTypes, ",vid,")
                || contains(ObjTypes, ",SuperClG,")
                || contains(ObjTypes, ",SCG,")
                || contains(ObjTypes, ",ClG,")
                || contains(ObjTypes, ",GroupG,")
                || contains(ObjTypes, ",GrG,")
                || contains(ObjTypes, ",Compact_Gr_G,")
                || contains(ObjTypes, ",CGG,")
                || contains(ObjTypes, ",PairG,")
                || contains(ObjTypes, ",PaG,")
                || contains(ObjTypes, ",IG,")
                || contains(ObjTypes, ",Cl*?,")
                || contains(ObjTypes, ",C?*,")
                || contains(ObjTypes, ",GlCl?,")
                || contains(ObjTypes, ",Gl?,")
                || contains(ObjTypes, ",Cl*,")
                || contains(ObjTypes, ",GlCl,")
                || contains(ObjTypes, ",GlC,")
                || contains(ObjTypes, ",OpCl,")
                || contains(ObjTypes, ",OpC,")
                || contains(ObjTypes, ",Assoc*,")
                || contains(ObjTypes, ",As*,")
                || contains(ObjTypes, ",EB*,")
                || contains(ObjTypes, ",EB*Algol,")
                || contains(ObjTypes, ",Al*,")
                || contains(ObjTypes, ",EB*betLyr,")
                || contains(ObjTypes, ",bL*,")
                || contains(ObjTypes, ",EB*WUMa,")
                || contains(ObjTypes, ",WU*,")
                || contains(ObjTypes, ",SB*,")
                || contains(ObjTypes, ",EllipVar,")
                || contains(ObjTypes, ",El*,")
                || contains(ObjTypes, ",Symbiotic*,")
                || contains(ObjTypes, ",Sy*,")
                || contains(ObjTypes, ",CataclyV*,")
                || contains(ObjTypes, ",CV*,")
                || contains(ObjTypes, ",DQHer,")
                || contains(ObjTypes, ",DQ*,")
                || contains(ObjTypes, ",AMHer,")
                || contains(ObjTypes, ",AM*,")
                || contains(ObjTypes, ",Nova-like,")
                || contains(ObjTypes, ",NL*,")
                || contains(ObjTypes, ",Nova,")
                || contains(ObjTypes, ",No*,")
                || contains(ObjTypes, ",DwarfNova,")
                || contains(ObjTypes, ",DN*,")
                || contains(ObjTypes, ",XB,")
                || contains(ObjTypes, ",XB*,")
                || contains(ObjTypes, ",LMXB,")
                || contains(ObjTypes, ",LXB,")
                || contains(ObjTypes, ",HMXB,")
                || contains(ObjTypes, ",HXB,")
                || contains(ObjTypes, ",ISM,")
                || contains(ObjTypes, ",PartofCloud,")
                || contains(ObjTypes, ",PoC,")
                || contains(ObjTypes, ",PN?,")
                || contains(ObjTypes, ",ComGlob,")
                || contains(ObjTypes, ",CGb,")
                || contains(ObjTypes, ",Bubble,")
                || contains(ObjTypes, ",bub,")
                || contains(ObjTypes, ",EmObj,")
                || contains(ObjTypes, ",EmO,")
                || contains(ObjTypes, ",Cloud,")
                || contains(ObjTypes, ",Cld,")
                || contains(ObjTypes, ",GalNeb,")
                || contains(ObjTypes, ",GNe,")
                || contains(ObjTypes, ",BrNeb,")
                || contains(ObjTypes, ",BNe,")
                || contains(ObjTypes, ",DkNeb,")
                || contains(ObjTypes, ",DNe,")
                || contains(ObjTypes, ",RfNeb,")
                || contains(ObjTypes, ",RNe,")
                || contains(ObjTypes, ",MolCld,")
                || contains(ObjTypes, ",MoC,")
                || contains(ObjTypes, ",Globule,")
                || contains(ObjTypes, ",glb,")
                || contains(ObjTypes, ",denseCore,")
                || contains(ObjTypes, ",cor,")
                || contains(ObjTypes, ",HVCld,")
                || contains(ObjTypes, ",HVC,")
                || contains(ObjTypes, ",HII,")
                || contains(ObjTypes, ",PN,")
                || contains(ObjTypes, ",HIshell,")
                || contains(ObjTypes, ",sh,")
                || contains(ObjTypes, ",SNR?,")
                || contains(ObjTypes, ",SR?,")
                || contains(ObjTypes, ",SNR,")
                || contains(ObjTypes, ",Circumstellar,")
                || contains(ObjTypes, ",cir,")
                || contains(ObjTypes, ",outflow?,")
                || contains(ObjTypes, ",of?,")
                || contains(ObjTypes, ",Outflow,")
                || contains(ObjTypes, ",out,")
                || contains(ObjTypes, ",HH,")
                || contains(ObjTypes, ",YSO,")
                || contains(ObjTypes, ",Y*O,")
                || contains(ObjTypes, ",Ae*,")
                || contains(ObjTypes, ",Em*,")
                || contains(ObjTypes, ",Be*,")
                || contains(ObjTypes, ",SG*,")
                || contains(ObjTypes, ",sg*,")
                || contains(ObjTypes, ",post-AGB*,")
                || contains(ObjTypes, ",pA*,")
                || contains(ObjTypes, ",OH/IR,")
                || contains(ObjTypes, ",OH*,")
                || contains(ObjTypes, ",CH,")
                || contains(ObjTypes, ",CH*,")
                || contains(ObjTypes, ",pMS*,")
                || contains(ObjTypes, ",pr*,")
                || contains(ObjTypes, ",TTau*,")
                || contains(ObjTypes, ",TT*,")
                || contains(ObjTypes, ",WR*,")
                || contains(ObjTypes, ",Irregular_V*,")
                || contains(ObjTypes, ",Ir*,")
                || contains(ObjTypes, ",Orion_V*,")
                || contains(ObjTypes, ",Or*,")
                || contains(ObjTypes, ",Eruptive*,")
                || contains(ObjTypes, ",Er*,")
                || contains(ObjTypes, ",FUOr,")
                || contains(ObjTypes, ",FU*,")
                || contains(ObjTypes, ",Erupt*RCrB,")
                || contains(ObjTypes, ",RC*,")
                || contains(ObjTypes, ",Pulsar,")
                || contains(ObjTypes, ",Psr,")
                || contains(ObjTypes, ",BYDra,")
                || contains(ObjTypes, ",BY*,")
                || contains(ObjTypes, ",RSCVn,")
                || contains(ObjTypes, ",RS*,")
                || contains(ObjTypes, ",PulsV*,")
                || contains(ObjTypes, ",Pu*,")
                || contains(ObjTypes, ",RRLyr,")
                || contains(ObjTypes, ",RR*,")
                || contains(ObjTypes, ",Cepheid,")
                || contains(ObjTypes, ",Ce*,")
                || contains(ObjTypes, ",PulsV*delSct,")
                || contains(ObjTypes, ",dS*,")
                || contains(ObjTypes, ",PulsV*RVTau,")
                || contains(ObjTypes, ",RV*,")
                || contains(ObjTypes, ",PulsV*WVir,")
                || contains(ObjTypes, ",WV*,")
                || contains(ObjTypes, ",PulsV*bCep,")
                || contains(ObjTypes, ",bC*,")
                || contains(ObjTypes, ",deltaCep,")
                || contains(ObjTypes, ",cC*,")
                || contains(ObjTypes, ",gammaDor,")
                || contains(ObjTypes, ",gD*,")
                || contains(ObjTypes, ",pulsV*SX,")
                || contains(ObjTypes, ",SX*,")
                || contains(ObjTypes, ",LPV*,")
                || contains(ObjTypes, ",LP*,")
                || contains(ObjTypes, ",Mira,")
                || contains(ObjTypes, ",Mi*,")
                || contains(ObjTypes, ",semi-regV*,")
                || contains(ObjTypes, ",sr*,")
                || contains(ObjTypes, ",SN,")
                || contains(ObjTypes, ",SN*,")
                || contains(ObjTypes, ",Sub-stellar,")
                || contains(ObjTypes, ",su*,")
                || contains(ObjTypes, ",Planet?,")
                || contains(ObjTypes, ",Pl?,")
                || contains(ObjTypes, ",Galaxy,")
                || contains(ObjTypes, ",G,")
                || contains(ObjTypes, ",PartofG,")
                || contains(ObjTypes, ",PoG,")
                || contains(ObjTypes, ",GinCl,")
                || contains(ObjTypes, ",GiC,")
                || contains(ObjTypes, ",BClG,")
                || contains(ObjTypes, ",BiC,")
                || contains(ObjTypes, ",GinGroup,")
                || contains(ObjTypes, ",GiG,")
                || contains(ObjTypes, ",GinPair,")
                || contains(ObjTypes, ",GiP,")
                || contains(ObjTypes, ",High_z_G,")
                || contains(ObjTypes, ",HzG,")
                || contains(ObjTypes, ",AbsLineSystem,")
                || contains(ObjTypes, ",ALS,")
                || contains(ObjTypes, ",Ly-alpha_ALS,")
                || contains(ObjTypes, ",LyA,")
                || contains(ObjTypes, ",DLy-alpha_ALS,")
                || contains(ObjTypes, ",DLA,")
                || contains(ObjTypes, ",metal_ALS,")
                || contains(ObjTypes, ",mAL,")
                || contains(ObjTypes, ",Ly-limit_ALS,")
                || contains(ObjTypes, ",LLS,")
                || contains(ObjTypes, ",Broad_ALS,")
                || contains(ObjTypes, ",BAL,")
                || contains(ObjTypes, ",RadioG,")
                || contains(ObjTypes, ",rG,")
                || contains(ObjTypes, ",HII_G,")
                || contains(ObjTypes, ",H2G,")
                || contains(ObjTypes, ",LSB_G,")
                || contains(ObjTypes, ",LSB,")
                || contains(ObjTypes, ",AGN_Candidate,")
                || contains(ObjTypes, ",AG?,")
                || contains(ObjTypes, ",QSO_Candidate,")
                || contains(ObjTypes, ",Q?,")
                || contains(ObjTypes, ",Blazar_Candidate,")
                || contains(ObjTypes, ",Bz?,")
                || contains(ObjTypes, ",BLLac_Candidate,")
                || contains(ObjTypes, ",BL?,")
                || contains(ObjTypes, ",EmG,")
                || contains(ObjTypes, ",StarburstG,")
                || contains(ObjTypes, ",SBG,")
                || contains(ObjTypes, ",BlueCompG,")
                || contains(ObjTypes, ",bCG,")
                || contains(ObjTypes, ",LensedImage,")
                || contains(ObjTypes, ",LeI,")
                || contains(ObjTypes, ",LensedG,")
                || contains(ObjTypes, ",LeG,")
                || contains(ObjTypes, ",LensedQ,")
                || contains(ObjTypes, ",LeQ,")
                || contains(ObjTypes, ",AGN,")
                || contains(ObjTypes, ",LINER,")
                || contains(ObjTypes, ",LIN,")
                || contains(ObjTypes, ",Seyfert,")
                || contains(ObjTypes, ",SyG,")
                || contains(ObjTypes, ",Seyfert_1,")
                || contains(ObjTypes, ",Sy1,")
                || contains(ObjTypes, ",Seyfert_2,")
                || contains(ObjTypes, ",Sy2,")
                || contains(ObjTypes, ",Blazar,")
                || contains(ObjTypes, ",Bla,")
                || contains(ObjTypes, ",BLLac,")
                || contains(ObjTypes, ",BLL,")
                || contains(ObjTypes, ",OVV,")
                || contains(ObjTypes, ",QSO,");
    }

    private static boolean contains(final String value, final String pattern) {
        return value.contains(pattern);
    }
}
/*___oOo___*/
