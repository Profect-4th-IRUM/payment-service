package com.irum.paymentservice.domain.payment.openfeign.member;

import jakarta.persistence.Column;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MemberClient {

    public UUID getMemberId(){
        return UUID.randomUUID();
    }


}
