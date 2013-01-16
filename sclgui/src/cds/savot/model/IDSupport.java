/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package cds.savot.model;

/**
 * This interface defines id attribute handling (get / set)
 * @author bourgesl
 */
public interface IDSupport {

    /**
     * Set ID attribute
     * 
     * @param id
     */
    public void setId(final String id);

    /**
     * Get ID attribute
     * 
     * @return String
     */
    public String getId();
}
