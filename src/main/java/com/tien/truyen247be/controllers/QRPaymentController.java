package com.tien.truyen247be.controllers;

import com.tien.truyen247be.payload.request.QRPaymentRequest;
import com.tien.truyen247be.security.services.QRPaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/admin/")
public class QRPaymentController {
    @Autowired
    private QRPaymentService qrPaymentService;

    @PostMapping("QRPayment")
    public ResponseEntity<?> createQRPayment(@Valid @RequestPart("request") QRPaymentRequest request, @RequestPart("file") MultipartFile file) throws IOException {
        return qrPaymentService.createQRPayment(request, file);
    }

    @PutMapping("QRPayment/{id}")
    public ResponseEntity<?> updateQRPayment(@Valid @PathVariable Long id, @RequestPart("request") QRPaymentRequest request, @RequestPart("file") MultipartFile file) throws IOException {
        return qrPaymentService.updateQRPayment(id, request, file);
    }

    @DeleteMapping("QRPayment/{id}")
    public ResponseEntity<?> deleteQRPayment(@Valid @PathVariable Long id) {
        return qrPaymentService.deleteQRPayment(id);
    }
}
