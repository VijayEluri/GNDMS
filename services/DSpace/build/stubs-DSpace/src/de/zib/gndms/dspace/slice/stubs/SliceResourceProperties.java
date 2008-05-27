/**
 * SliceResourceProperties.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC2 Feb 27, 2008 (08:34:14 CST) WSDL2Java emitter.
 */

package de.zib.gndms.dspace.slice.stubs;

public class SliceResourceProperties  implements java.io.Serializable {
    private java.util.Calendar currentTime;
    private java.util.Calendar terminationTime;
    private types.StorageSizeT totalStorageSize;
    private types.SliceKindT sliceKind;
    private java.lang.String sliceId;
    private de.zib.gndms.dspace.subpace.stubs.types.SubspaceReference subspaceReference;

    public SliceResourceProperties() {
    }

    public SliceResourceProperties(
           java.util.Calendar currentTime,
           java.lang.String sliceId,
           types.SliceKindT sliceKind,
           de.zib.gndms.dspace.subpace.stubs.types.SubspaceReference subspaceReference,
           java.util.Calendar terminationTime,
           types.StorageSizeT totalStorageSize) {
           this.currentTime = currentTime;
           this.terminationTime = terminationTime;
           this.totalStorageSize = totalStorageSize;
           this.sliceKind = sliceKind;
           this.sliceId = sliceId;
           this.subspaceReference = subspaceReference;
    }


    /**
     * Gets the currentTime value for this SliceResourceProperties.
     * 
     * @return currentTime
     */
    public java.util.Calendar getCurrentTime() {
        return currentTime;
    }


    /**
     * Sets the currentTime value for this SliceResourceProperties.
     * 
     * @param currentTime
     */
    public void setCurrentTime(java.util.Calendar currentTime) {
        this.currentTime = currentTime;
    }


    /**
     * Gets the terminationTime value for this SliceResourceProperties.
     * 
     * @return terminationTime
     */
    public java.util.Calendar getTerminationTime() {
        return terminationTime;
    }


    /**
     * Sets the terminationTime value for this SliceResourceProperties.
     * 
     * @param terminationTime
     */
    public void setTerminationTime(java.util.Calendar terminationTime) {
        this.terminationTime = terminationTime;
    }


    /**
     * Gets the totalStorageSize value for this SliceResourceProperties.
     * 
     * @return totalStorageSize
     */
    public types.StorageSizeT getTotalStorageSize() {
        return totalStorageSize;
    }


    /**
     * Sets the totalStorageSize value for this SliceResourceProperties.
     * 
     * @param totalStorageSize
     */
    public void setTotalStorageSize(types.StorageSizeT totalStorageSize) {
        this.totalStorageSize = totalStorageSize;
    }


    /**
     * Gets the sliceKind value for this SliceResourceProperties.
     * 
     * @return sliceKind
     */
    public types.SliceKindT getSliceKind() {
        return sliceKind;
    }


    /**
     * Sets the sliceKind value for this SliceResourceProperties.
     * 
     * @param sliceKind
     */
    public void setSliceKind(types.SliceKindT sliceKind) {
        this.sliceKind = sliceKind;
    }


    /**
     * Gets the sliceId value for this SliceResourceProperties.
     * 
     * @return sliceId
     */
    public java.lang.String getSliceId() {
        return sliceId;
    }


    /**
     * Sets the sliceId value for this SliceResourceProperties.
     * 
     * @param sliceId
     */
    public void setSliceId(java.lang.String sliceId) {
        this.sliceId = sliceId;
    }


    /**
     * Gets the subspaceReference value for this SliceResourceProperties.
     * 
     * @return subspaceReference
     */
    public de.zib.gndms.dspace.subpace.stubs.types.SubspaceReference getSubspaceReference() {
        return subspaceReference;
    }


    /**
     * Sets the subspaceReference value for this SliceResourceProperties.
     * 
     * @param subspaceReference
     */
    public void setSubspaceReference(de.zib.gndms.dspace.subpace.stubs.types.SubspaceReference subspaceReference) {
        this.subspaceReference = subspaceReference;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SliceResourceProperties)) return false;
        SliceResourceProperties other = (SliceResourceProperties) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.currentTime==null && other.getCurrentTime()==null) || 
             (this.currentTime!=null &&
              this.currentTime.equals(other.getCurrentTime()))) &&
            ((this.terminationTime==null && other.getTerminationTime()==null) || 
             (this.terminationTime!=null &&
              this.terminationTime.equals(other.getTerminationTime()))) &&
            ((this.totalStorageSize==null && other.getTotalStorageSize()==null) || 
             (this.totalStorageSize!=null &&
              this.totalStorageSize.equals(other.getTotalStorageSize()))) &&
            ((this.sliceKind==null && other.getSliceKind()==null) || 
             (this.sliceKind!=null &&
              this.sliceKind.equals(other.getSliceKind()))) &&
            ((this.sliceId==null && other.getSliceId()==null) || 
             (this.sliceId!=null &&
              this.sliceId.equals(other.getSliceId()))) &&
            ((this.subspaceReference==null && other.getSubspaceReference()==null) || 
             (this.subspaceReference!=null &&
              this.subspaceReference.equals(other.getSubspaceReference())));
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
        if (getCurrentTime() != null) {
            _hashCode += getCurrentTime().hashCode();
        }
        if (getTerminationTime() != null) {
            _hashCode += getTerminationTime().hashCode();
        }
        if (getTotalStorageSize() != null) {
            _hashCode += getTotalStorageSize().hashCode();
        }
        if (getSliceKind() != null) {
            _hashCode += getSliceKind().hashCode();
        }
        if (getSliceId() != null) {
            _hashCode += getSliceId().hashCode();
        }
        if (getSubspaceReference() != null) {
            _hashCode += getSubspaceReference().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SliceResourceProperties.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://dspace.gndms.zib.de/DSpace/Slice", ">SliceResourceProperties"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currentTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceLifetime-1.2-draft-01.xsd", "CurrentTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminationTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceLifetime-1.2-draft-01.xsd", "TerminationTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("totalStorageSize");
        elemField.setXmlName(new javax.xml.namespace.QName("http://gndms.zib.de/common/types", "TotalStorageSize"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://gndms.zib.de/common/types", "StorageSizeT"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sliceKind");
        elemField.setXmlName(new javax.xml.namespace.QName("http://gndms.zib.de/common/types", "SliceKind"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://gndms.zib.de/common/types", "SliceKindT"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sliceId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://gndms.zib.de/common/types", "SliceId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("subspaceReference");
        elemField.setXmlName(new javax.xml.namespace.QName("http://dspace.gndms.zib.de/DSpace/Subspace/types", "SubspaceReference"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://dspace.gndms.zib.de/DSpace/Subspace/types", ">SubspaceReference"));
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
