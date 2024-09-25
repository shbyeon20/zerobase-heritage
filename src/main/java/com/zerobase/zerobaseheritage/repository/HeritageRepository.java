package com.zerobase.zerobaseheritage.repository;

import com.zerobase.zerobaseheritage.entity.HeritageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HeritageRepository extends JpaRepository<HeritageEntity, Long> {

}
