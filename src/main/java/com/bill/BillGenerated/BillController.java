package com.bill.BillGenerated;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.bill.BillGenerated.Service.BillService;

@Controller
public class BillController {
	
	@Autowired
	private BillService billService;

    @GetMapping("/BillGenerated")
    public String showBillForm(Model model) {
        Bill bill = billService.showBillFormService();
        model.addAttribute("bill", bill);
        return "bill_form";
    }

    @PostMapping("/BillGenerated")
    public String processBill(@ModelAttribute Bill bill, Model model) throws Exception {
        bill.setDateTime(LocalDateTime.now());

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

        ClassPathResource logoResource = new ClassPathResource("static/inline.png");
        byte[] logoBytes;
        try (InputStream is = logoResource.getInputStream()) {
            logoBytes = is.readAllBytes();
        }
        String base64 = Base64.getEncoder().encodeToString(logoBytes);
        model.addAttribute("logoBase64", base64);

        ClassPathResource qrResource = new ClassPathResource("static/bakebliss_with_isha_qr.png");
        byte[] qrBytes;
        try (InputStream is2 = qrResource.getInputStream()) {
            qrBytes = is2.readAllBytes();
        }
        String base642 = Base64.getEncoder().encodeToString(qrBytes);
        model.addAttribute("instagramQrBase64", base642);

        bill.setTotalItems(totalItems);
        bill.setGrandTotal(grandTotal);
        model.addAttribute("bill", bill);
        return "bill_result";
    }

    @PostMapping("/BillGenerated/download")
    public ResponseEntity<ByteArrayResource> generateBillPdf(@ModelAttribute Bill bill) {
        // Calculate totals and set date if missing
        if (bill.getDateTime() == null) {
            bill.setDateTime(LocalDateTime.now());
        }
        int totalItems = 0;
        double grandTotal = 0;
        if (bill.getItems() != null) {
            for (Item item : bill.getItems()) {
                if (item.getQuantity() != null && item.getRate() != null) {
                    totalItems += item.getQuantity();
                    grandTotal += item.getQuantity() * item.getRate();
                }
            }
        }
        bill.setTotalItems(totalItems);
        bill.setGrandTotal((int)grandTotal);

        return billService.generateBillPdf(bill);
    }


    @PostMapping("/BillGenerated/downloadImage")
    public ResponseEntity<ByteArrayResource> downloadBillImageDirect(@ModelAttribute Bill bill) {
        // Ensure totals and date are set before image generation
        bill = billService.processBillService(bill);
        return billService.downloadBillImage(bill);
    }
}
