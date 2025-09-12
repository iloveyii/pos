package net.softhem.pos.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.softhem.pos.dto.OrderDTO;
import net.softhem.pos.model.Helpers;
import net.softhem.pos.model.InvoiceFormat;
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
        return pdfUrl;
    }

    public String generatePdfInvoice(OrderDTO orderDTO) throws Exception {
        boolean result = generateLatexInvoice(orderDTO);
        return result? "success": "fail";
    }

    private String genPdf(long id) throws Exception {
        // Prepare dir pdf/9
        Helpers.getDirectoryPath(String.format("pdf/%s", id), true);
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
    private boolean genLatex(OrderDTO orderDTO) {
        try {
            System.out.println("\n=== PAYMENT QR LATEX ===\n");
            String latexPayment = ReceiptFormat.generatePaymentQRLatex(orderDTO);
            System.out.println(latexPayment);
            fileStorageService.writeLatexStringToFile(orderDTO.getId().toString() + ".tex", latexPayment);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return  false;
        }
    }

    private boolean generateLatexInvoice(OrderDTO orderDTO) {
        try {
            System.out.println("\n=== INVOICE LATEX ===\n");
            String latexInvoice = InvoiceFormat.generateLatexInvoice(orderDTO);
            System.out.println(latexInvoice);
            fileStorageService.writeLatexStringToFile(orderDTO.getId().toString() + "_invoice.tex", latexInvoice);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return  false;
        }
    }
}
