package jgraf.app;

import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.*;

public class FZKHausUtils {

    public static Map<String, Integer> count(String filePath) throws Exception {
        Map<String, Integer> elementOccurrences = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        File file = new File(filePath);
        InputStream inputStream = new FileInputStream(file);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(inputStream));

        countElementOccurrences(document.getDocumentElement(), elementOccurrences);

        return elementOccurrences;
    }

    private static void countElementOccurrences(Element element, Map<String, Integer> elementOccurrences) {
        String key = element.getNamespaceURI() + ":" + element.getNodeName();

        // Append "(attr)" to key if element has attributes
        if (element.hasAttributes()) {
            key += " (attr)";
        }

        // Append "(text)" to key if element has text content
        if (hasTextOnly(element)) {
            key += " (text)";
        }
        elementOccurrences.put(key, elementOccurrences.getOrDefault(key, 0) + 1);

        // Prepend "(attr)" to key for each attribute
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attribute = attributes.item(i);
            String attributeKey = "(attr) " + attribute.getNamespaceURI() + ":" + attribute.getNodeName();
            elementOccurrences.put(attributeKey, elementOccurrences.getOrDefault(attributeKey, 0) + 1);
        }

        // Recursively count children
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                countElementOccurrences((Element) child, elementOccurrences);
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

    public static void writeMergedMap(Map<String, Integer[]> map, String outputFile) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            for (Map.Entry<String, Integer[]> entry : map.entrySet()) {
                String s = "";
                for (Integer i : entry.getValue()) {
                    s += String.format("%12s", i);
                }
                writer.println(String.format("%-50s", entry.getKey().replace("null:", "")) + ":" + s);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
