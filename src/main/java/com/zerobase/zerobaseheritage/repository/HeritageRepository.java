package com.zerobase.zerobaseheritage.repository;

import com.zerobase.zerobaseheritage.entity.HeritageEntity;
import java.util.List;
import java.util.Optional;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HeritageRepository extends JpaRepository<HeritageEntity, Long> {

  @Query(value = "SELECT h FROM HeritageEntity h WHERE ST_Distance(h.location, :point) < :distanceMeter")
  List<HeritageEntity> findWithinDistance(Point point, int distanceMeter);

  @Modifying
  @Query(value = "INSERT IGNORE INTO heritage_entity (heritage_Id, heritage_Name, location, heritage_Grade, basic_Description) VALUES (:heritageId, :heritageName, :location, :heritageGrade,:basicDescription)", nativeQuery = true)
  int insertIgnore(String heritageId, String heritageName, Point location, String heritageGrade, String basicDescription);

  Optional<HeritageEntity> findByHeritageId(String heritageId);
}
