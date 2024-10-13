package com.zerobase.zerobaseheritage.repository;

import com.zerobase.zerobaseheritage.entity.HeritageEntity;
import com.zerobase.zerobaseheritage.entity.VisitedHeritageEntity;
import java.util.List;
import org.locationtech.jts.geom.Polygon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitedHeritageRepository extends
    JpaRepository<VisitedHeritageEntity, Long> {


  boolean existsByMemberEntity_MemberIdAndHeritageEntity_HeritageId(
      String memberId, String heritageId);

  @Query("select v.heritageEntity from VisitedHeritageEntity v where v.memberEntity.memberId = :memberId and ST_Within(v.heritageEntity.location, :polygon) = true")
  List<HeritageEntity> findAllVisitedHeritageByMemberIdWithinPolygon(String memberId,
      Polygon polygon);
}
