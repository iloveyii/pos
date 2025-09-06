package net.softhem.pos.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ReceiptFormat {

    private static final DecimalFormat currencyFormat = new DecimalFormat("$#,##0.00");
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
        latex.append("\\setmainfont{Courier New}\n");
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
        latex.append("\\hdashrule[0.5ex]{\\textwidth}{0.5pt}{10pt}\\\\\n");

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
        latex.append("\\hdashrule[0.5ex]{\\textwidth}{0.5pt}{10pt}\\\\\n");

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
        latex.append("\\hdashrule[0.5ex]{\\textwidth}{0.5pt}{10pt}\\\\\n");

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
        latex.append("\\hdashrule[0.5ex]{0.7\\textwidth}{0.5pt}{10pt}\\\\\n");
        latex.append("\\textbf{TOTAL:} & \\textbf{").append(currencyFormat.format(totalAmount)).append("}\\\\\n");
        latex.append("\\end{tabular}\n");
        latex.append("\\hdashrule[0.5ex]{\\textwidth}{0.5pt}{10pt}\\\\\n");

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

    public static String generatePaymentQRLatex(String jsonOrder) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode order = mapper.readTree(jsonOrder);

        StringBuilder latex = new StringBuilder();

        // LaTeX document preamble
        latex.append("\\documentclass[10pt]{article}\n");
        latex.append("\\usepackage[utf8]{inputenc}\n");
        latex.append("\\usepackage{graphicx}\n");
        latex.append("\\usepackage{dashrule}\n");
        latex.append("\\usepackage{geometry}\n");
        latex.append("\\geometry{a4paper, margin=0.5in}\n");
        latex.append("\\usepackage{fontspec}\n");
        latex.append("\\setmainfont{Courier New}\n");
        latex.append("\\begin{document}\n");
        latex.append("\\thispagestyle{empty}\n");
        latex.append("\\noindent\n");

        // Header section
        latex.append("\\begin{center}\n");
        latex.append("\\textbf{\\Large RETAIL PRO}\\\\\n");
        latex.append("Scan to Pay\\\\\n");
        latex.append("\\end{center}\n");
        latex.append("\\hdashrule[0.5ex]{\\textwidth}{0.5pt}{10pt}\\\\\n");

        // Order information
        latex.append("\\vspace{0.2cm}\n");
        latex.append("\\begin{tabular}{@{}p{0.3\\textwidth}p{0.6\\textwidth}@{}}\n");
        latex.append("Order \\#: & ").append(order.get("id").asText()).append("\\\\\n");

        String orderDate = formatOrderDate(order.get("orderDate").asText());
        latex.append("Date: & ").append(orderDate).append("\\\\\n");

        double totalAmount = order.get("totalAmount").asDouble();
        latex.append("Amount Due: & ").append(currencyFormat.format(totalAmount)).append("\\\\\n");
        latex.append("\\end{tabular}\n");
        latex.append("\\hdashrule[0.5ex]{\\textwidth}{0.5pt}{10pt}\\\\\n");

        // QR Code placeholder
        latex.append("\\vspace{0.5cm}\n");
        latex.append("\\begin{center}\n");
        latex.append("\\fbox{\\parbox{6cm}{\\centering\\vspace{6cm}\\\\[-6cm]\\textbf{QR CODE PLACEHOLDER}}}\\\\\n");
        latex.append("\\vspace{0.3cm}\n");
        latex.append("Scan this QR code with your mobile payment app\\\\\n");
        latex.append("to complete your payment of ").append(currencyFormat.format(totalAmount)).append("\\\\\n");
        latex.append("\\end{center}\n");

        // Payment methods
        latex.append("\\vspace{0.5cm}\n");
        latex.append("\\begin{center}\n");
        latex.append("We accept:\\\\\n");
        latex.append("Apple Pay, Google Pay, PayPal, Venmo\\\\\n");
        latex.append("\\end{center}\n");

        // Thank you message
        latex.append("\\vspace{0.5cm}\n");
        latex.append("\\begin{center}\n");
        latex.append("\\textbf{THANK YOU FOR SHOPPING WITH US!}\n");
        latex.append("\\end{center}\n");

        // Footer
        latex.append("\\vspace{0.3cm}\n");
        latex.append("\\begin{center}\n");
        latex.append("\\footnotesize\n");
        latex.append("Payment must be completed within 15 minutes\\\\\n");
        latex.append("Show confirmation at counter when done\n");
        latex.append("\\end{center}\n");

        latex.append("\\end{document}\n");

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
