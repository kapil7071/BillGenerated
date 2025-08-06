package com.bill.BillGenerated.Service;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiConsumer;

import javax.imageio.ImageIO;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.bill.BillGenerated.Bill;
import com.bill.BillGenerated.Item;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

@Service
public class BillService {

    public ResponseEntity<ByteArrayResource> generateBillPdf(Bill bill) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            // Ensure non-null items list
            if (bill.getItems() == null) {
                bill.setItems(new ArrayList<>());
            }

            // Ensure totalItems, grandTotal, dateTime are set
            

            // Load logo image from classpath
            ClassPathResource imgFile = new ClassPathResource("static/inline.png");
            byte[] imageBytes;
            try (InputStream is = imgFile.getInputStream()) {
                imageBytes = is.readAllBytes();
            }

            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(30, 30, 30, 30);

            // Logo centered on top
            ImageData logoData = ImageDataFactory.create(imageBytes);
            com.itextpdf.layout.element.Image logo = new com.itextpdf.layout.element.Image(logoData)
                    .scaleToFit(120, 120)
                    .setHorizontalAlignment(HorizontalAlignment.CENTER)
                    .setMarginBottom(10);
            document.add(logo);

            // Header - Cafe Name
            Paragraph header = new Paragraph("BakeBliss with Isha")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setFontSize(20)
                    .setFontColor(new DeviceRgb(0, 51, 102)); // Dark blue
            document.add(header);

            // Underline after header
            document.add(new LineSeparator(new SolidLine())
                    .setMarginBottom(10).setMarginTop(5));

            // Order info table with background color on labels
            Table orderTable = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                    .useAllAvailableWidth()
                    .setMarginTop(15)
                    .setMarginBottom(10);

            orderTable.addCell(new Cell().add(new Paragraph("Order Id:").setBold())
                    .setBackgroundColor(new DeviceRgb(200, 200, 200)));
            orderTable.addCell(new Cell().add(new Paragraph(String.valueOf(bill.getOrderNo() != null ? bill.getOrderNo() : ""))));

            String formattedDate = (bill.getDateTime() != null)
                    ? bill.getDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yy"))
                    : "N/A";
            orderTable.addCell(new Cell().add(new Paragraph("Date:").setBold())
                    .setBackgroundColor(new DeviceRgb(200, 200, 200)));
            orderTable.addCell(new Cell().add(new Paragraph(formattedDate)));

            orderTable.addCell(new Cell().add(new Paragraph("Order Type:").setBold())
                    .setBackgroundColor(new DeviceRgb(200, 200, 200)));
            orderTable.addCell(new Cell().add(new Paragraph(bill.getOrderType() != null ? bill.getOrderType() : "")));

            document.add(orderTable);

            document.add(new LineSeparator(new SolidLine()).setMarginBottom(10));

            // Customer Details heading
            document.add(new Paragraph("Customer Details")
                    .setBold()
                    .setFontSize(14)
                    .setMarginBottom(8)
                    .setTextAlignment(TextAlignment.LEFT));

            // Customer info table with gray label backgrounds
            Table custTable = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                    .useAllAvailableWidth()
                    .setMarginBottom(10);

            custTable.addCell(new Cell().add(new Paragraph("Name:").setBold())
                    .setBackgroundColor(new DeviceRgb(200, 200, 200)));
            custTable.addCell(new Cell().add(new Paragraph(bill.getCustomerName() != null ? bill.getCustomerName() : "")));

            custTable.addCell(new Cell().add(new Paragraph("Mobile:").setBold())
                    .setBackgroundColor(new DeviceRgb(200, 200, 200)));
            custTable.addCell(new Cell().add(new Paragraph(bill.getCustomerMobile() != null ? bill.getCustomerMobile() : "")));

            document.add(custTable);

            document.add(new LineSeparator(new SolidLine()).setMarginBottom(10));

            // Items table headers with blue background and white text
            Table itemsTable = new Table(UnitValue.createPercentArray(new float[]{4, 2, 1, 2, 2}))
                    .useAllAvailableWidth();

            Color headerBg = new DeviceRgb(41, 128, 185); // blue color

