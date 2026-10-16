package com.ecommerce.payment.service;

import com.ecommerce.payment.dto.PaymentRequest;
import com.ecommerce.payment.dto.PaymentResponse;
import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.entity.PaymentStatus;
import com.ecommerce.payment.repository.PaymentRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.Set;

@Service
public class PaymentService {

    private static final Set<String> METHODS =
            Set.of("UPI", "CARD", "NETBANKING", "COD");

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {

        String method = request.paymentMethod()
                .trim()
                .toUpperCase(Locale.ROOT);

        if (!METHODS.contains(method)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Unsupported payment method"
            );
        }

        var existing = paymentRepository.findByOrderId(request.orderId());

        if (existing.isPresent()) {
            Payment payment = existing.get();

            if (!payment.getUserId().equals(request.userId())
                    || payment.getAmount().compareTo(request.amount()) != 0
                    || !payment.getPaymentMethod().equals(method)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Order already has a different payment request"
                );
            }

            return PaymentResponse.from(payment);
        }

        Payment payment = new Payment(
                request.orderId(),
                request.userId(),
                request.amount(),
                method
        );

        if ("COD".equals(method)) {
            payment.setStatus(PaymentStatus.COD_PENDING);
        } else if (request.simulateFailure()) {
            payment.setStatus(PaymentStatus.FAILED);
        } else {
            payment.setStatus(PaymentStatus.SUCCESS);
        }

        Payment saved = paymentRepository.saveAndFlush(payment);

        return PaymentResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Payment not found"
                ));

        return PaymentResponse.from(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrder(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Payment not found for order"
                ));

        return PaymentResponse.from(payment);
    }
}