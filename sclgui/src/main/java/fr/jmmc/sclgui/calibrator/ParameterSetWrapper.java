/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.calibrator;

import cds.savot.model.ParamSet;
import cds.savot.model.SavotParam;
import java.util.HashMap;

/**
 * Simple SavotParam wrapper to easily get / set values
 */
public final class ParameterSetWrapper {

    /** Parameter set */
    private final ParamSet paramSet;
    /** Parameter map keyed by id */
    private final HashMap<String, SavotParam> paramById;
    /** Parameter map keyed by name */
    private final HashMap<String, SavotParam> paramByName;

    ParameterSetWrapper(final ParamSet paramSet) {
        this.paramSet = paramSet;

        final int nParams = paramSet.getItemCount();

        this.paramById = new HashMap<String, SavotParam>(nParams);
        this.paramByName = new HashMap<String, SavotParam>(nParams);

        for (int i = 0; i < nParams; i++) {
            final SavotParam param = paramSet.getItemAt(i);

            final String id = param.getId();
            if (id.length() != 0) {
                paramById.put(id, param);
            }

            final String name = param.getName();
            if (name.length() != 0) {
                paramByName.put(name, param);
            }
        }
    }

    SavotParam getById(final String id) {
        return paramById.get(id);
    }

    public SavotParam get(final String name) {
        return paramByName.get(name);
    }

    public String getValue(final String name) {
        final SavotParam param = paramByName.get(name);
        return (param != null) ? param.getValue() : null;
    }

    public String getValue(final String name, final String def) {
        final String value = getValue(name);
        return (value != null) ? value : def;
    }

    void setValue(final String name, final String value) {
        final SavotParam param = paramByName.get(name);
        if (param != null) {
            param.setValue(value);
        }
    }

    void add(final String name, final String datatype, final String value) {
        add(newParam(name, datatype, value));
    }

    void add(final SavotParam param) {
        paramByName.put(param.getName(), param);
        paramSet.addItem(param);
    }

    static SavotParam newParam(final String name, final String datatype, final String value) {
        final SavotParam param = new SavotParam();
        param.setName(name);
        param.setDataType(datatype);
        if ("char".equals(datatype)) {
            param.setArraySize("*");
        }
        if (value != null) {
            param.setValue(value);
        }
        return param;
    }
}
