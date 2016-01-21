package com.data.search;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class XmlUtil {

    private String path;
    AllResults results;

    public void setPath(String path) {
        this.path = path;
    }

    ArrayList<SearchResult> deserializeFromXML(String filename) {
        ArrayList<SearchResult> list = new ArrayList<>();
        File file = new File(path + filename);
        try {

            JAXBContext jaxbContext = JAXBContext.newInstance(AllResults.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            AllResults all = (AllResults) jaxbUnmarshaller.unmarshal(file);
            list = (ArrayList<SearchResult>) all.getList();
        } catch (JAXBException ex) {
            Logger.getLogger(XmlUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

    public void serialize2XML(ArrayList<SearchResult> list, String filename) {
        AllResults allresults = new AllResults();
        allresults.getList().addAll(list);

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(AllResults.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(allresults, new File(path + filename));
            jaxbMarshaller.marshal(allresults, System.out);
        } catch (JAXBException ex) {
            Logger.getLogger(XmlUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void buildXML(AllResults results) throws TransformerConfigurationException, TransformerException {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = docFactory.newDocumentBuilder();

            Document doc = builder.newDocument();
            Element rootElement = doc.createElement("Countries");
            doc.appendChild(rootElement);

            List<SearchResult> listofresults = results.getList();
            for (SearchResult result : listofresults) {

                String name = result.getDescription();
                String capital = result.getLink();
                String continent = result.getTitle();

                Element e_country = doc.createElement("Country");
                rootElement.appendChild(e_country);

                Element e_name = doc.createElement("Country_Name");
                e_name.setTextContent(name);
                e_country.appendChild(e_name);

                Element e_capital = doc.createElement("Country_Capital");
                e_capital.setTextContent(capital);
                e_country.appendChild(e_capital);
                
                Element e_continent = doc.createElement("Country_Continent");
                e_continent.setTextContent(continent);
                e_country.appendChild(e_continent);
                
//                Element e_population = doc.createElement("Country_Population");
//                e_population.setTextContent(Integer.toString(population));
//                e_country.appendChild(e_population);
//
//                List<City> cities = country.getCities();
//                for(City c : cities){
//                    Element e_city = doc.createElement("Country_City");
//                    e_country.appendChild(e_city);
//                    
//                    String town = c.getTown();
//                    Element e_town = doc.createElement("town");
//                    e_town.setTextContent(town);
//                    e_city.appendChild(e_town);
//                    
//                    int people = c.getPeople();
//                    Element e_people = doc.createElement("people");
//                    e_people.setTextContent(Integer.toString(people));
//                    e_city.appendChild(e_people);
//
//                }
                
            }
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File("test_countries.xml"));

                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		transformer.transform(source, result);
                StreamResult xmlOutput = new StreamResult(new StringWriter());
                transformer.transform(source, xmlOutput);
            
                System.out.println(xmlOutput.getWriter().toString());

        } catch (ParserConfigurationException ex) {
        }

    }

    
}
