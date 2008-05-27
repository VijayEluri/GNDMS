/**
 * GetSliceByIdRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC2 Feb 27, 2008 (08:34:14 CST) WSDL2Java emitter.
 */

package de.zib.gndms.dspace.subpace.stubs;

public class GetSliceByIdRequest  implements java.io.Serializable {
    private de.zib.gndms.dspace.subpace.stubs.GetSliceByIdRequestSliceId sliceId;

    public GetSliceByIdRequest() {
    }

    public GetSliceByIdRequest(
           de.zib.gndms.dspace.subpace.stubs.GetSliceByIdRequestSliceId sliceId) {
           this.sliceId = sliceId;
    }


    /**
     * Gets the sliceId value for this GetSliceByIdRequest.
     * 
     * @return sliceId
     */
    public de.zib.gndms.dspace.subpace.stubs.GetSliceByIdRequestSliceId getSliceId() {
        return sliceId;
    }


    /**
     * Sets the sliceId value for this GetSliceByIdRequest.
     * 
     * @param sliceId
     */
    public void setSliceId(de.zib.gndms.dspace.subpace.stubs.GetSliceByIdRequestSliceId sliceId) {
        this.sliceId = sliceId;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetSliceByIdRequest)) return false;
        GetSliceByIdRequest other = (GetSliceByIdRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.sliceId==null && other.getSliceId()==null) || 
             (this.sliceId!=null &&
              this.sliceId.equals(other.getSliceId())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getSliceId() != null) {
            _hashCode += getSliceId().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetSliceByIdRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://dspace.gndms.zib.de/DSpace/Subspace", ">GetSliceByIdRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sliceId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://dspace.gndms.zib.de/DSpace/Subspace", "sliceId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://dspace.gndms.zib.de/DSpace/Subspace", ">>GetSliceByIdRequest>sliceId"));
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
