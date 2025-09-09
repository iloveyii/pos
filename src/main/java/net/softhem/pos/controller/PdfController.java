package net.softhem.pos.controller;

import net.softhem.pos.dto.OrderDTO;
import net.softhem.pos.model.ReceiptFormat;
import net.softhem.pos.service.OrderService;
import net.softhem.pos.service.PdfService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
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

    @GetMapping("/{filename:.+}")
    public ResponseEntity<?> getPdf(@PathVariable Long filename) throws Exception {
        Path file = Paths.get("/data/pdf/" + filename).resolve(filename + ".pdf").normalize();
        Resource resource = new UrlResource(file.toUri());
        if (!resource.exists()) {
            // Create one
            OrderDTO orderDto = orderService.getOrderById(filename);
            pdfService.generatePdfReceipt(orderDto);
            // return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}
