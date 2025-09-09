package net.softhem.pos.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.softhem.pos.dto.OrderDTO;
import net.softhem.pos.model.ReceiptFormat;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


@Service
public class PdfService {
    private final FileStorageService fileStorageService;

    public PdfService(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    public String generatePdfReceipt(OrderDTO orderDTO) throws Exception {
        genLatex(orderDTO);
        Thread.sleep(2500);
        String pdfUrl = genPdf(orderDTO.getId());
        Thread.sleep(1000);
        return  pdfUrl;
    }

    private String genPdf(long id) throws Exception {
        String json = String.format("""
        {
          "id": %d,
          "customer": "Walk-in",
          "total": 112.28
        }
        """, id);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://springlatex:3001/generate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Status: " + response.statusCode());
        System.out.println("Response: " + response.body());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonObject = mapper.readTree(response.body());
        if(jsonObject.has("data")) {
            String url = jsonObject.get("data").get("url").asText();
            return url;
        }
        return "";
    }

    // Utility method to test the class
    private void genLatex(OrderDTO orderDTO) {
        String jsonOrder = "{\n" +
                "    \"id\": 10,\n" +
                "    \"orderDate\": \"2025-09-06T19:31:27.491238\",\n" +
                "    \"orderDateString\": \"2025-09-06 19:31\",\n" +
                "    \"status\": \"PENDING\",\n" +
                "    \"type\": \"INVOICE\",\n" +
                "    \"subTotal\": 308.98,\n" +
                "    \"discount\": 0,\n" +
                "    \"totalAmount\": 333.69843,\n" +
                "    \"paymentMethod\": null,\n" +
                "    \"notes\": \"https://k.jojomobil.se/pdf_files/JM004426/JM004426.pdf\",\n" +
                "    \"orderProducts\": [\n" +
                "        {\n" +
                "            \"id\": 22,\n" +
                "            \"orderId\": 10,\n" +
                "            \"productId\": 3,\n" +
                "            \"productName\": \"Bluetooth Speaker\",\n" +
                "            \"quantity\": 1,\n" +
                "            \"priceAtPurchase\": 59.99\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 23,\n" +
                "            \"orderId\": 10,\n" +
                "            \"productId\": 2,\n" +
                "            \"productName\": \"Smart Watch\",\n" +
                "            \"quantity\": 1,\n" +
                "            \"priceAtPurchase\": 149.99\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 24,\n" +
                "            \"orderId\": 10,\n" +
                "            \"productId\": 1,\n" +
                "            \"productName\": \"Wireless Headphones\",\n" +
                "            \"quantity\": 1,\n" +
                "            \"priceAtPurchase\": 99\n" +
                "        }\n" +
                "    ],\n" +
                "    \"command\": null\n" +
                "}";

        try {

            System.out.println("\n=== PAYMENT QR LATEX ===\n");
            String latexPayment = ReceiptFormat.generatePaymentQRLatex(orderDTO);
            System.out.println(latexPayment);
            fileStorageService.writeLatexStringToFile(orderDTO.getId().toString() + ".tex", latexPayment);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
