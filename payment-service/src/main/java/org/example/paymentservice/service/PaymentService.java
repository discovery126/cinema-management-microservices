package org.example.paymentservice.service;

import org.example.paymentservice.dto.PaymentDto;

public interface PaymentService {
    void createPayment(PaymentDto paymentDto);
}
