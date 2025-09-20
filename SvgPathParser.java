package com.example.annonces.config;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class SvgPathParser {

    public static void main(String[] args) {
        try {
            // 1. Charger le SVG
            File inputFile = new File("Suisse_cantons.svg");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setNamespaceAware(true);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            // 2. Récupérer tous les <path>
            NodeList pathList = doc.getElementsByTagName("path");
            System.out.println("Nombre de <path> trouvés: " + pathList.getLength());

            for (int i = 0; i < pathList.getLength(); i++) {
                Element path = (Element) pathList.item(i);

                String id = path.getAttribute("id");
                String d = path.getAttribute("d");
                String fill = path.getAttribute("fill");
                String stroke = path.getAttribute("stroke");

                System.out.println("----- Path #" + (i + 1) + " -----");
                System.out.println("ID: " + id);
                System.out.println("d: " + d.substring(0, Math.min(60, d.length())) + "...");
                System.out.println("fill: " + fill);
                System.out.println("stroke: " + stroke);

                // 3. Exemple de transformation : changer la couleur
                if ("#AFDEE9".equalsIgnoreCase(fill)) {
                    path.setAttribute("fill", "#FF0000"); // rouge
                }
            }

            // 4. Sauvegarder le SVG modifié
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("output.svg"));
            transformer.transform(source, result);

            System.out.println("SVG transformé enregistré dans output.svg");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

