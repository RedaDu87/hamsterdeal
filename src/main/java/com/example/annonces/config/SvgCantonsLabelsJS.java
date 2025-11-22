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
import java.util.LinkedHashMap;
import java.util.Map;

public class SvgCantonsLabelsJS {

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

            // CSS global
            Element style = doc.createElementNS("http://www.w3.org/2000/svg", "style");
            style.setTextContent(
                    ".canton { " +
                            "    fill:white; stroke:green; stroke-width:1.5; cursor:pointer; transition:all 0.2s;" +
                            "}" +
                            ".canton:hover, .canton.hovered { " +
                            "    fill:#90EE90 !important; stroke:darkgreen !important; stroke-width:3px !important;" +
                            "}" +
                            ".label-box { " +
                            "    fill:white; stroke:green; stroke-width:1; rx:4; ry:4; opacity:0; transition:opacity 0.2s; cursor:pointer;" +
                            "}" +
                            ".label-text { " +
                            "    font-weight:bold; font-size:14px; text-anchor:middle; fill:green; opacity:0; transition:opacity 0.2s; cursor:pointer;" +
                            "}" +
                            ".visible { opacity:1 !important; }"
            );


            doc.getDocumentElement().appendChild(style);

            // Groupe global cantons
            Element mapGroup = doc.createElementNS(doc.getDocumentElement().getNamespaceURI(), "g");
            mapGroup.setAttribute("id", "map");
            doc.getDocumentElement().appendChild(mapGroup);

            // Groupe global labels (toujours au-dessus)
            Element labelsGroup = doc.createElementNS(doc.getDocumentElement().getNamespaceURI(), "g");
            labelsGroup.setAttribute("id", "labels");
            doc.getDocumentElement().appendChild(labelsGroup);

            for (String cantonId : CANTON_NAMES.keySet()) {
                Element path = doc.getElementById(cantonId);
                if (path == null) {
                    System.out.println("⚠ Canton " + cantonId + " introuvable");
                    continue;
                }

                // Calcul du centre
                GraphicsNode node = ctx.getGraphicsNode(path);
                if (node == null) continue;
                Rectangle2D bbox = node.getPrimitiveBounds();
                if (bbox == null) continue;

                double cx = bbox.getCenterX();
                double cy = bbox.getCenterY() - 15;

                // Canton
                Element newPath = (Element) path.cloneNode(true);
                newPath.setAttribute("class", "canton");
                newPath.setAttribute("id", "canton-" + cantonId);

                mapGroup.appendChild(newPath);

                // Label (rect + texte) dans le groupe labels
// ---------------------------------------------
// 1) Créer le texte AVANT pour mesurer sa taille
// ---------------------------------------------
                String label = CANTON_NAMES.get(cantonId);

                Element text = doc.createElementNS(doc.getDocumentElement().getNamespaceURI(), "text");
                text.setAttribute("x", "0"); // position temporaire
                text.setAttribute("y", "0");
                text.setAttribute("class", "label-text");
                text.setAttribute("id", "label-text-" + cantonId);
                text.setTextContent(label);

// Ajouter temporairement
                labelsGroup.appendChild(text);

// Construire son node GVT pour avoir la vraie bbox
                ctx.getGraphicsNode(text);
                GraphicsNode textNode = ctx.getGraphicsNode(text);
                Rectangle2D textBBox = textNode.getPrimitiveBounds();

// Taille réelle du texte
                double textWidth = textBBox.getWidth();
                double textHeight = textBBox.getHeight();

// Padding interne
                double padX = 8;
                double padY = 4;

// ---------------------------------------------
// 2) Création dynamique du rectangle
// ---------------------------------------------
                Element rect = doc.createElementNS(doc.getDocumentElement().getNamespaceURI(), "rect");
                rect.setAttribute("width", String.valueOf(textWidth + padX * 2));
                rect.setAttribute("height", String.valueOf(textHeight + padY * 2));
                rect.setAttribute("class", "label-box");
                rect.setAttribute("id", "label-box-" + cantonId);

// ---------------------------------------------
// 3) Repositionnement CENTRÉ
// ---------------------------------------------
                rect.setAttribute("x", String.valueOf(cx - (textWidth / 2) - padX));
                rect.setAttribute("y", String.valueOf(cy - textHeight));

// Centrage du texte
                text.setAttribute("x", String.valueOf(cx));
                text.setAttribute("y", String.valueOf(cy));

// ---------------------------------------------
// 4) Insérer rect AVANT texte
// ---------------------------------------------
                labelsGroup.insertBefore(rect, text);

            }

            // Script JS inline pour gérer hover → affiche le bon label
            Element script = doc.createElementNS("http://www.w3.org/2000/svg", "script");
            script.setAttribute("type", "text/ecmascript");
            script.setTextContent(
                    "const cantons = document.querySelectorAll('.canton');\n" +
                            "\n" +
                            "cantons.forEach(c => {\n" +
                            "    const id = c.id.replace('canton-', '');\n" +
                            "    const labelBox = document.getElementById('label-box-' + id);\n" +
                            "    const labelText = document.getElementById('label-text-' + id);\n" +
                            "\n" +
                            "    // --- HOVER ÉVÉNEMENTS ---\n" +
                            "\n" +
                            "    function hoverOn() {\n" +
                            "        c.classList.add('hovered');\n" +
                            "        labelBox.classList.add('visible');\n" +
                            "        labelText.classList.add('visible');\n" +
                            "    }\n" +
                            "\n" +
                            "    function hoverOff() {\n" +
                            "        c.classList.remove('hovered');\n" +
                            "        labelBox.classList.remove('visible');\n" +
                            "        labelText.classList.remove('visible');\n" +
                            "    }\n" +
                            "\n" +
                            "    // Survol canton\n" +
                            "    c.addEventListener('mouseenter', hoverOn);\n" +
                            "    c.addEventListener('mouseleave', hoverOff);\n" +
                            "\n" +
                            "    // Survol du label (rectangle + texte)\n" +
                            "    labelBox.addEventListener('mouseenter', hoverOn);\n" +
                            "    labelText.addEventListener('mouseenter', hoverOn);\n" +
                            "\n" +
                            "    labelBox.addEventListener('mouseleave', hoverOff);\n" +
                            "    labelText.addEventListener('mouseleave', hoverOff);\n" +
                            "\n" +
                            "    // --- CLICK ÉVÉNEMENTS (même action) ---\n" +
                            "    function onClick() {\n" +
                            "        console.log(\"Click canton:\", id);\n" +
                            "        // ICI tu mets ton action :\n" +
                            "        // onCantonClick(id);\n" +
                            "    }\n" +
                            "\n" +
                            "    c.addEventListener('click', onClick);\n" +
                            "    labelBox.addEventListener('click', onClick);\n" +
                            "    labelText.addEventListener('click', onClick);\n" +
                            "});\n"
            );

            doc.getDocumentElement().appendChild(script);

            // Sauvegarde
            try (OutputStreamWriter writer = new OutputStreamWriter(
                    new FileOutputStream("Suisse_cantons_labels_js.svg"), "UTF-8")) {
                org.apache.batik.dom.util.DOMUtilities.writeDocument(doc, writer);
            }

            System.out.println("🎉 Fichier généré : Suisse_cantons_labels_js.svg");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
