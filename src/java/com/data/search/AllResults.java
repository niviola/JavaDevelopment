
package com.data.search;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Test_Results")

public class AllResults {
    private List<SearchResult> list = new ArrayList<>();
    
    
@XmlElement(name = "List_of_Results")
    public List<SearchResult> getList() {
        return list;
    }

    public void setList(List<SearchResult> list) {
        this.list = list;
    }
}
