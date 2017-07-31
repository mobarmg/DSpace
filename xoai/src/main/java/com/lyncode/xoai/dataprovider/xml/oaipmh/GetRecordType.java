//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-147 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.01.13 at 08:24:23 PM WET 
//

package com.lyncode.xoai.dataprovider.xml.oaipmh;

import com.lyncode.xoai.dataprovider.exceptions.WritingXmlException;
import com.lyncode.xoai.dataprovider.xml.XMLWritable;
import com.lyncode.xoai.dataprovider.xml.XmlOutputContext;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.stream.XMLStreamException;

/**
 *
 * Java class for GetRecordType complex type.
 *
 *
 * The following schema fragment specifies the expected content contained within
 * this class.
 *











 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetRecordType", propOrder = {"record"})
public class GetRecordType implements XMLWritable {

    @XmlElement(required = true)
    protected RecordType record;

    /**
     * Gets the value of the record property.
     *
     * @return possible object is ;
     */
    public RecordType getRecord() {
        return record;
    }

    /**
     * Sets the value of the record property.
     *
     * @param value allowed object is ;
     */
    public void setRecord(RecordType value) {
        this.record = value;
    }

    @Override
    public void write(XmlOutputContext writer) throws WritingXmlException {
        try {
            writer.getWriter().writeStartElement("record");
            if (this.record != null)
                this.record.write(writer);
            writer.getWriter().writeEndElement();
        } catch (XMLStreamException e) {
            throw new WritingXmlException(e);
        }
    }

}
