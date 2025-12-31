package com.example.annonces.config;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.bridge.*;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.LinkedHashMap;
import java.util.Map;

public class SvgCantonsTitleLabel {

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

            String parser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
            Document doc = factory.createDocument(inputFile.toURI().toString());

            BridgeContext ctx = new BridgeContext(new UserAgentAdapter());
            ctx.setDynamicState(BridgeContext.DYNAMIC);
            new GVTBuilder().build(ctx, doc);

            String ns = doc.getDocumentElement().getNamespaceURI();

            /* ================= STYLE ================= */
            Element style = doc.createElementNS(ns, "style");
            style.setTextContent("""
                .canton {
                    fill: #ffffff;
                    stroke: #16a34a;
                    stroke-width: 1.5;
                    cursor: pointer;
                    transition: all .2s;
                }
                .canton:hover {
                    fill: #bbf7d0;
                    stroke-width: 3;
                }
                .title-box {
                    fill: #16a34a; /* vert */
                    rx: 18;
                    ry: 18;
                }
                .title-text {
                    fill: #ffffff;
                    font-size: 28px;
                    font-weight: 800;
                    dominant-baseline: middle;
                    text-anchor: start;
                }
            """);
            doc.getDocumentElement().appendChild(style);

            /* ================= TITRE HAUT GAUCHE ================= */
            Element titleGroup = doc.createElementNS(ns, "g");
            titleGroup.setAttribute("id", "title");

            Element titleBox = doc.createElementNS(ns, "rect");
            titleBox.setAttribute("id", "title-box");
            titleBox.setAttribute("class", "title-box");
            titleBox.setAttribute("x", "20");
            titleBox.setAttribute("y", "20");
            titleBox.setAttribute("height", "60");

            Element titleText = doc.createElementNS(ns, "text");
            titleText.setAttribute("id", "title-text");
            titleText.setAttribute("class", "title-text");
            titleText.setAttribute("x", "40");
            titleText.setAttribute("y", "50");
            titleText.setTextContent("Suisse");

            titleGroup.appendChild(titleBox);
            titleGroup.appendChild(titleText);
            doc.getDocumentElement().appendChild(titleGroup);

            /* ================= CANTONS ================= */
            for (Map.Entry<String, String> entry : CANTON_NAMES.entrySet()) {
                Element path = doc.getElementById(entry.getKey());
                if (path == null) continue;

                Element clone = (Element) path.cloneNode(true);
                clone.setAttribute("class", "canton");
                clone.setAttribute("data-name", entry.getValue());
                doc.getDocumentElement().appendChild(clone);
            }

            /* ================= JS ================= */
            Element script = doc.createElementNS(ns, "script");
            script.setAttribute("type", "text/ecmascript");
            script.setTextContent("""
                const text = document.getElementById('title-text');
                const box = document.getElementById('title-box');

                function updateTitle(label) {
                    text.textContent = label;

                    const bbox = text.getBBox();
                    const paddingX = 30;

                    box.setAttribute('width', bbox.width + paddingX * 2);
                }

                updateTitle("Suisse");

                document.querySelectorAll('.canton').forEach(canton => {
                    canton.addEventListener('mouseenter', () => {
                        updateTitle(canton.dataset.name);
                    });
                    canton.addEventListener('mouseleave', () => {
                        updateTitle("Suisse");
                    });
                });
            """);
            doc.getDocumentElement().appendChild(script);

            /* ================= SAVE ================= */
            try (OutputStreamWriter writer =
                         new OutputStreamWriter(new FileOutputStream("Suisse_cantons_title.svg"), "UTF-8")) {
                org.apache.batik.dom.util.DOMUtilities.writeDocument(doc, writer);
            }

            System.out.println("✅ SVG généré : Suisse_cantons_title.svg");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
