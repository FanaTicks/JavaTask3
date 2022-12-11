package org.example.task1;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.MalformedJsonException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.io.File;

public class InputOutputFile {
    private static final String ITEMS_NAME = "repots_details";
    private static final String TYPE_PROPERTY = "type";
    private static final String FINE_AMOUNT__PROPERTY = "fine_amount";

    public static void readAndParse() throws FileNotFoundException {
        ExecutorService executor = Executors.newFixedThreadPool(8);
        List<CompletableFuture<Map<String, Double>>> list = new ArrayList<CompletableFuture<Map<String, Double>>>();
        Map<String, Double> map = new ConcurrentHashMap<>();

        for (int i = 1; i < 11; i++) {
            int finalI = i;
            CompletableFuture<Map<String, Double>> future =  CompletableFuture.supplyAsync(() -> {
                try (final JsonReader jsonReader = new JsonReader(new BufferedReader(new InputStreamReader(new FileInputStream(
                        "src\\main\\resources\\report1." + finalI + ".json"))))) {
                    return parseReports(jsonReader);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, executor);
            list.add(future);
        }
        for ( CompletableFuture<Map<String, Double>> fut : list) {
            try {
                map.putAll(fut.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();
        writeFile(map.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldVal, newVal) -> oldVal, LinkedHashMap::new)));//
    }

    public static Map<String, Double> parseReports(final JsonReader jsonReader) throws IOException {
        Map<String, Double> map = new HashMap<>();
        jsonReader.beginObject();//начало обьэкта {
        final String itemsName = jsonReader.nextName();
        if ( !itemsName.equals(ITEMS_NAME) ) {
            throw new MalformedJsonException(ITEMS_NAME + " expected but was " + itemsName);//вывод ошибки
        }
        jsonReader.beginArray();//начала масива
        while ( jsonReader.hasNext() ) {
            jsonReader.beginObject();//начало обьэкта
            String type = "";
            double fineAmount = 0;
            while ( jsonReader.hasNext() ) {
                final String property = jsonReader.nextName();
                switch ( property ) {
                    case TYPE_PROPERTY:
                        type = jsonReader.nextString();//нашли тип
                        break;
                    case FINE_AMOUNT__PROPERTY:
                        fineAmount = jsonReader.nextDouble();//нашли цену
                        break;
                    default:
                        jsonReader.skipValue();//не подходящий обьэкт
                        break;
                }
            }

            if (map.containsKey(type)) {
                Double get = map.get(type);//импорт значения ключа
                map.put(type, get+fineAmount);//повышение значения ключа
            } else {
                map.put(type, fineAmount);//добавление ключа
            }

            jsonReader.endObject();//конец обьэкта
        }
        jsonReader.endArray();//конец масива
        jsonReader.endObject();//конец обьэкта  }
        return map;

    }
    public static void writeFile(Map<String, Double> map) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element rootElement = doc.createElementNS("https://polis.ua/shtraf", "Reports");
            doc.appendChild(rootElement);

            for(Map.Entry<String, Double> item : map.entrySet()) {
                rootElement.appendChild(getReport(doc, item.getKey(), item.getValue()));//создание елемента для каждого значения мапы
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);

            StreamResult file = new StreamResult(new File("src/main/resources/ReportsTop.xml"));
            //запись в файл
            transformer.transform(source, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Node getReport(Document doc, String type, Double fine_amount) {
        Element language = doc.createElement("Report");

        language.appendChild(gerReportElement(doc, language, "type", type));
        language.appendChild(gerReportElement(doc, language, "fine_amount", String.valueOf(fine_amount)));

        return language;
    }

    private static Node gerReportElement(Document doc, Element element, String name, String value) {
        Element node = doc.createElement(name);
        node.appendChild(doc.createTextNode(value));
        return node;
    }

}

