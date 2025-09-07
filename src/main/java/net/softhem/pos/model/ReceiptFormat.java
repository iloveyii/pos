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

        /**
         \documentclass{article}
         \usepackage[
         paperwidth=80mm,
         paperheight=200mm,
         margin=5mm
         ]{geometry}
         * */
        // LaTeX document preamble
        latex.append("\\documentclass{article} \n");
        latex.append("\\usepackage[ \n");
        latex.append("\\paperwidth=80mm, \n");
        latex.append("\\usepackage{dashrule} \n");
        latex.append("\\paperheight=200mm, \n");
        latex.append("\\margin=5mm \n");
        latex.append("\\]{geometry} \n");

        /**
         \usepackage{fontspec}
         \usepackage{array}
         \usepackage{dashrule}
         \usepackage{arydshln}
         \usepackage{makecell}
         */
        latex.append("\\usepackage{fontspec} \n");
        latex.append("\\usepackage{array} \n");
        latex.append("\\usepackage{dashrule} \n");
        latex.append("\\usepackage{makecell} \n");
        /**
         \setmainfont[
         Path = ./fonts/,
         Extension = .ttf,
         ]{"CourierPrime-Regular"}

         \newfontfamily\barcodefont[
         Path = ./fonts/,
         Extension = .ttf,
         ]{LibreBarcode39-Regular}
         */
        latex.append("\\setmainfont[ \n");
        latex.append("Path = ./fonts/, \n");
        latex.append("Extension = .ttf, \n");
        latex.append("]{\"CourierPrime-Regular\"} \n");
        latex.append("\\newfontfamily\\barcodefont[ \n");
        latex.append("Path = ./fonts/,\n");
        latex.append("Extension = .ttf, \n");
        latex.append("]{LibreBarcode39-Regular} \n");

        /**
         \setlength{\parindent}{0pt}
         \pagestyle{empty}
         \begin{document}
         \thispagestyle{empty}
         */
        latex.append("\\setlength{\\parindent}{0pt} \n");
        latex.append("\\pagestyle{empty} \n");
        latex.append("\\begin{document} \n");
        latex.append("\\thispagestyle{empty} \n");
        /**
         \begin{center}
         \textbf{\Large RETAIL PRO}\\
         123 Main Street, Anytown, USA\\
         Tel: (555) 123-4567\\
         receipt.example.com\\
         \end{center}
         */
        latex.append("\\begin{center} \n");
        latex.append("\\textbf{\\Large RETAIL PRO}\\\\ \n");
        latex.append("123 Main Street, Anytown, USA\\\\ \n");
        latex.append("Tel: (555) 123-4567\\\\ \n");
        latex.append("receipt.example.com\\\\ \n");
        latex.append("\\end{center} \n");

        /**
         \begin{tabular}{@{}p{0.3\textwidth}p{0.6\textwidth}@{}}
         Order \#: & 10\\
         Date: & 2025-09-06 19:31\\
         Customer: & Walk-in\\
         Status: & PENDING\\
         \end{tabular}
         */
        latex.append("\\begin{tabular}{@{}p{0.3\\textwidth}p{0.6\\textwidth}@{}} \n");
        latex.append("Order \\#: & 10\\\\ \n");
        latex.append("Date: & 2025-09-06 19:31\\\\ \n");
        latex.append("Customer: & Walk-in\\\\ \n");
        latex.append("Status: & PENDING\\\\ \n");
        latex.append("\\end{tabular} \n");

        /**
         \vspace{0.5cm}
         \hdashrule[0.5ex]{\textwidth}{0.1pt}{1mm}
         */
        latex.append("\\vspace{0.5cm} \n");
        latex.append("\\hdashrule[0.5ex]{\\textwidth}{0.1pt}{1mm} \n");
        /**
         \begin{tabular}{@{}>{\raggedright\arraybackslash}p{0.325\textwidth}p{0.135\textwidth}rr@{}}
         \textbf{Item} & \textbf{Qty} & \textbf{Price} & \textbf{Total}\\
         \end{tabular}
         */
        latex.append("\\begin{tabular}{@{}>{\\raggedright\\arraybackslash}p{0.325\\textwidth}p{0.135\\textwidth}rr@{}} \n");
        latex.append("\\textbf{Item} & \\textbf{Qty} & \\textbf{Price} & \\textbf{Total}\\\\ \n");
        latex.append("\\end{tabular} \n");
        /**
         \hdashrule[0.5ex]{\textwidth}{0.1pt}{1mm}
         */
        latex.append("\\hdashrule[0.5ex]{\\textwidth}{0.1pt}{1mm} \n");
        /**
         \begin{tabular}{@{}>{\raggedright\arraybackslash}p{0.35\textwidth}p{0.07\textwidth}rr@{}}
         Bluetooth Speaker & 1 & 59.99 & 59.99\\
         Smartwatch & 1 & 149.99 & 149.99\\
         Wireless Headphones & 1 & 99.00 & 99.00\\
         \end{tabular}
         */
        latex.append("\\begin{tabular}{@{}>{\\raggedright\\arraybackslash}p{0.35\\textwidth}p{0.07\\textwidth}rr@{}} \n");
        latex.append("Bluetooth Speaker & 1 & 59.99 & 59.99\\\\ \n");
        latex.append("Smartwatch & 1 & 149.99 & 149.99\\\\ \n");
        latex.append("Wireless Headphones & 1 & 99.00 & 99.00\\\\ \n");
        latex.append("\\end{tabular} \n");
        /**
         \hdashrule[0.5ex]{\textwidth}{0.1pt}{1mm}
         \begin{tabular}{@{}p{0.7\textwidth}r@{}}
         Subtotal: & 308.98\\
         Tax (8\%): & 24.72\\
         Discount: & 0.00\\
         \hdashrule[0.5ex]{\textwidth}{0.1pt}{1mm} \\
         */
        latex.append("\\hdashrule[0.5ex]{\\textwidth}{0.1pt}{1mm} \n");
        latex.append("\\begin{tabular}{@{}p{0.7\\textwidth}r@{}} \n");
        latex.append("Subtotal: & 308.98\\\\ \n");
        latex.append("Tax (8\\%): & 24.72\\\\ \n");
        latex.append("Discount: & 0.00\\\\ \n");
        latex.append("\\hdashrule[0.5ex]{\\textwidth}{0.1pt}{1mm} \\\\ \n");
        /**
         \textbf{TOTAL:} & \textbf{\$333.70}\\
         \end{tabular}
         \vspace{0.15cm}
         \hdashrule[0.5ex]{\textwidth}{0.1pt}{1mm} \\
         \begin{tabular}{@{}p{0.5\textwidth}p{0.4\textwidth}@{}}
         Payment Method: & Not Paid\\
         \end{tabular}
         */
        latex.append("\\textbf{TOTAL:} & \\textbf{\\$333.70}\\\\ \n");
        latex.append("\\end{tabular} \n");
        latex.append("\\vspace{0.15cm} \n");
        latex.append("\\hdashrule[0.5ex]{\\textwidth}{0.1pt}{1mm} \\\\ \n");
        latex.append("\\begin{tabular}{@{}p{0.5\\textwidth}p{0.4\\textwidth}@{}} \n");
        latex.append("Payment Method: & Not Paid\\\\ \n");
        latex.append("\\end{tabular} \n");
        /**
         \vspace{0.5cm}
         \begin{center}
         \textbf{THANK YOU FOR YOUR BUSINESS!}
         \end{center}
         \vspace{0.3cm}
         \begin{center}
         {\Huge \barcodefont *13420*}
         \end{center}
         */
        latex.append("\\vspace{0.5cm} \n");
        latex.append("\\begin{center} \n");
        latex.append("\\textbf{THANK YOU FOR YOUR BUSINESS!} \n");
        latex.append("\\end{center} \n");
        latex.append("\\vspace{0.3cm} \n");
        latex.append("\\begin{center} \n");
        latex.append("{\\Huge \\barcodefont *13420*} \n");
        latex.append("\\end{center} \n");

        /**
         \vspace{0.3cm}
         \begin{center}
         \footnotesize
         Items can be exchanged within 14 days with receipt\\
         Original Receipt - Customer Copy
         \end{center}
         \end{document}
         */
        latex.append("\\vspace{0.3cm} \n");
        latex.append("\\begin{center} \n");
        latex.append("\\footnotesize \n");
        latex.append("Items can be exchanged within 14 days with receipt\\\\ \n");
        latex.append("Original Receipt - Customer Copy \n");
        latex.append("\\end{center} \n");
        latex.append("\\end{document} \n");

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
