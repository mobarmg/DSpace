//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-147 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.01.13 at 08:24:23 PM WET 
//

package com.lyncode.xoai.serviceprovider.oaipmh.spec;

import javax.xml.bind.annotation.*;
import java.util.Date;

/**
 * A resumptionToken may have 3 optional attributes and can be used in ListSets,
 * ListIdentifiers, ListRecords responses.
 *
 *
 * Java class for resumptionTokenType complex type.
 *
 *
 * The following schema fragment specifies the expected content contained within
 * this class.
 *











 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resumptionTokenType", propOrder = {"value"})
public class ResumptionTokenType {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "expirationDate")
    @XmlSchemaType(name = "dateTime")
    protected Date expirationDate;
    @XmlAttribute(name = "completeListSize")
    @XmlSchemaType(name = "positiveInteger")
    protected long completeListSize;
    @XmlAttribute(name = "cursor")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected long cursor;

    /**
     * Gets the value of the value property.
     *
     * @return possible object is ;
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     *
     * @param value allowed object is ;
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the expirationDate property.
     *
     * @return possible object is
     */
    public Date getExpirationDate() {
        return expirationDate;
    }

    /**
     * Sets the value of the expirationDate property.
     *
     * @param value allowed object is
     */
    public void setExpirationDate(Date value) {
        this.expirationDate = value;
    }

    /**
     * Gets the value of the completeListSize property.
     *
     * @return possible object is ;
     */
    public long getCompleteListSize() {
        return completeListSize;
    }

    /**
     * Sets the value of the completeListSize property.
     *
     * @param value allowed object is ;
     */
    public void setCompleteListSize(long value) {
        this.completeListSize = value;
    }

    /**
     * Gets the value of the cursor property.
     *
     * @return possible object is ;
     */
    public long getCursor() {
        return cursor;
    }

    /**
     * Sets the value of the cursor property.
     *
     * @param value allowed object is ;
     */
    public void setCursor(long value) {
        this.cursor = value;
    }

}
