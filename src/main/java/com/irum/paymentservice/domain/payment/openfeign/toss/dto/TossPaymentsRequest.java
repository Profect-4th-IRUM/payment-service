<<<<<<<< HEAD:src/main/java/com/irum/paymentservice/domain/payment/client/toss/dto/TossPaymentsRequest.java
package com.irum.paymentservice.domain.payment.client.toss.dto;
========
package com.irum.paymentservice.domain.payment.openfeign.toss.dto;
>>>>>>>> develop:src/main/java/com/irum/paymentservice/domain/payment/openfeign/toss/dto/TossPaymentsRequest.java

public record TossPaymentsRequest(String paymentKey, String orderId, int amount) {}