            itemsTable.addHeaderCell(new Cell().add(new Paragraph("Item").setBold().setFontColor(ColorConstants.WHITE)).setBackgroundColor(headerBg));
            itemsTable.addHeaderCell(new Cell().add(new Paragraph("Weight").setBold().setFontColor(ColorConstants.WHITE)).setBackgroundColor(headerBg));
            itemsTable.addHeaderCell(new Cell().add(new Paragraph("QTY").setBold().setFontColor(ColorConstants.WHITE)).setBackgroundColor(headerBg).setTextAlignment(TextAlignment.RIGHT));
            itemsTable.addHeaderCell(new Cell().add(new Paragraph("Rate").setBold().setFontColor(ColorConstants.WHITE)).setBackgroundColor(headerBg).setTextAlignment(TextAlignment.RIGHT));
            itemsTable.addHeaderCell(new Cell().add(new Paragraph("Total").setBold().setFontColor(ColorConstants.WHITE)).setBackgroundColor(headerBg).setTextAlignment(TextAlignment.RIGHT));

            // Add items with alternating row background color for readability
            boolean alternate = false;
            for (Item item : bill.getItems()) {
                Color bgColor = alternate ? new DeviceRgb(230, 240, 255) : ColorConstants.WHITE;
                alternate = !alternate;

                itemsTable.addCell(new Cell().add(new Paragraph(item.getName() != null ? item.getName() : ""))
                        .setBackgroundColor(bgColor));
                String weightStr = (item.getWeight() != 0 ? item.getWeight() : 0) + " " + (item.getWeightUnit() != null ? item.getWeightUnit() : "");
                itemsTable.addCell(new Cell().add(new Paragraph(weightStr)).setBackgroundColor(bgColor));
                itemsTable.addCell(new Cell().add(new Paragraph(String.valueOf(item.getQuantity() != null ? item.getQuantity() : 0))).setTextAlignment(TextAlignment.RIGHT).setBackgroundColor(bgColor));
                itemsTable.addCell(new Cell().add(new Paragraph(String.format("₹ %.2f", item.getRate() != null ? item.getRate() : 0))).setTextAlignment(TextAlignment.RIGHT).setBackgroundColor(bgColor));
                double total = (item.getQuantity() != null && item.getRate() != null) ? item.getQuantity() * item.getRate() : 0;
                itemsTable.addCell(new Cell().add(new Paragraph(String.format("₹ %.2f", total))).setTextAlignment(TextAlignment.RIGHT).setBackgroundColor(bgColor));
            }

            document.add(itemsTable);

            document.add(new LineSeparator(new SolidLine()).setMarginTop(10).setMarginBottom(10));

            // Total summary table with light blue background
            Table totalTable = new Table(UnitValue.createPercentArray(new float[]{3, 1}))
                    .useAllAvailableWidth();

            int totalItems = bill.getTotalItems() != 0 ? bill.getTotalItems() : 0;
            double grandTotal = bill.getGrandTotal() != 0 ? bill.getGrandTotal() : 0.0;

            totalTable.addCell(new Cell().add(new Paragraph("Total Items:").setBold())
                    .setBorder(Border.NO_BORDER)
                    .setBackgroundColor(new DeviceRgb(200, 220, 255)));
            totalTable.addCell(new Cell().add(new Paragraph(String.valueOf(totalItems)))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setBorder(Border.NO_BORDER)
                    .setBackgroundColor(new DeviceRgb(200, 220, 255)));

            totalTable.addCell(new Cell().add(new Paragraph("Grand Total:").setBold())
                    .setBorder(Border.NO_BORDER)
                    .setBackgroundColor(new DeviceRgb(200, 220, 255)));
            totalTable.addCell(new Cell().add(new Paragraph(String.format("₹ %.2f", grandTotal)))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setBorder(Border.NO_BORDER)
                    .setBackgroundColor(new DeviceRgb(200, 220, 255)));

            document.add(totalTable);

            // Contact Us Section
            document.add(new Paragraph("Contact Us")
                    .setBold()
                    .setFontSize(14)
                    .setMarginTop(15)
                    .setMarginBottom(8)
                    .setTextAlignment(TextAlignment.LEFT));

            Table contactTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                    .useAllAvailableWidth()
                    .setMarginBottom(15);

            Paragraph contactInfo = new Paragraph()
                    .add("Address:\nH - 179 Satyam Vihar AWAS VIKAS 1 KALYANPUR NEAR Croma Mall\n")
                    .add("Phone: 8546075108")
                    .setFontSize(11)
                    .setFontColor(new DeviceRgb(128, 128, 128))
                    .setTextAlignment(TextAlignment.LEFT);

