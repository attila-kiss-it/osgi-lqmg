/*
 * Copyright (C) 2011 Everit Kft. (http://www.everit.biz)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.10.14 at 03:21:46 PM CEST 
//


package org.everit.persistence.lqmg.internal.schema.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LQMGType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LQMGType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="namingRules" type="{http://everit.org/lqmg}NamingRulesType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="defaultSchema" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="defaultPackage" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="defaultPrefix" type="{http://www.w3.org/2001/XMLSchema}string" default="Q" />
 *       &lt;attribute name="defaultSuffix" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LQMGType", propOrder = {
    "namingRules"
})
public class LQMGType {

    @XmlElement(required = true)
    protected NamingRulesType namingRules;
    @XmlAttribute(name = "defaultSchema")
    protected String defaultSchema;
    @XmlAttribute(name = "defaultPackage")
    protected String defaultPackage;
    @XmlAttribute(name = "defaultPrefix")
    protected String defaultPrefix;
    @XmlAttribute(name = "defaultSuffix")
    protected String defaultSuffix;

    /**
     * Gets the value of the namingRules property.
     * 
     * @return
     *     possible object is
     *     {@link NamingRulesType }
     *     
     */
    public NamingRulesType getNamingRules() {
        return namingRules;
    }

    /**
     * Sets the value of the namingRules property.
     * 
     * @param value
     *     allowed object is
     *     {@link NamingRulesType }
     *     
     */
    public void setNamingRules(NamingRulesType value) {
        this.namingRules = value;
    }

    /**
     * Gets the value of the defaultSchema property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultSchema() {
        return defaultSchema;
    }

    /**
     * Sets the value of the defaultSchema property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultSchema(String value) {
        this.defaultSchema = value;
    }

    /**
     * Gets the value of the defaultPackage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultPackage() {
        return defaultPackage;
    }

    /**
     * Sets the value of the defaultPackage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultPackage(String value) {
        this.defaultPackage = value;
    }

    /**
     * Gets the value of the defaultPrefix property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultPrefix() {
        if (defaultPrefix == null) {
            return "Q";
        } else {
            return defaultPrefix;
        }
    }

    /**
     * Sets the value of the defaultPrefix property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultPrefix(String value) {
        this.defaultPrefix = value;
    }

    /**
     * Gets the value of the defaultSuffix property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultSuffix() {
        return defaultSuffix;
    }

    /**
     * Sets the value of the defaultSuffix property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultSuffix(String value) {
        this.defaultSuffix = value;
    }

}