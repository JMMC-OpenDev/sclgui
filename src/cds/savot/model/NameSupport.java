/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package cds.savot.model;

/**
 * This interface defines name attribute handling (get / set)
 * @author bourgesl
 */
public interface NameSupport {

    /**
     * Set name attribute
     * 
     * @param name
     */
    public void setName(final String name);

    /**
     * Get name attribute
     * 
     * @return String
     */
    public String getName();
}
