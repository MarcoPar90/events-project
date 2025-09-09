package com.application.events.repository;

import com.application.events.entity.Events;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EventsRepository extends JpaRepository<Events, Integer> {

    @Query(value = "SELECT * FROM events WHERE band_id = :id", nativeQuery = true)
    Optional<Events> findByBandId(@Param("id") int id);

    @Query(value = "SELECT * FROM events WHERE title = :title OR event_code = :eventCode", nativeQuery = true)
    Optional<Events> findByTitleOrEventCode(@Param("title") String title, @Param("eventCode") String eventCode);

}
