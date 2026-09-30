package org.example.paymentservice.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.paymentservice.dto.PaymentDto;
import org.example.paymentservice.model.Payment;
import org.example.paymentservice.model.PaymentStatus;
import org.example.paymentservice.repository.PaymentRepository;
import org.example.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, UUID> kafkaTemplate;

    @Value("${payment.max-amount}")
    private BigDecimal maxAmount;

    @Override
    @KafkaListener(topics = "booking-created",groupId = "payment-service-booking-service")
    @Transactional
    public void createPayment(PaymentDto paymentDto) {
        log.info("Processing payment: bookingId={}, amount={}",
                paymentDto.bookingId(), paymentDto.amount());

        PaymentStatus status = paymentDto.amount().compareTo(maxAmount) <= 0
                ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;

        log.debug("Payment status determined: bookingId={}, status={}, maxAmount={}",
                paymentDto.bookingId(), status, maxAmount);

        Payment payment = Payment.builder()
                .bookingId(paymentDto.bookingId())
                .amount(paymentDto.amount())
                .status(status)
                .processedAt(Instant.now())
                .build();

        if (status == PaymentStatus.SUCCESS) {
            kafkaTemplate.send("payment-completed", payment.getBookingId());
            log.info("Payment completed: bookingId={}, amount={}",
                    payment.getBookingId(), payment.getAmount());
        } else {
            kafkaTemplate.send("payment-failed", payment.getBookingId());
            log.warn("Payment failed: bookingId={}, amount={}, maxAutoApprove={}",
                    payment.getBookingId(), payment.getAmount(), maxAmount);
        }

        paymentRepository.save(payment);
        log.info("Payment saved: id={}, bookingId={}, status={}",
                payment.getId(), payment.getBookingId(), payment.getStatus());
    }
}