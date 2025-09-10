package net.softhem.pos.controller;

import net.softhem.pos.dto.OrderDTO;
import net.softhem.pos.model.Helpers;
import net.softhem.pos.model.ReceiptFormat;
import net.softhem.pos.service.OrderService;
import net.softhem.pos.service.PdfService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/pdf")
public class PdfController {
    private final PdfService pdfService;
    private final OrderService orderService;

    public PdfController(PdfService pdfService, OrderService orderService) {
        this.pdfService = pdfService;
        this.orderService = orderService;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> checkPdfExists(@PathVariable Long id) throws Exception {
        String filePath = String.format("pdf/%s/%s.pdf", id, id);
        if (Helpers.fileExists(filePath)) {
            return ResponseEntity.ok().build(); // 200 OK
        } else {
            // Create one
            OrderDTO orderDto = orderService.getOrderById(id);
            pdfService.generatePdfReceipt(orderDto);
            // return ResponseEntity.notFound().build(); // 404 Not Found
            return ResponseEntity.ok().build(); // 200 OK
        }
    }


    @GetMapping("/{filename:.+}")
    public ResponseEntity<?> getPdf(@PathVariable Long filename) {
        try {
            Path file = Paths.get(Helpers.getDirectoryPath("pdf" + "/" + filename)).resolve(filename + ".pdf").normalize();
            String filePath = String.format("%s/%s/%s.pdf", Helpers.getDirectoryPath("pdf"), filename, filename);
            System.out.println("Checking path :: " + filePath);

            if (! Files.exists(Path.of(filePath))) {
                // Create one
                OrderDTO orderDto = orderService.getOrderById(filename);
                pdfService.generatePdfReceipt(orderDto);
                Thread.sleep(2000);
                Resource resource = new UrlResource(Path.of(filePath).toUri());
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + ".pdf\"")
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(resource);
            }
        } catch (Exception e) {
            System.out.println("Error in returning pdf file");
            System.out.println(e);
        }
        return ResponseEntity.notFound().build();
    }
}
