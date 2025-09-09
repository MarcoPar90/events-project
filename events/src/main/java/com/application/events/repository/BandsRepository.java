package com.application.events.repository;

import com.application.events.entity.Bands;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BandsRepository  extends JpaRepository<Bands, Integer> {

    @Query(value = "SELECT * FROM bands WHERE name = :name", nativeQuery = true)
    Optional<Bands> findByName(@Param("name") String name);
}
