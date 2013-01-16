/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package cds.savot.model;

/**
 * This interface defines ref attribute handling (get / set)
 * @author bourgesl
 */
public interface RefSupport {

    /**
     * Set the ref attribute
     * 
     * @param ref
     *            ref
     */
    public void setRef(final String ref);
    /**
     * Get the ref attribute
     * 
     * @return a String
     */
    public String getRef();
}
