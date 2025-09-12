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


public class InvoiceFormat {
    private static final DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");
    private static final DecimalFormat taxFormat = new DecimalFormat("0%");

    public static String generateLatexInvoice(OrderDTO orderDTO) throws Exception {
        StringBuilder latex = new StringBuilder();
        // Header
        latex.append("\\documentclass[a4paper,10pt]{article} \n");
        latex.append("\\usepackage[a4paper, top=2.0cm, bottom=1.5cm]{geometry} \n");
        latex.append("\\usepackage{fontspec} \n");
        latex.append("\\setmainfont[ \n");
        latex.append("Path = /data/fonts/, \n");
        latex.append("Extension = .ttf, \n");
        latex.append("]{CourierPrime-Regular} \n");
        latex.append("\\newfontfamily\\barcodefont[ \n");
        latex.append("Path = /data/fonts/,\n");
        latex.append("Extension = .ttf, \n");
        latex.append("]{LibreBarcode39-Regular} \n");
        latex.append(" \n");
        latex.append("\\usepackage{tikz} \n");
        latex.append("\\usepackage{longtable} \n");
        latex.append("\\usepackage{array} \n");

        // Start document
        latex.append("\\graphicspath{{/data/images/}} \n");
        latex.append("\\begin{document} \n");
        // Top of pdf
        latex.append("\\begin{center} \n");
        latex.append("\\begin{tikzpicture} \n");
        latex.append("\\clip[rounded corners=6pt] (0,0) rectangle (3.5,1.185); \n");
        latex.append("\\node[anchor=south west, inner sep=0] at (0,0)  \n");
        latex.append("{\\includegraphics[width=3.5cm]{logo.jpeg}}; \n");
        latex.append("\\end{tikzpicture} \n");
        latex.append("\\hfill \n");
        latex.append("\\begin{minipage}{0.2\\textwidth} \n");
        latex.append("\\begin{flushleft} \n");
        latex.append("\\textbf{Servicenummer:}  \\\\ \n");
        latex.append("\\texttt{").append(String.format("JO%05d", orderDTO.getId())).append("} \\\\ \n");
        latex.append("\\textbf{Datum:} \\\\ \n");
        latex.append("\\texttt{").append(Helpers.formatOrderDate(orderDTO.getOrderDate().toString(), true)).append("} \n");
        latex.append("\\end{flushleft} \n");
        latex.append("\\end{minipage} \n");
        latex.append("\\end{center} \n");
        latex.append(" \n");
        latex.append("\\vspace{1em} \n");
        latex.append(" \n");
        // title
        latex.append("\\noindent\\large\\textbf{Utlåtande / Kostnadsförslag / undersökningsrapport}\n \n");
        latex.append(" \n");
        latex.append("\\vspace{1.3em} \n");
        latex.append(" \n");
        // customer info
        latex.append("\\noindent\\textbf{Kundnamn:} Faez Shihab \\\\[0.35em] \n");
        latex.append("\\textbf{Phone:} +461234567 \\\\[0.35em] \n");
        latex.append("\\textbf{Enhet:} iPhone 13 \\\\[0.35em] \n");
        latex.append("\\textbf{SN / IMEI:} 353535353535353 \\\\ \n");
        latex.append(" \n");
        latex.append("\\vspace{0.5em} \n");
        latex.append(" \n");
        // Error description
        latex.append("\\noindent\\textbf{Felkbeskrivning vid inlämnande:} \\\\\n \n");
        latex.append("\\noindent \n");
        latex.append(Helpers.escapeLatex(orderDTO.getErrorDescription())).append("\n");
        latex.append(" \n");
        latex.append("\\vspace{1em} \n");
        latex.append(" \n");
        // items table
        latex.append("\\renewcommand{\\arraystretch}{1.3} \n \n");
        latex.append("\\begin{longtable}{|>{\\raggedright\\arraybackslash}p{0.63\\textwidth}|>{\\raggedleft\\arraybackslash}p{0.3\\textwidth}|}\n \n");
        latex.append("\\hline \n");
        latex.append("\\textbf{Åtgärd} & \\textbf{Pris} \\\\ \n");
        latex.append("\\hline \n");

        for (OrderProductDTO orderProductDto : orderDTO.getOrderProducts()) {
            String productName = orderProductDto.getProductName();
            int quantity = orderProductDto.getQuantity();
            double price = orderProductDto.getPriceAtPurchase();
            double total = quantity * price;

            latex.append(Helpers.escapeLatex(productName)).append(" x ");
            latex.append(quantity).append(" & ");
            latex.append(currencyFormat.format(total)).append("\\\\ \n");
            latex.append("\\hline \n");
        }

        latex.append("\\end{longtable} \n");
        latex.append("\\vspace{1em} \n");
        // total
        latex.append("\\begin{flushright} \n");
        latex.append("\\textbf{Rabatt:} 398:- \\\\ \n");
        latex.append("\\textbf{Totalt:} 2999:- \n");
        latex.append("\\end{flushright} \n");
        latex.append("\\vspace{2em} \n");
        latex.append(" \n");
        latex.append("\\vfill \n");
        latex.append(" \n");
        latex.append("\\noindent \n");
        latex.append("\\textbf{Undersökningsavgift:} Vid inlämning tas en avgift på 395 kr för undersökning och utlåtande. \\\\\n \n");
        latex.append("Vid eventuell reparation dras avgiften av från reparationskostnaden.\n \n");
        latex.append(" \n");
        latex.append("\\vspace{3em}\n \n");
        // Signature placeholder
        latex.append("\\noindent \n");
        latex.append("\\begin{tikzpicture} \n");
        latex.append("\\draw[dotted] (0,0) -- (6,0); \n");
        latex.append("\\end{tikzpicture} \n");
        latex.append(" \n");
        // Horizontal line (fits text width) with URL at right
        latex.append("\\vspace{0.5em} \n");
        latex.append("\\noindent \n");
        latex.append("\\begin{minipage}{\\textwidth} \n");
        latex.append("\\noindent\\rule{0.75\\textwidth}{0.4pt}\\hfill\\texttt{www.jojomobil.se}\n \n");
        latex.append("\\end{minipage} \n");
        latex.append(" \n");
        latex.append("\\vspace{0.5em} \n");
        latex.append(" \n");
        // Two cols address
        latex.append("\\noindent \n");
        latex.append("\\begin{tabular}{@{}p{0.5\\textwidth}@{\\hspace{3.0cm}}p{0.4\\textwidth}@{}}\n \n");
        latex.append("\\textbf{Adress:} & \\\\ \n");
        latex.append("JOJO Digital Service AB & \\textbf{Tel:} 044-3000880 \\\\ \n");
        latex.append("Kanalgatan 19 & \\texttt{support@jojomobil.se} \\\\ \n");
        latex.append("29131, Kristianstad & \\textbf{Org.} 5593174328 \\\\ \n");
        latex.append("\\end{tabular} \n");
        latex.append(" \n");
        latex.append("\\end{document} \n");

        return latex.toString();
    }

}