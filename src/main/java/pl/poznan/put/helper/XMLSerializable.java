package pl.poznan.put.helper;

import java.io.Serializable;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMResult;

import org.w3c.dom.Document;

public abstract class XMLSerializable implements Serializable {
    private static final long serialVersionUID = 1L;

    public Document toXML() throws JAXBException, ParserConfigurationException {
        Document document =
                DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        JAXBContext context = JAXBContext.newInstance(getClass());
        Marshaller marshaller = context.createMarshaller();
        marshaller.marshal(this, new DOMResult(document));
        return document;
    }
}
