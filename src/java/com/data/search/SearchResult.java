package com.data.search;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

//@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = {"title", "link", "description", "resultsMap"})
@XmlRootElement(name = "Result")
public class SearchResult {

//    @XmlAttribute
    private String title;
//    @XmlAttribute
    private String link;
    @XmlElement
    private String description;
private Map<String, String> resultsMap = new HashMap(); 

    SearchResult(String title, String link, String description) {
        this.title = title;
        this.link = link;
        this.description = description;
    }

    SearchResult() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    //@XmlTransient
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getResultsMap() {
        return resultsMap; // searched text table
    }

    public void setResultsMap(Map<String, String> resultsMap) {
        this.resultsMap = resultsMap;
    }

}
