/**
 * SclwsLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fr.jmmc.sclws_wsdl;

public class SclwsLocator extends org.apache.axis.client.Service implements fr.jmmc.sclws_wsdl.Sclws {

/**
 * gSOAP 2.7.11 generated service definition
 */

    public SclwsLocator() {
    }


    public SclwsLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public SclwsLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for sclws
    private java.lang.String sclws_address = "http://jmmc.fr:8079";

    public java.lang.String getsclwsAddress() {
        return sclws_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String sclwsWSDDServiceName = "sclws";

    public java.lang.String getsclwsWSDDServiceName() {
        return sclwsWSDDServiceName;
    }

    public void setsclwsWSDDServiceName(java.lang.String name) {
        sclwsWSDDServiceName = name;
    }

    public fr.jmmc.sclws_wsdl.SclwsPortType getsclws() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(sclws_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getsclws(endpoint);
    }

    public fr.jmmc.sclws_wsdl.SclwsPortType getsclws(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            fr.jmmc.sclws_wsdl.SclwsStub _stub = new fr.jmmc.sclws_wsdl.SclwsStub(portAddress, this);
            _stub.setPortName(getsclwsWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setsclwsEndpointAddress(java.lang.String address) {
        sclws_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (fr.jmmc.sclws_wsdl.SclwsPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                fr.jmmc.sclws_wsdl.SclwsStub _stub = new fr.jmmc.sclws_wsdl.SclwsStub(new java.net.URL(sclws_address), this);
                _stub.setPortName(getsclwsWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("sclws".equals(inputPortName)) {
            return getsclws();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://jmmc.fr:8079/sclws.wsdl", "sclws");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://jmmc.fr:8079/sclws.wsdl", "sclws"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("sclws".equals(portName)) {
            setsclwsEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
