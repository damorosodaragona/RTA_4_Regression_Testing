package CATTO.reportfromtesting;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XMLReport {
    private int id;
    private ArrayList<String> test;
    private long timeLaps;
    private Document rta;
    private Element elementInToAdd;
    private String fileNameToWrite;
    private static final Logger LOGGER = Logger.getLogger(XMLReport.class);

    public XMLReport(int id, long timeLaps, List<String> test, String fileNameInToWrite) {
        this.id = id;
        this.test = new ArrayList<>(test);
        this.timeLaps = timeLaps;
        this.fileNameToWrite = fileNameInToWrite;
        readXML();
    }

    private void readXML() {
        SAXBuilder builder = new SAXBuilder();
        rta = null;
        try {
            rta = builder.build(new File(fileNameToWrite + ".xml"));
        } catch (JDOMException | IOException e) {
            LOGGER.error(e);
        }
        if (rta != null) {
            elementInToAdd = rta.getRootElement();
        }
    }

    public void writeOut() {

        if (rta == null) {
            elementInToAdd = new Element("reports");
            rta = new Document(elementInToAdd);
        }
        Element report = new Element("report");
        elementInToAdd.addContent(report);

        Element idElement = new Element("id");
        idElement.setText(Integer.toString(id));
        report.addContent(idElement);

        Element time = new Element("selectionTime");
        time.setText(Long.toString(timeLaps));
        report.addContent(time);

        Element selectedTests = new Element("selectedTests");
        report.addContent(selectedTests);

        for (String s : test) {
            Element selectedTest = new Element("selectedTest");
            selectedTest.setText(s.replaceFirst("class ", ""));
            selectedTests.addContent(selectedTest);
        }

        XMLOutputter outputter = new XMLOutputter();
        try {
            outputter.output(rta, new FileOutputStream(fileNameToWrite + ".xml"));

        } catch (IOException e) {
           LOGGER.info(e.toString());
        }
    }
}


