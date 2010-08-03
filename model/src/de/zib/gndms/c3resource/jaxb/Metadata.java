package de.zib.gndms.c3resource.jaxb;
/*
 * Copyright 2008-2010 Zuse Institute Berlin (ZIB)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-661 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.12.11 at 02:32:25 PM CET 
//



import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.apache.cxf.jaxb.JAXBToStringBuilder;
import org.apache.cxf.jaxb.JAXBToStringStyle;


/**
 * 
 *         Description of an OAI-PMH Interface. This interface is used to
 *         publish ISO 19139 documents to be harvested by the C3-Grid
 *         information service. For a specification of the OAI-PMH see
 *         http://www.openarchives.org/OAI/openarchivesprotocol.html#MetadataNamespaces
 *         .
 *       
 * 
 * <p>Java class for metadata complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="metadata">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="baseUrl" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="metadataPrefix" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="set">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="setSpec" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;any/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "metadata", namespace = "http://c3grid.de/language/resouces/2007-05-16-resources.xsd", propOrder = {
    "baseUrl",
    "metadataPrefix",
    "set",
    "any"
})
public class Metadata {

    @XmlElement(namespace = "http://c3grid.de/language/resouces/2007-05-16-resources.xsd", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String baseUrl;
    @XmlElement(namespace = "http://c3grid.de/language/resouces/2007-05-16-resources.xsd", required = true)
    protected String metadataPrefix;
    @XmlElement(namespace = "http://c3grid.de/language/resouces/2007-05-16-resources.xsd", required = true)
    protected Metadata.Set set;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute(required = true)
    protected String id;

    /**
     * Gets the value of the baseUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Sets the value of the baseUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseUrl(String value) {
        this.baseUrl = value;
    }

    /**
     * Gets the value of the metadataPrefix property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMetadataPrefix() {
        return metadataPrefix;
    }

    /**
     * Sets the value of the metadataPrefix property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMetadataPrefix(String value) {
        this.metadataPrefix = value;
    }

    /**
     * Gets the value of the set property.
     * 
     * @return
     *     possible object is
     *     {@link Metadata.Set }
     *     
     */
    public Metadata.Set getSet() {
        return set;
    }

    /**
     * Sets the value of the set property.
     * 
     * @param value
     *     allowed object is
     *     {@link Metadata.Set }
     *     
     */
    public void setSet(Metadata.Set value) {
        this.set = value;
    }

    /**
     * Gets the value of the any property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Generates a String representation of the contents of this type.
     * This is an extension method, produced by the 'ts' xjc plugin
     * 
     */
    @Override
    public String toString() {
        return JAXBToStringBuilder.valueOf(this, JAXBToStringStyle.MULTI_LINE_STYLE);
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="setSpec" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "setSpec"
    })
    public static class Set {

        @XmlElement(namespace = "http://c3grid.de/language/resouces/2007-05-16-resources.xsd", required = true)
        protected Object setSpec;

        /**
         * Gets the value of the setSpec property.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     
         */
        public Object getSetSpec() {
            return setSpec;
        }

        /**
         * Sets the value of the setSpec property.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     
         */
        public void setSetSpec(Object value) {
            this.setSpec = value;
        }

        /**
         * Generates a String representation of the contents of this type.
         * This is an extension method, produced by the 'ts' xjc plugin
         * 
         */
        @Override
        public String toString() {
            return JAXBToStringBuilder.valueOf(this, JAXBToStringStyle.MULTI_LINE_STYLE);
        }

    }

}
