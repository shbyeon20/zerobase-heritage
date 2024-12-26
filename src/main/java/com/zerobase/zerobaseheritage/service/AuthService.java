package com.zerobase.zerobaseheritage.service;

import com.zerobase.zerobaseheritage.model.dto.SignInRequest;
import com.zerobase.zerobaseheritage.model.dto.SignUpRequest;
import com.zerobase.zerobaseheritage.model.entity.MemberEntity;
import com.zerobase.zerobaseheritage.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void signUp(SignUpRequest request) {
        if (memberRepository.findByMemberId(request.getMemberId()).isPresent()) {
            throw new RuntimeException("Member ID already exists");
        }

        MemberEntity member = new MemberEntity();
        member.setMemberId(request.getMemberId());
        member.setPassword(passwordEncoder.encode(request.getPassword()));

        memberRepository.save(member);
    }

    public String signIn(SignInRequest request) {
        MemberEntity member = memberRepository.findByMemberId(request.getMemberId())
            .orElseThrow(() -> new RuntimeException("Member not found"));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // Here you would generate and return a JWT token
        return "JWT_TOKEN";
    }
}
