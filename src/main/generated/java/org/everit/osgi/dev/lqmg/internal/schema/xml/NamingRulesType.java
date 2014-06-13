/**
 * This file is part of Everit - Liquibase-QueryDSL Model Generator.
 *
 * Everit - Liquibase-QueryDSL Model Generator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Everit - Liquibase-QueryDSL Model Generator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Everit - Liquibase-QueryDSL Model Generator.  If not, see <http://www.gnu.org/licenses/>.
 */
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.06.13 at 12:42:47 PM CEST 
//


package org.everit.osgi.dev.lqmg.internal.schema.xml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NamingRulesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NamingRulesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="unbounded">
 *         &lt;element name="classNameRule" type="{http://everit.org/lqmg}ClassNameRuleType" minOccurs="0"/>
 *         &lt;element name="regexRule" type="{http://everit.org/lqmg}RegexRuleType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NamingRulesType", propOrder = {
    "classNameRuleAndRegexRule"
})
public class NamingRulesType {

    @XmlElements({
        @XmlElement(name = "classNameRule", type = ClassNameRuleType.class),
        @XmlElement(name = "regexRule", type = RegexRuleType.class)
    })
    protected List<AbstractNamingRuleType> classNameRuleAndRegexRule;

    /**
     * Gets the value of the classNameRuleAndRegexRule property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the classNameRuleAndRegexRule property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClassNameRuleAndRegexRule().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClassNameRuleType }
     * {@link RegexRuleType }
     * 
     * 
     */
    public List<AbstractNamingRuleType> getClassNameRuleAndRegexRule() {
        if (classNameRuleAndRegexRule == null) {
            classNameRuleAndRegexRule = new ArrayList<AbstractNamingRuleType>();
        }
        return this.classNameRuleAndRegexRule;
    }

}
