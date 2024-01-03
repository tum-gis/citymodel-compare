package jgraf.app;

import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.*;

public class InputCityGMLUtils {

    public static Map<String, Integer> count(String filePath) throws Exception {
        Map<String, Integer> elementOccurrences = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        File file = new File(filePath);
        InputStream inputStream = new FileInputStream(file);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(inputStream));

        countElementOccurrences(document.getDocumentElement(), elementOccurrences, 0);

        return elementOccurrences;
    }

    private static final String INDICATOR_TEXT = " (text)";
    private static final String INDICATOR_ATTR = "(attr) ";

    private static void countElementOccurrences(Element element, Map<String, Integer> elementOccurrences, int epoch) {
        String key = "(" + String.format("%02d", epoch) + ") " + element.getNamespaceURI() + ":" + element.getNodeName();

        // Append "(attr)" to key if element has attributes
        /*
        if (element.hasAttributes()) {
            key += " (attr)";
        }
        */

        // Append "(text)" to key if element has text content
        if (hasTextOnly(element)) {
            key += INDICATOR_TEXT;
        }
        elementOccurrences.put(key, elementOccurrences.getOrDefault(key, 0) + 1);

        // Prepend "(attr)" to key for each attribute
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attribute = attributes.item(i);
            String attributeKey = INDICATOR_ATTR + attribute.getNamespaceURI() + ":" + attribute.getNodeName();
            elementOccurrences.put(attributeKey, elementOccurrences.getOrDefault(attributeKey, 0) + 1);
        }

        // Recursively count children
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                countElementOccurrences((Element) child, elementOccurrences, epoch + 1);
            }
        }
    }

    private static boolean hasTextOnly(Element element) {
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.TEXT_NODE) {
                // Check if the text node contains non-whitespace characters
                if (!child.getTextContent().trim().isEmpty()) {
                    return true;
                }
            } else if (child.getNodeType() == Node.ELEMENT_NODE) {
                // If the element has other children, return false
                return false;
            }
        }
        return true; // No other element children found, and text node contains non-whitespace characters
    }

    @SafeVarargs
    public static Map<String, Integer[]> mergeMaps(Map<String, Integer>... maps) {
        Map<String, Integer[]> mergedMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        for (int i = 0; i < maps.length; i++) {
            Map<String, Integer> map = maps[i];
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                String key = entry.getKey();
                Integer value = entry.getValue();

                if (mergedMap.containsKey(key)) {
                    mergedMap.get(key)[i] = value;
                } else {
                    // If key is new, create a new array and add the value to the corresponding index
                    Integer[] newArray = new Integer[maps.length];
                    Arrays.fill(newArray, 0);
                    newArray[i] = value;
                    mergedMap.put(key, newArray);
                }
            }
        }

        return mergedMap;
    }

    private static final String LATEX_COL_SEP = " & ";
    private static final String LATEX_COL_END = " \\\\ ";
    private static final String FORMAT_NUM = "\\num";
    private static final String PREFIX_NUM_ZERO = "\\cz"; // short command for coloring a cell
    private static final String PREFIX_NUM_NONZERO = "\\cc"; // short command for coloring a cell
    private static final String SUPERSCRIPT_TEXT = "\\textsuperscript{1}";

    public static void writeMergedMapLaTeX(Map<String, Integer[]> map, String outputFile) {
        final String[] tmps = {"a", "b", "c", "d", "e", "f"};
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            for (Map.Entry<String, Integer[]> entry : map.entrySet()) {
                String counts = "";
                int tmp = 0;
                for (Integer i : entry.getValue()) {
                    String si = i.toString();
                    if (!FORMAT_NUM.isEmpty()) {
                        si = "$" + FORMAT_NUM + "{" + si + "}" + "$";
                    }
                    if (i == 0) {
                        si = PREFIX_NUM_ZERO + si;
                    } else {
                        si = PREFIX_NUM_NONZERO + tmps[tmp] + si;
                    }
                    counts += LATEX_COL_SEP + String.format("%18s", si);
                    tmp++;
                }
                String keyString = entry.getKey().replace("null:", "");
                String depth = "";
                String name = "";
                if (keyString.contains(INDICATOR_ATTR)) {
                    depth = "";
                    name = keyString.replace(INDICATOR_ATTR, "");
                } else {
                    depth = keyString.substring(1, keyString.indexOf(")"));
                    name = keyString.replace("(" + depth + ") ", "");
                }
                if (name.contains(INDICATOR_TEXT)) {
                    name = name.replace(INDICATOR_TEXT, "");
                    name = "\\textit{" + name + "}";
                    name += SUPERSCRIPT_TEXT;
                } else {
                    name = "\\textit{" + name + "}";
                }
                writer.println(String.format("%7s", depth)  // column for depth
                        + LATEX_COL_SEP + String.format("%-60s", name) // column for XML element/attribute names
                        + LATEX_COL_SEP + String.format("%-30s", "") // column for corresponding node labels / node property names
                        + counts // columns for numbers of occurrences
                        + LATEX_COL_END);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
