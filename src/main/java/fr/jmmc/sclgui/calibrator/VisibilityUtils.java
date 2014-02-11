/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.calibrator;

import fr.jmmc.jmal.model.function.math.Functions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class dedicated to computeVisibility visiblities + errors
 * @author bourgesl
 */
public final class VisibilityUtils {

    /** Logger */
    private final static Logger _logger = LoggerFactory.getLogger(VisibilityUtils.class.getName());

    private VisibilityUtils() {
        // forbidden constructor
    }

    /**
     * Compute star visibility.
     *
     * This method computes the potential calibrator visibility according to the
     * angular diameter, the length of the maximal baseline and the wavelength
     *
     * @param angDiam angular diameter of the star (mas)
     * @param angDiamError relative error on the angular diameter
     * @param baseMax maximal baseline length(m)
     * @param wlen wavelength (m)
     * @param visibilities the computed visibilities (vis, visErr, vis2, vis2Err)
     */
    static void computeVisibility(final double angDiam, final double angDiamError, 
                                  final double baseMax, final double wlen, 
                                  final VisibilityResult visibilities) {
        
        final double freq = baseMax / wlen;

        // Compute V 
        final double vis = Math.abs(Functions.computeDisk(freq, angDiam));

        // and its associated error for Diameter Uniform Disc 
        final double err = Functions.computeDiskError(freq, angDiam, angDiamError);

        visibilities.vis = vis;
        visibilities.visErr = err;

        // Compute V2 
        visibilities.vis2 = vis * vis;

        // and its associated error for Diameter Uniform Disc 
        // d(Vis2) = 2 x V x dV 
        visibilities.vis2Err = 2.0 * vis * err;

        // Print out result 
        if (_logger.isDebugEnabled()) {
            _logger.debug("Diam={} ({}) - base={} - wlen={}", angDiam, angDiamError, baseMax, wlen);
            _logger.debug("V(dV), V2(dV2)= {} ({}), {} ({})",
                    visibilities.vis, visibilities.visErr,
                    visibilities.vis2, visibilities.vis2Err);
        }
    }

    /**
     * Visibility result
     */
    static final class VisibilityResult {

        /** values */
        double vis, visErr, vis2, vis2Err;
    }
}
