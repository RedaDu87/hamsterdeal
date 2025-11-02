package com.example.annonces.config;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.*;

public class SvgCantonsInteractiveSelection {

    private static final Map<String, String> CANTON_NAMES = new LinkedHashMap<>();
    static {
        CANTON_NAMES.put("ZH", "Zürich");
        CANTON_NAMES.put("BE", "Berne");
        CANTON_NAMES.put("LU", "Lucerne");
        CANTON_NAMES.put("UR", "Uri");
        CANTON_NAMES.put("SZ", "Schwyz");
        CANTON_NAMES.put("OW", "Obwald");
        CANTON_NAMES.put("NW", "Nidwald");
        CANTON_NAMES.put("GL", "Glaris");
        CANTON_NAMES.put("ZG", "Zoug");
        CANTON_NAMES.put("FR", "Fribourg");
        CANTON_NAMES.put("SO", "Soleure");
        CANTON_NAMES.put("BS", "Bâle-Ville");
        CANTON_NAMES.put("BL", "Bâle-Campagne");
        CANTON_NAMES.put("SH", "Schaffhouse");
        CANTON_NAMES.put("AR", "Appenzell Rh.-Ext.");
        CANTON_NAMES.put("AI", "Appenzell Rh.-Int.");
        CANTON_NAMES.put("SG", "Saint-Gall");
        CANTON_NAMES.put("GR", "Grisons");
        CANTON_NAMES.put("AG", "Argovie");
        CANTON_NAMES.put("TG", "Thurgovie");
        CANTON_NAMES.put("TI", "Tessin");
        CANTON_NAMES.put("VD", "Vaud");
        CANTON_NAMES.put("VS", "Valais");
        CANTON_NAMES.put("NE", "Neuchâtel");
        CANTON_NAMES.put("GE", "Genève");
        CANTON_NAMES.put("JU", "Jura");
    }

    public static void main(String[] args) {
        try {
            File inputFile = new File("Suisse_cantons.svg");
            if (!inputFile.exists()) {
                System.err.println("❌ Le fichier Suisse_cantons.svg est introuvable !");
                return;
            }

            String parser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
            Document doc = factory.createDocument(inputFile.toURI().toString());

            UserAgentAdapter ua = new UserAgentAdapter();
            BridgeContext ctx = new BridgeContext(ua);
            ctx.setDynamicState(BridgeContext.DYNAMIC);
            GVTBuilder builder = new GVTBuilder();
            builder.build(ctx, doc);

            Random rnd = new Random();
            Set<String> usedColors = new HashSet<>();

            Element labelsGroup = doc.createElementNS(doc.getDocumentElement().getNamespaceURI(), "g");
            labelsGroup.setAttribute("id", "labels");

            for (String cantonId : CANTON_NAMES.keySet()) {
                Element path = doc.getElementById(cantonId);
                if (path == null) continue;

                // 🎨 Couleur unique
                String color;
                do {
                    color = String.format("#%06X", rnd.nextInt(0xFFFFFF + 1));
                } while (usedColors.contains(color));
                usedColors.add(color);

                path.setAttribute("fill", color);
                path.setAttribute("class", "canton");
                path.setAttribute("onclick", "selectCanton('" + cantonId + "')");

                // 🔍 BBox pour placer le texte
                GraphicsNode node = ctx.getGraphicsNode(path);
                if (node == null) continue;
                Rectangle2D bbox = node.getPrimitiveBounds();
                if (bbox == null) continue;

                double cx = bbox.getCenterX();
                double cy = bbox.getCenterY();

                Element text = doc.createElementNS(doc.getDocumentElement().getNamespaceURI(), "text");
                text.setAttribute("x", String.valueOf(cx));
                text.setAttribute("y", String.valueOf(cy));
                text.setAttribute("font-size", "16");
                text.setAttribute("font-weight", "bold");
                text.setAttribute("text-anchor", "middle");
                text.setAttribute("fill", "black");
                text.setTextContent(CANTON_NAMES.get(cantonId));

                labelsGroup.appendChild(text);
            }

            // ➕ CSS pour hover + sélection
            Element style = doc.createElementNS("http://www.w3.org/2000/svg", "style");
            style.setTextContent(
                    ".canton { cursor: pointer; transition: 0.2s; } " +
                            ".canton:hover { opacity: 0.8; stroke: #000; stroke-width: 1.5px; } " +
                            ".selected { stroke: red !important; stroke-width: 2.5px !important; }"
            );
            doc.getDocumentElement().appendChild(style);

            // ➕ JS pour gérer la sélection
            Element script = doc.createElementNS("http://www.w3.org/2000/svg", "script");
            script.setAttribute("type", "text/ecmascript");
            script.setTextContent(
                    "function selectCanton(id) {" +
                            "  var all = document.getElementsByClassName('canton');" +
                            "  for (var i = 0; i < all.length; i++) { all[i].classList.remove('selected'); }" +
                            "  var c = document.getElementById(id);" +
                            "  if (c) { c.classList.add('selected'); }" +
                            "  console.log('✔ Région sélectionnée: ' + id);" +
                            "  // TODO: ici tu peux appeler ton backend ou Angular pour filtrer les annonces" +
                            "}"
            );
            doc.getDocumentElement().appendChild(script);

            doc.getDocumentElement().appendChild(labelsGroup);

            try (OutputStreamWriter writer = new OutputStreamWriter(
                    new FileOutputStream("Suisse_cantons_selectable.svg"), "UTF-8")) {
                org.apache.batik.dom.util.DOMUtilities.writeDocument(doc, writer);
            }

            System.out.println("🎉 Fichier généré : Suisse_cantons_selectable.svg");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
