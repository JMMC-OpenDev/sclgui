/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.calibrator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author bourgesl
 */
public interface StarProperty {

    /** Logger */
    static final Logger _logger = LoggerFactory.getLogger(StarProperty.class.getName());    
    
    /** shared empty star property to reduce memory footprint */
    StarProperty EMPTY_STAR_PROPERTY = new ObjectStarProperty(null, Origin.KEY_ORIGIN_NONE, Confidence.KEY_CONFIDENCE_NO);

    /**
     * Get the value of the star property as a Boolean object.
     *
     * @return a Boolean object representing the star property value.
     */
    Boolean getBoolean();

    /**
     * Get the value of the star property as a Boolean object.
     *
     * @return a Boolean object representing the star property value.
     */
    boolean getBooleanValue();

    /**
     * Get the confidence of the star property as a Confidence enum.
     *
     * @return a Confidence enum representing the star property confidence index.
     */
    Confidence getConfidence();

    /**
     * Get the confidence index of the star property as an integer.
     *
     * @return Confidence index as integer representing the star property confidence index.
     */
    int getConfidenceIndex();

    /**
     * Get the value of the star property as a Double object.
     *
     * @return a Double object representing the star property value.
     */
    Double getDouble();

    /**
     * Get the value of the star property as a Double object.
     *
     * @return a Double object representing the star property value.
     */
    double getDoubleValue();

    /**
     * Get the value of the star property as an Integer object.
     *
     * @return an Integer object representing the star property value.
     */
    Integer getInteger();

    /**
     * Get the value of the star property as an Integer object.
     *
     * @return an Integer object representing the star property value.
     */
    int getIntegerValue();

    /**
     * Get the origin of the star property as an Origin enum.
     *
     * @return an Origin enum representing the star property origin.
     */
    Origin getOrigin();

    /**
     * Get the origin index of the star property as an integer.
     *
     * @return Origin index as integer representing the star property origin index.
     */
    int getOriginIndex();

    /**
     * Get the value of the star property as a String object.
     *
     * @return a String object representing the star property value or null if undefined
     */
    String getString();

    /**
     * Get the value of the star property as a String object.
     *
     * @return a String object representing the star property value.
     */
    String getStringValue();

    /**
     * Get the value of the star property in its original form.
     *
     * @return an Object representing the star property value.
     */
    Object getValue();

    /**
     * Return whether the star property has a confidence index.
     *
     * @return true if a confidence index is set, false otherwise.
     */
    boolean hasConfidence();

    /**
     * Return whether the star property has a catalog origin (ie not undefined nor computed and (high or no) confidence index).
     *
     * @return true if a origin index is set, false otherwise.
     */
    boolean hasOrigin();

    /**
     * Return whether the star property has a value.
     *
     * @return true if a value is set, false otherwise.
     */
    boolean hasValue();

    /**
     * Update the star property with new given values (use with care).
     * @param value the new star property value.
     * @param originIndex the origin of the star property
     * @param confidenceIndex the confidence index of the star property
     */
    void set(final Object value, final int originIndex, final int confidenceIndex);

    /**
     * Set the value of the star property.
     *
     * @param value the new star property value.
     */
    void setValue(final Object value);
}
