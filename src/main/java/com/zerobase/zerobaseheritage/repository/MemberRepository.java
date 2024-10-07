package com.zerobase.zerobaseheritage.repository;

import com.zerobase.zerobaseheritage.entity.MemberEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

  Optional<MemberEntity> findByMemberId(String memberId) ;
  }
