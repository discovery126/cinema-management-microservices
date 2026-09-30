package com.github.discovery126.paymentservice.service;

import com.github.discovery126.paymentservice.dto.PaymentDto;

public interface PaymentService {
    void createPayment(PaymentDto paymentDto);
}
