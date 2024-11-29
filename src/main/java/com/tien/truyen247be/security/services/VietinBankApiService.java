package com.tien.truyen247be.security.services;

import okhttp3.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class VietinBankApiService {

    @Value("${sepay.api.url}")
    private String apiUrl;

    @Value("${sepay.api.token}")
    private String apiToken;

    @Value("${sepay.account.number}")
    private String accountNumber;

    private final OkHttpClient httpClient = new OkHttpClient();

    public JsonNode getTransactions(String startDate, String endDate, int limit) throws IOException {
        // Tạo URL với các tham số
        String url = apiUrl + "?account_number=" + accountNumber +
                "&transaction_date_min=" + startDate +
                "&transaction_date_max=" + endDate +
                "&limit=" + limit;

        // Tạo request HTTP
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + apiToken)
                .get()
                .build();

        // Gửi request và nhận phản hồi
        Response response = httpClient.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Request thất bại: " + response);
        }

        // Chuyển đổi JSON thành đối tượng để xử lý
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(response.body().string());
    }
}