package com.zerobase.zerobaseheritage.repository;

import com.zerobase.zerobaseheritage.entity.HeritageEntity;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HeritageRepository extends
    JpaRepository<HeritageEntity, Long> {

  @Query(value = "SELECT h FROM HeritageEntity h WHERE ST_Distance(h.location, :point) < :distanceMeter")
  List<HeritageEntity> findWithinDistance(Point point, int distanceMeter);

  @Query("SELECT h FROM HeritageEntity h WHERE ST_Within(h.location, :polygon) = true")
  List<HeritageEntity> findWithinPolygon(Polygon polygon);

  @Modifying
  @Query(value = "INSERT INTO heritage_entity"
      + " (heritage_Id, heritage_Name, location, heritage_Grade, basic_Description)" 
      + " VALUES (:heritageId, :heritageName, :location, :heritageGrade, :basicDescription)" 
      + " ON DUPLICATE KEY UPDATE" 
      + " heritage_Name = :heritageName, location = :location, heritage_Grade =" 
      + " :heritageGrade, basic_Description = :basicDescription", nativeQuery = true)
  int insertOrUpdate(String heritageId, String heritageName, Point location,
      String heritageGrade, String basicDescription);


  Optional<HeritageEntity> findByHeritageId(String heritageId);


}