            contactTable.addCell(new Cell()
                    .add(contactInfo)
                    .setBorder(Border.NO_BORDER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setPaddingLeft(10));

            ClassPathResource qrImageFile = new ClassPathResource("static/bakebliss_with_isha_qr.png");
            byte[] qrImageBytes;
            try (InputStream qrIs = qrImageFile.getInputStream()) {
                qrImageBytes = qrIs.readAllBytes();
            }
            ImageData qrImageData = ImageDataFactory.create(qrImageBytes);
            com.itextpdf.layout.element.Image qrImage = new com.itextpdf.layout.element.Image(qrImageData)
                    .scaleToFit(100, 100)
                    .setHorizontalAlignment(HorizontalAlignment.CENTER);

            Paragraph instaId = new Paragraph("")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(10)
                    .setBold()
                    .setFontColor(new DeviceRgb(120, 120, 120))
                    .setMarginTop(5);

            Cell rightCell = new Cell()
                    .setBorder(Border.NO_BORDER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setPaddingRight(10);

            rightCell.add(qrImage);
            rightCell.add(instaId);

            contactTable.addCell(rightCell);

            document.add(contactTable);

            // Footer: Thank you note
            Paragraph thankYou = new Paragraph("\n★ Thank You for your order! ★")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(14)
                    .setBold()
                    .setMarginTop(10);
            document.add(thankYou);

            // Footer: Powered by text
            Paragraph poweredBy = new Paragraph("Powered by BakeBliss_with_isha")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(8)
                    .setItalic()
                    .setFontColor(new DeviceRgb(128, 128, 128))
                    .setMarginTop(5);
            document.add(poweredBy);

            document.close();

            byte[] pdfBytes = baos.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename("bill-" + (bill.getOrderNo() != null ? bill.getOrderNo() : "unknown") + ".pdf")
                    .build());

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(pdfBytes.length)
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    public ResponseEntity<ByteArrayResource> downloadBillImage(Bill bill) {
        try {
            int width = 595;
            int height = 900; // increased height for footer

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();

            // Enable anti-aliasing
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // White background
            g.setColor(java.awt.Color.WHITE);
            g.fillRect(0, 0, width, height);

            final int[] y = {20};

            // Draw circular clipped logo
            BufferedImage logo = ImageIO.read(new ClassPathResource("static/inline.png").getInputStream());
            int logoDiameter = 120;
            int logoX = (width - logoDiameter) / 2;

            Ellipse2D.Double clip = new Ellipse2D.Double(logoX, y[0], logoDiameter, logoDiameter);
            g.setClip(clip);
            g.drawImage(logo, logoX, y[0], logoDiameter, logoDiameter, null);
            g.setClip(null); // reset clip

            // Border circle around logo
            g.setColor(new java.awt.Color(192, 192, 192)); // light gray border
            g.setStroke(new BasicStroke(3));
            g.drawOval(logoX, y[0], logoDiameter, logoDiameter);

            y[0] += logoDiameter + 10;

            // Heading
            g.setColor(new java.awt.Color(34, 34, 34)); // almost black
            g.setFont(new Font("SansSerif", Font.BOLD, 20));
            String header = "BakeBliss with Isha";
            FontMetrics fm = g.getFontMetrics();
            int headerX = (width - fm.stringWidth(header)) / 2;
            g.drawString(header, headerX, y[0] + fm.getAscent());
            y[0] += fm.getHeight() + 10;

            // Dotted line separator
            float[] dash = {3f, 3f};
            g.setColor(java.awt.Color.GRAY);
            g.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 3f, dash, 0f));
            g.drawLine(40, y[0], width - 40, y[0]);
            y[0] += 15;

            BiConsumer<String, String> drawLabelValue = (label, value) -> {
                int labelX = 50;
                int valueX = width - 100;
                g.setColor(new java.awt.Color(51, 51, 51));
                g.setFont(new Font("SansSerif", Font.BOLD, 13));
                g.drawString(label, labelX, y[0] + g.getFontMetrics().getAscent());
                g.setFont(new Font("SansSerif", Font.PLAIN, 13));
                String valStr = value != null ? value : "";
                int valWidth = g.getFontMetrics().stringWidth(valStr);
                g.drawString(valStr, valueX - valWidth, y[0] + g.getFontMetrics().getAscent());
                y[0] += g.getFontMetrics().getHeight() + 8;
            };

            drawLabelValue.accept("Order Id:", bill.getOrderNo() != null ? String.valueOf(bill.getOrderNo()) : "");
            String dateStr = (bill.getDateTime() != null) ? bill.getDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yy")) : "N/A";
            drawLabelValue.accept("Date:", dateStr);
            drawLabelValue.accept("Order Type:", bill.getOrderType() != null ? bill.getOrderType() : "");

            // Dotted line separator
            y[0] += 5;
            g.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 3f, dash, 0f));
            g.drawLine(40, y[0], width - 40, y[0]);
            y[0] += 15;

            // Customer info
            g.setFont(new Font("SansSerif", Font.BOLD, 15));
            g.setColor(new java.awt.Color(34, 34, 34));
            g.drawString("Name:", 50, y[0] + g.getFontMetrics().getAscent());
            g.setFont(new Font("SansSerif", Font.PLAIN, 14));
            String custName = bill.getCustomerName() != null ? bill.getCustomerName() : "";
            g.drawString(custName, width - 100 - g.getFontMetrics().stringWidth(custName), y[0] + g.getFontMetrics().getAscent());
            y[0] += g.getFontMetrics().getHeight() + 5;

            g.setFont(new Font("SansSerif", Font.BOLD, 15));
            g.drawString("Mobile:", 50, y[0] + g.getFontMetrics().getAscent());
            g.setFont(new Font("SansSerif", Font.PLAIN, 14));
            String custMobile = bill.getCustomerMobile() != null ? bill.getCustomerMobile() : "";
            g.drawString(custMobile, width - 100 - g.getFontMetrics().stringWidth(custMobile), y[0] + g.getFontMetrics().getAscent());
            y[0] += g.getFontMetrics().getHeight() + 10;

            // Dotted line separator
            g.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 3f, dash, 0f));
            g.drawLine(40, y[0], width - 40, y[0]);
            y[0] += 15;

            // Items table header
            int tableX = 40;
            int[] colWidths = {180, 80, 50, 80, 80};
            String[] headers = {"Item", "Weight", "QTY", "Rate", "Total"};
            int tableWidth = Arrays.stream(colWidths).sum();

            g.setColor(new java.awt.Color(41, 128, 185));
            g.fillRect(tableX, y[0], tableWidth, 30);

            g.setFont(new Font("SansSerif", Font.BOLD, 14));
            g.setColor(java.awt.Color.WHITE);
            int xPos = tableX + 5;
            for (String head : headers) {
                g.drawString(head, xPos, y[0] + 22);
                xPos += colWidths[Arrays.asList(headers).indexOf(head)];
            }
            y[0] += 30;

            // Items rows with alternating background
            g.setFont(new Font("SansSerif", Font.PLAIN, 13));
            boolean alternate = false;
            for (Item item : bill.getItems()) {
                java.awt.Color bg = alternate ? new java.awt.Color(230, 240, 255) : java.awt.Color.WHITE;
                g.setColor(bg);
                g.fillRect(tableX, y[0], tableWidth, 25);

                g.setColor(java.awt.Color.BLACK);
                xPos = tableX + 5;

                String weightStr = (item.getWeight() != 0 ? item.getWeight() : 0) + " " + (item.getWeightUnit() != null ? item.getWeightUnit() : "");
                String qtyStr = String.valueOf(item.getQuantity() != null ? item.getQuantity() : 0);
                String rateStr = String.format("₹ %.2f", item.getRate() != null ? item.getRate() : 0);
                double totalVal = (item.getQuantity() != null && item.getRate() != null) ? item.getQuantity() * item.getRate() : 0;
                String totalStr = String.format("₹ %.2f", totalVal);

                String[] rowVals = {
                        item.getName() != null ? item.getName() : "",
                        weightStr,
                        qtyStr,
                        rateStr,
                        totalStr
                };

                for (int i = 0; i < rowVals.length; i++) {
                    String val = rowVals[i];
                    int textY = y[0] + 18;
                    if (i >= 2) { // right align qty, rate, total
                        int strWidth = g.getFontMetrics().stringWidth(val);
                        g.drawString(val, xPos + colWidths[i] - 5 - strWidth, textY);
                    } else {
                        g.drawString(val, xPos, textY);
                    }
                    xPos += colWidths[i];
                }
                y[0] += 25;
                alternate = !alternate;
            }

            // Totals section
            y[0] += 10;

            Stroke originalStroke = g.getStroke();
            g.setStroke(new BasicStroke(2f));
            g.setColor(new java.awt.Color(41, 128, 185));
            g.drawLine(tableX, y[0], tableX + tableWidth, y[0]);
            y[0] += 8;

            g.setFont(new Font("SansSerif", Font.BOLD, 14));
            g.setColor(new java.awt.Color(34, 34, 34));
            g.drawString("Total Items:", tableX, y[0] + 15);

            int totalItems = bill.getTotalItems();
            String totalItemsStr = String.valueOf(totalItems);
            int totalItemsWidth = g.getFontMetrics().stringWidth(totalItemsStr);
            g.drawString(totalItemsStr, tableX + tableWidth - totalItemsWidth, y[0] + 15);
            y[0] += 30;

            g.drawLine(tableX, y[0], tableX + tableWidth, y[0]);
            y[0] += 8;

            g.drawString("Grand Total:", tableX, y[0] + 15);
            int grandTotal = bill.getGrandTotal();
            String grandTotalStr = String.format("₹ %d", grandTotal);
            int grandTotalWidth = g.getFontMetrics().stringWidth(grandTotalStr);
            g.drawString(grandTotalStr, tableX + tableWidth - grandTotalWidth, y[0] + 15);
            y[0] += 30;

            g.setStroke(originalStroke);

            // Contact Us Section
            g.setColor(new java.awt.Color(41, 128, 185));
            g.setFont(new Font("SansSerif", Font.BOLD, 15));
            g.drawString("Contact Us", tableX, y[0] + 15);
            y[0] += 25;

            g.setFont(new Font("SansSerif", Font.PLAIN, 13));
            g.setColor(new java.awt.Color(80, 80, 80));

            String address = "Address: H - 179 Satyam Vihar AWAS VIKAS 1 KALYANPUR NEAR Croma Mall";
            String phone = "Phone: 8546075108";

            g.drawString(address, tableX, y[0] + 15);
            y[0] += 20;
            g.drawString(phone, tableX, y[0] + 15);
            y[0] += 40;

            // Instagram QR code
            BufferedImage qrImage = ImageIO.read(new ClassPathResource("static/bakebliss_with_isha_qr.png").getInputStream());
            int qrWidth = 100;
            int qrHeight = (int) ((double) qrImage.getHeight() / qrImage.getWidth() * qrWidth);
            int qrX = (width - qrWidth) / 2;
            g.drawImage(qrImage, qrX, y[0], qrWidth, qrHeight, null);
            y[0] += qrHeight + 10;

            // Instagram handle centered
            String instaHandle = "@bakebliss_with_isha";
            g.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g.setColor(new java.awt.Color(100, 100, 100));
            int instaX = (width - g.getFontMetrics().stringWidth(instaHandle)) / 2;
            g.drawString(instaHandle, instaX, y[0] + g.getFontMetrics().getAscent());
            y[0] += 30;

            // Thank You note centered
            String thankYou = "★ Thank You for your order! ★";
            g.setFont(new Font("SansSerif", Font.BOLD, 16));
            g.setColor(new java.awt.Color(0, 128, 0)); // green
            int thankYouX = (width - g.getFontMetrics().stringWidth(thankYou)) / 2;
            g.drawString(thankYou, thankYouX, y[0] + g.getFontMetrics().getAscent());
            y[0] += g.getFontMetrics().getHeight() + 10;

            // Powered by footer centered
            String footer = "Powered by BakeBliss_with_isha";
            g.setFont(new Font("SansSerif", Font.ITALIC, 10));
            g.setColor(new java.awt.Color(130, 130, 130));
            int footerX = (width - g.getFontMetrics().stringWidth(footer)) / 2;
            g.drawString(footer, footerX, y[0] + g.getFontMetrics().getAscent());

            g.dispose();

            // Convert image to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();

            ByteArrayResource resource = new ByteArrayResource(imageBytes);

            HttpHeaders header2 = new HttpHeaders();
            header2.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bill_" + (bill.getOrderNo() != null ? bill.getOrderNo() : "unknown") + ".png");
            header2.setContentType(MediaType.IMAGE_PNG);

            return ResponseEntity.ok()
                    .headers(header2)
                    .contentLength(imageBytes.length)
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    public Bill processBillService(Bill bill) {
        


        int totalItems = 0;
        int grandTotal = 0;

        if (bill.getItems() != null) {
            for (Item item : bill.getItems()) {
                if (item.getQuantity() != null && item.getRate() != null) {
                    totalItems += item.getQuantity();
                    grandTotal += item.getQuantity() * item.getRate();
                }
            }
        } else {
            bill.setItems(new ArrayList<>());
        }

        bill.setTotalItems(totalItems);
        bill.setGrandTotal(grandTotal);

        return bill;
    }

    public Bill showBillFormService() {
        Bill bill = new Bill();
        bill.setDateTime(java.time.LocalDateTime.now());

        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item());
        bill.setItems(items);
        return bill;
    }
}
