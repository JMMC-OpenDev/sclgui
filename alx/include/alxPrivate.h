#ifndef alxPrivate_H
#define alxPrivate_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: alxPrivate.h,v 1.2 2005-01-21 08:10:30 gluck Exp $"
 * 
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * 
 * scetre    20-Sep-2004  Created
 * gzins     20-Dec-2004  Fixed wrong module name and removed unused definition
 * gzins     12-Jan-2005  Move private declaration (structures, macros, ...)
 *                        from alx.h
 *
 ******************************************************************************/

/**
 * \file
 * alxPrivate header file 
 */

/* The following piece of code alternates the linkage type to C for all 
functions declared within the braces, which is necessary to use the 
functions in C++-code.
*/
#ifdef __cplusplus
extern "C" {
#endif

/* Module name */ 
#define MODULE_ID "alx" 

/* Pi */
#ifndef M_PI 
#define M_PI 3.14159265358979323846264338327950288
#endif

/******** Macros and structure for angular diameter computation */
/* Number of color indexes used to compute the angular diameter : (B-V), (V-R),
 * (V-K) */
#define alxNB_COLOR_INDEXES 3
    
/*
 * Polynomial to compute the angular diameter is made by 6 coefficients which
 * are stored in file.
 */
#define alxNB_POLYNOMIAL_COEFF_DIAMETER 6
/*
 * Structure of the an coefficient table for compute angular diameter
 */
typedef struct
{
    mcsLOGICAL loaded;
    char      *fileName;
    mcsFLOAT   coeff[alxNB_COLOR_INDEXES][alxNB_POLYNOMIAL_COEFF_DIAMETER];
} alxPOLYNOMIAL_ANGULAR_DIAMETER;

/******** Macros and structure for missing magnitudes computation */
/* 
 * The maximum of line which can be found in the table is 61.
 * This represents the number of spectral types that it is possible to find in
 * the relation 
 *  O5 to O9  ---->  line 1 to  5
 *  B0 to B9  ---->  line 6 to 15
 *  A0 to A9  ---->  line 16 to 25
 *  F0 to F8  ---->  line 26 to 34
 *  G0 to G9  ---->  line 35 to 44
 *  K0 to K7  ---->  line 45 to 52
 *  M0 to M8  ---->  line 53 to 61
 */
#define alxNB_SPECTRAL_TYPES 61
/*
 * spectral type structure.
 *
 * A spectral type is build with a code (O, B, A, F, G, K, M),
 * a number between 0 and 9, and a light class which can be I,II,III,IV,etc...
 */
typedef struct
{
    char        code;          /* Code of the spectral type */
    mcsFLOAT    quantity;      /* Quantity of the spectral subtype */
    mcsSTRING32 lightClass;    /* Luminosity class*/
} alxSPECTRAL_TYPE;
/*
 * The number of column in the table comes from the number of difference it is
 * necessary to have to compute the missing magnitude
 */
#define alxNB_DIFF_MAG 10
/*
 * Colum identificator of magnitude difference.
 */
typedef enum
{
    alxB_V,                 /* value of MagB - MagV */
    alxV_I,                 /* value of MagV - MagI */
    alxV_R,                 /* value of MagV - MagR */
    alxI_J,                 /* value of MagI - MagJ */
    alxJ_H,                 /* value of MagJ - MagH */
    alxJ_K,                 /* value of MagJ - MagK */
    alxK_L,                 /* value of MagK - MagL */
    alxL_M,                 /* value of MagL - MagM */
    alxMv,
    alxM    
} alxDIFF_MAG;

/*
 * Structure of the color table.
 */
typedef struct
{
    mcsLOGICAL       loaded;
    char            *fileName;
    mcsINT32         nbLines;
    alxSPECTRAL_TYPE spectralType[alxNB_SPECTRAL_TYPES];
    mcsFLOAT         index[alxNB_SPECTRAL_TYPES][alxNB_DIFF_MAG];
} alxCOLOR_TABLE;

/*
 * Type of star.
 */
#define alxNB_STAR_TYPES 3
typedef enum
{
    alxDWARF,
    alxGIANT,
    alxSUPER_GIANT
} alxSTAR_TYPE;

/******** Macros and structure for corrected magnitudes computation */
/*
 * Structure of the extinction ratio table.
 */
/*
 * Fitzpatrick Id for M, B, V, R, I, J, H and K bands, used to retreive the
 * extinction ratio.
 */
typedef enum
{
   alxFITZ_M = 0,  /* Id for band M */
   alxFITZ_L,      /* Id for band L */
   alxFITZ_K,      /* Id for band K */
   alxFITZ_H,      /* Id for band H */
   alxFITZ_J,      /* Id for band J */
   alxFITZ_I,      /* Id for band I */
   alxFITZ_R,      /* Id for band R */
   alxFITZ_V,      /* Id for band V */
   alxFITZ_B       /* Id for band B */
} alxFITZ_ID;
#define alxNB_FITZ_IDS 9

typedef struct
{
    mcsLOGICAL  loaded;    
    char       *fileName;
    mcsFLOAT    rc[alxNB_FITZ_IDS];
} alxEXTINCTION_RATIO_TABLE;
/*
 * Polynomial to compute the interstellar absorbtion is made by 4 coefficients
 * which are stored in file. Polynomial coefficients is given versus the
 * galactic longitude by step of 10 degrees; i.e. there are 36 polynomial
 * coefficient sets for all longitude range [0..360�].
 */
#define alxNB_MAX_LONGITUDE_STEPS         (360/10)
#define alxNB_POLYNOMIAL_COEFF_ABSORPTION  4
/*
 * Structure of the an coefficient table for compute interstellar absorbtion
 */
typedef struct
{
    mcsLOGICAL loaded;
    char      *fileName;
    mcsINT32   nbLines;
    mcsFLOAT   longMin[alxNB_MAX_LONGITUDE_STEPS];
    mcsFLOAT   longMax[alxNB_MAX_LONGITUDE_STEPS];
    mcsFLOAT   coeff[alxNB_MAX_LONGITUDE_STEPS][alxNB_POLYNOMIAL_COEFF_ABSORPTION];
} alxPOLYNOMIAL_INTERSTELLAR_ABSORPTION;

#ifdef __cplusplus
}
#endif

#endif /*!alxPrivate_H*/

/*___oOo___*/
