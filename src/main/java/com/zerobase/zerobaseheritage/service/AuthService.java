package com.zerobase.zerobaseheritage.service;

import com.zerobase.zerobaseheritage.model.dto.SignInRequest;
import com.zerobase.zerobaseheritage.model.dto.SignUpRequest;
import com.zerobase.zerobaseheritage.model.entity.MemberEntity;
import com.zerobase.zerobaseheritage.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;


    public void signUp(SignUpRequest request) {
        if (memberRepository.findByMemberId(request.getMemberId()).isPresent()) {
            throw new RuntimeException("Member ID already exists");
        }

        MemberEntity member = new MemberEntity();
        member.setMemberId(request.getMemberId());

        memberRepository.save(member);
    }

    public String signIn(SignInRequest request) {
        MemberEntity member = memberRepository.findByMemberId(request.getMemberId())
            .orElseThrow(() -> new RuntimeException("Member not found"));


        // Here you would generate and return a JWT token
        return "JWT_TOKEN";
    }
}
