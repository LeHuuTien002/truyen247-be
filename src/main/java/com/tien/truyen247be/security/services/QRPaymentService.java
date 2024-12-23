package com.tien.truyen247be.security.services;

import com.tien.truyen247be.models.QRPayment;
import com.tien.truyen247be.payload.request.QRPaymentRequest;
import com.tien.truyen247be.payload.response.ErrorResponse;
import com.tien.truyen247be.payload.response.QRPaymentResponse;
import com.tien.truyen247be.payload.response.SuccessResponse;
import com.tien.truyen247be.repository.QRPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QRPaymentService {
    @Autowired
    private QRPaymentRepository qrPaymentRepository;

    @Autowired
    private S3Service s3Service;

    public ResponseEntity<?> createQRPayment(QRPaymentRequest request, MultipartFile file) throws IOException {
        String qrCode = s3Service.uploadFile(file);

        QRPayment qrPayment = new QRPayment();
        qrPayment.setQRCode(qrCode);
        qrPayment.setAmount(request.getAmount());
        qrPayment.setBankName(request.getBankName());
        qrPayment.setCardName(request.getCardName());
        qrPayment.setCardNumber(request.getCardNumber());
        qrPayment.setPaymentContent(request.getPaymentContent());
        qrPayment.setCreatedAt(LocalDateTime.now());
        qrPayment.setUpdatedAt(LocalDateTime.now());
        qrPaymentRepository.save(qrPayment);

        SuccessResponse response = new SuccessResponse();
        response.setStatus(200);
        response.setMessage("Tạo thanh toán thành công.");
        response.setTimestamp(LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<List<QRPaymentResponse>> getQRPayments() {
        List<QRPayment> qrPayments = qrPaymentRepository.findAll();
        List<QRPaymentResponse> qrPaymentResponses = new ArrayList<>();
        for (QRPayment qrPayment : qrPayments) {
            QRPaymentResponse qrPaymentResponse = new QRPaymentResponse();
            qrPaymentResponse.setId(qrPayment.getId());
            qrPaymentResponse.setQRCode(qrPayment.getQRCode());
            qrPaymentResponse.setAmount(qrPayment.getAmount());
            qrPaymentResponse.setBankName(qrPayment.getBankName());
            qrPaymentResponse.setCardName(qrPayment.getCardName());
            qrPaymentResponse.setCardNumber(qrPayment.getCardNumber());
            qrPaymentResponse.setPaymentContent(qrPayment.getPaymentContent());
            qrPaymentResponse.setCreatedAt(qrPayment.getCreatedAt());
            qrPaymentResponse.setUpdatedAt(qrPayment.getUpdatedAt());
            qrPaymentResponses.add(qrPaymentResponse);
        }
        return ResponseEntity.ok(qrPaymentResponses);
    }

    public ResponseEntity<?> updateQRPayment(Long id, QRPaymentRequest request, MultipartFile file) throws IOException {
        Optional<QRPayment> qrPaymentOptional = qrPaymentRepository.findById(id);
        if (qrPaymentOptional.isPresent()) {
            String qrCode = s3Service.uploadFile(file);
            QRPayment qrPayment = qrPaymentOptional.get();
            qrPayment.setAmount(request.getAmount());
            qrPayment.setBankName(request.getBankName());
            qrPayment.setCardName(request.getCardName());
            qrPayment.setCardNumber(request.getCardNumber());
            qrPayment.setPaymentContent(request.getPaymentContent());
            qrPayment.setQRCode(qrCode);
            qrPayment.setUpdatedAt(LocalDateTime.now());
            qrPaymentRepository.save(qrPayment);

            SuccessResponse response = new SuccessResponse();
            response.setStatus(200);
            response.setMessage("Cập nhật thành công");
            response.setTimestamp(LocalDateTime.now().toString());
            return ResponseEntity.ok(response);
        } else {
            ErrorResponse response = new ErrorResponse();
            response.setStatus(400);
            response.setMessage("Id thanh toán không tồn tại");
            response.setTimestamp(LocalDateTime.now().toString());
            return ResponseEntity.ok(response);
        }
    }

    public ResponseEntity<?> deleteQRPayment(Long id) {
        Optional<QRPayment> qrPaymentOptional = qrPaymentRepository.findById(id);
        if (qrPaymentOptional.isPresent()) {
            qrPaymentRepository.delete(qrPaymentOptional.get());
            SuccessResponse response = new SuccessResponse();
            response.setStatus(200);
            response.setMessage("Xóa thanh toán thành công");
            response.setTimestamp(LocalDateTime.now().toString());
            return ResponseEntity.ok(response);
        } else {
            ErrorResponse response = new ErrorResponse();
            response.setStatus(400);
            response.setMessage("Id thanh toán không tồn tại");
            response.setTimestamp(LocalDateTime.now().toString());
            return ResponseEntity.ok(response);
        }
    }

}
