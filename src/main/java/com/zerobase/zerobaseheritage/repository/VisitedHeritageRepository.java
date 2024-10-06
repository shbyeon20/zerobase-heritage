package com.zerobase.zerobaseheritage.repository;

import com.zerobase.zerobaseheritage.entity.VisitedHeritageEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitedHeritageRepository extends JpaRepository<VisitedHeritageEntity, Long> {


  List<VisitedHeritageEntity> findAllByMemberId(String userId);
}
