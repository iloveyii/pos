package net.softhem.pos.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.softhem.pos.dto.OrderDTO;
import net.softhem.pos.dto.OrderProductDTO;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ReceiptFormat {

    private static final DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");
    private static final DecimalFormat taxFormat = new DecimalFormat("0%");

    public static String generateLatexReceipt(String jsonOrder) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode order = mapper.readTree(jsonOrder);

        StringBuilder latex = new StringBuilder();

        // LaTeX document preamble
        latex.append("\\documentclass[10pt]{article}\n");
        latex.append("\\usepackage[utf8]{inputenc}\n");
        latex.append("\\usepackage{array}\n");
        latex.append("\\usepackage{graphicx}\n");
        latex.append("\\usepackage{dashrule}\n");
        latex.append("\\usepackage{geometry}\n");
        latex.append("\\geometry{a4paper, margin=0.5in}\n");
        latex.append("\\usepackage{lastpage}\n");
        latex.append("\\usepackage{fancyhdr}\n");
        latex.append("\\pagestyle{empty}\n");
        latex.append("\\usepackage{fontspec}\n");
        latex.append("\\newfontfamily\\barcodefont{Libre Barcode 39}\n");
        latex.append("\\begin{document}\n");
        latex.append("\\thispagestyle{empty}\n");
        latex.append("\\noindent\n");

        // Header section
        latex.append("\\begin{center}\n");
        latex.append("\\textbf{\\Large RETAIL PRO}\\\\\n");
        latex.append("123 Main Street, Anytown, USA\\\\\n");
        latex.append("Tel: (555) 123-4567\\\\\n");
        latex.append("receipt.example.com\\\\\n");
        latex.append("\\end{center}\n");

        latex.append("\n\n");
        latex.append("\\hdashrule[0.5ex]{\\textwidth}{0.5pt}{10pt}\\\\\n");
        latex.append("\n\n");

        // Order information
        latex.append("\\vspace{0.2cm}\n");
        latex.append("\\begin{tabular}{@{}p{0.3\\textwidth}p{0.6\\textwidth}@{}}\n");
        latex.append("Order \\#: & ").append(order.get("id").asText()).append("\\\\\n");

        // Format date
        String orderDate = formatOrderDate(order.get("orderDate").asText());
        latex.append("Date: & ").append(orderDate).append("\\\\\n");
        latex.append("Customer: & Walk-in\\\\\n");
        latex.append("Status: & ").append(order.get("status").asText()).append("\\\\\n");
        latex.append("\\end{tabular}\n");

        latex.append("\n\n");
        latex.append("\\hdashrule[0.5ex]{\\textwidth}{0.5pt}{10pt}\\\\\n");
        latex.append("\n\n");
        // Order items
        latex.append("\\vspace{0.2cm}\n");
        latex.append("\\begin{tabular}{@{}p{0.5\\textwidth}rrr@{}}\n");
        latex.append("\\textbf{Item} & \\textbf{Qty} & \\textbf{Price} & \\textbf{Total}\\\\\n");
        latex.append("\\hline\n");

        ArrayNode orderProducts = (ArrayNode) order.get("orderProducts");
        for (JsonNode product : orderProducts) {
            String productName = product.get("productName").asText();
            int quantity = product.get("quantity").asInt();
            double price = product.get("priceAtPurchase").asDouble();
            double total = quantity * price;

            latex.append(escapeLatex(productName)).append(" & ");
            latex.append(quantity).append(" & ");
            latex.append(currencyFormat.format(price)).append(" & ");
            latex.append(currencyFormat.format(total)).append("\\\\\n");
        }
        latex.append("\\end{tabular}\n");

        latex.append("\n\n");
        latex.append("\\hdashrule[0.5ex]{\\textwidth}{0.5pt}{10pt}\\\\\n");
        latex.append("\n\n");

        // Totals section
        latex.append("\\vspace{0.2cm}\n");
        latex.append("\\begin{tabular}{@{}p{0.7\\textwidth}r@{}}\n");

        double subTotal = order.get("subTotal").asDouble();
        double discount = order.get("discount").asDouble();
        double totalAmount = order.get("totalAmount").asDouble();
        double taxAmount = totalAmount - subTotal + discount;
        double taxRate = (subTotal > 0) ? (taxAmount / subTotal) : 0;

        latex.append("Subtotal: & ").append(currencyFormat.format(subTotal)).append("\\\\\n");
        latex.append("Tax (").append(taxFormat.format(taxRate)).append("): & ").append(currencyFormat.format(taxAmount)).append("\\\\\n");
        latex.append("Discount: & ").append(currencyFormat.format(discount)).append("\\\\\n");

        latex.append("\n\n");
        latex.append("\\hdashrule[0.5ex]{0.7\\textwidth}{0.5pt}{10pt}\\\\\n");
        latex.append("\n\n");

        latex.append("\\textbf{TOTAL:} & \\textbf{").append(currencyFormat.format(totalAmount)).append("}\\\\\n");
        latex.append("\\end{tabular}\n");

        latex.append("\n\n");
        latex.append("\\hdashrule[0.5ex]{\\textwidth}{0.5pt}{10pt}\\\\\n");
        latex.append("\n\n");

        // Payment information
        latex.append("\\vspace{0.2cm}\n");
        latex.append("\\begin{tabular}{@{}p{0.5\\textwidth}p{0.4\\textwidth}@{}}\n");
        JsonNode paymentMethod = order.get("paymentMethod");
        String paymentMethodStr = paymentMethod.isNull() ? "Not Paid" : paymentMethod.asText();
        latex.append("Payment Method: & ").append(paymentMethodStr).append("\\\\\n");
        latex.append("\\end{tabular}\n");

        // Thank you message
        latex.append("\\vspace{0.5cm}\n");
        latex.append("\\begin{center}\n");
        latex.append("\\textbf{THANK YOU FOR YOUR BUSINESS!}\n");
        latex.append("\\end{center}\n");

        // Barcode
        latex.append("\\vspace{0.3cm}\n");
        latex.append("\\begin{center}\n");
        latex.append("{\\barcodefont\\large *RETAILPRO-").append(String.format("%04d", order.get("id").asInt())).append("*}\n");
        latex.append("\\end{center}\n");

        // Footer
        latex.append("\\vspace{0.3cm}\n");
        latex.append("\\begin{center}\n");
        latex.append("\\footnotesize\n");
        latex.append("Items can be exchanged within 14 days with receipt\\\\\n");
        latex.append("Original Receipt - Customer Copy\n");
        latex.append("\\end{center}\n");

        latex.append("\\end{document}\n");

        return latex.toString();
    }

    public static String generatePaymentQRLatex(OrderDTO orderDTO) throws Exception {
        StringBuilder latex = new StringBuilder();

        latex.append("\\documentclass{article} \n");
        latex.append("\\usepackage[\n");
        latex.append("paperwidth=80mm,\n");
        latex.append("paperheight=200mm,\n");
        latex.append("top=15mm,left=5mm,right=5mm,bottom=15mm\n"); // margin=5mm
        latex.append("]{geometry}\n");

        latex.append("\\usepackage{fontspec} \n");

        latex.append("\\setmainfont[ \n");
        latex.append("Path = /data/fonts/, \n");
        latex.append("Extension = .ttf, \n");
        latex.append("]{CourierPrime-Regular} \n");

        latex.append("\\newfontfamily\\barcodefont[ \n");
        latex.append("Path = /data/fonts/,\n");
        latex.append("Extension = .ttf, \n");
        latex.append("]{LibreBarcode39-Regular} \n");

        latex.append("\\usepackage{array} \n");
        latex.append("\\usepackage{dashrule} \n");
        latex.append("\\usepackage{arydshln} \n");
        latex.append("\\usepackage{makecell} \n");

        latex.append("\\setlength{\\parindent}{0pt} \n");
        latex.append("\\pagestyle{empty} \n");
        latex.append("\\begin{document} \n");
        latex.append("\\thispagestyle{empty} \n");

        latex.append("\\begin{center} \n");
        latex.append("\\textbf{\\Large RETAIL PRO}\\\\ \n");
        latex.append("123 Main Street, Anytown, USA\\\\ \n");
        latex.append("Tel: (555) 123-4567\\\\ \n");
        latex.append("receipt.example.com\\\\ \n");
        latex.append("\\end{center} \n");

        latex.append("\\begin{tabular}{@{}p{0.3\\textwidth}p{0.6\\textwidth}@{}} \n");
        latex.append("Order \\#: & ").append(orderDTO.getId()).append("\\\\\n");
        String orderDate = formatOrderDate(orderDTO.getOrderDate().toString());
        latex.append("Date: & ").append(orderDate).append("\\\\\n");
        latex.append("Customer: & Walk-in\\\\ \n");
        latex.append("Status: & PENDING\\\\ \n");
        latex.append("\\end{tabular} \n");

        latex.append("\\vspace{0.5cm}\n");

        latex.append("\n\n");
        latex.append("\\hdashrule[0.5ex]{\\textwidth}{0.1pt}{1mm}\n");
        latex.append("\n\n");

        latex.append("\\begin{tabular}{@{}>{\\raggedright\\arraybackslash}p{0.375\\textwidth}p{0.130\\textwidth}rr@{}} \n");
        latex.append("\\textbf{Item} & \\textbf{Qty} & \\textbf{Price} & \\textbf{Total}\\\\ \n");
        latex.append("\\end{tabular} \n");

        latex.append("\n\n");
        latex.append("\\hdashrule[0.5ex]{\\textwidth}{0.1pt}{1mm} \n");
        latex.append("\n\n");

        latex.append("\\small");
        latex.append("\\begin{tabular}{@{}>{\\raggedright\\arraybackslash}p{0.45\\textwidth}p{0.07\\textwidth}rr@{}} \n");

        for (OrderProductDTO orderProductDto : orderDTO.getOrderProducts()) {
            String productName = orderProductDto.getProductName();
            int quantity = orderProductDto.getQuantity();
            double price = orderProductDto.getPriceAtPurchase();
            double total = quantity * price;

            latex.append(escapeLatex(productName)).append(" & ");
            latex.append(quantity).append(" & ");
            latex.append(currencyFormat.format(price)).append(" & ");
            latex.append(currencyFormat.format(total)).append("\\\\\n");
        }

        latex.append("\\end{tabular} \n");
        latex.append("\\normalsize");

        latex.append("\n\n");
        latex.append("\\hdashrule[0.5ex]{\\textwidth}{0.1pt}{1mm} \n");
        latex.append("\n\n");

        latex.append("\\begin{tabular}{@{}p{0.7\\textwidth}r@{}} \n");
        latex.append("Subtotal: &").append(orderDTO.getStatus()).append("\\\\ \n");
        latex.append("Tax (8\\%): & ").append(Math.round(orderDTO.getSubTotal() * 0.08)).append("\\\\ \n");
        latex.append("Discount: & 0.00\\\\ \n");

        latex.append("\n\n");
        latex.append("\\hdashrule[0.5ex]{\\textwidth}{0.1pt}{1mm} \\\\ \n");
        latex.append("\n\n");

        double totalAmount = orderDTO.getTotalAmount();
        latex.append("\\textbf{TOTAL:} & \\$").append(currencyFormat.format(totalAmount)).append("\\\\\n");
        latex.append("\\end{tabular} \n");
        latex.append("\\vspace{0.15cm} \n");

        latex.append("\n\n");
        latex.append("\\hdashrule[0.5ex]{\\textwidth}{0.1pt}{1mm} \\\\ \n");
        latex.append("\n\n");

        latex.append("\\begin{tabular}{@{}p{0.5\\textwidth}p{0.4\\textwidth}@{}} \n");
        latex.append("Payment Method: & Not Paid\\\\ \n");
        latex.append("\\end{tabular} \n");

        latex.append("\\vspace{0.5cm} \n");
        latex.append("\\begin{center} \n");
        latex.append("\\textbf{THANK YOU FOR YOUR BUSINESS!} \n");
        latex.append("\\end{center} \n");
        latex.append("\\vspace{0.3cm} \n");
        latex.append("\\begin{center} \n");
        latex.append("{\\Huge \\barcodefont *13420*} \n");
        latex.append("\\end{center} \n");

        latex.append("\\vspace{0.3cm} \n");
        latex.append("\\begin{center} \n");
        latex.append("\\footnotesize \n");
        latex.append("Items can be exchanged within 14 days with receipt\\\\ \n");
        latex.append("Original Receipt - Customer Copy \n");
        latex.append("\\end{center} \n");
        latex.append("\\end{document} \n");
        return latex.toString();
    }

    private static String formatOrderDate(String dateString) {
        try {
            // Parse ISO date format
            LocalDateTime dateTime = LocalDateTime.parse(dateString.substring(0, 19));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            return dateTime.format(formatter);
        } catch (Exception e) {
            return dateString; // Return original if parsing fails
        }
    }

    private static String escapeLatex(String text) {
        return text.replace("&", "\\&")
                .replace("%", "\\%")
                .replace("$", "\\$")
                .replace("#", "\\#")
                .replace("_", "\\_")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace("~", "\\textasciitilde")
                .replace("^", "\\textasciicircum")
                .replace("\\", "\\textbackslash");
    }


}
