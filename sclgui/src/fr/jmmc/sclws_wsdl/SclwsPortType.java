/**
 * SclwsPortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fr.jmmc.sclws_wsdl;

public interface SclwsPortType extends java.rmi.Remote {

    /**
     * Service definition of function ns__GetCalOpenSession
     */
    public java.lang.String getCalOpenSession() throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__GetCalSearchCal
     */
    public java.lang.String getCalSearchCal(java.lang.String param2, java.lang.String param3) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__GetCalQueryStatus
     */
    public java.lang.String getCalQueryStatus(java.lang.String param5) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__GetCalCancelSession
     */
    public boolean getCalCancelSession(java.lang.String param7) throws java.rmi.RemoteException;
}
