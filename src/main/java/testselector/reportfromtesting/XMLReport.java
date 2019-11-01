package testselector.reportfromtesting;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import testselector.main.Main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class XMLReport {
    private int id;
    private ArrayList<String> test;
    private long timeLaps;
    private Document rta;
    private Element elementInToAdd;
    private String fileNameToWrite;
    private Logger LOGGER = Logger.getLogger(Main.class);

    public XMLReport(int id, long timeLaps, ArrayList<String> test, String fileNameInToWrite) {
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
            rta = null;
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
            /*StringBuilder s1 = new StringBuilder(s);
            int index = s1.lastIndexOf(".");
            s1 = s1.replace(index, index, "#");

            selectdTests.setText(s1.toString());
            selectdTests.addContent(selectedTest);
        */
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


