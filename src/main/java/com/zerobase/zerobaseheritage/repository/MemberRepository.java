package com.zerobase.zerobaseheritage.repository;

import com.zerobase.zerobaseheritage.model.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
  Optional<MemberEntity> findByMemberId(String memberId);
}
