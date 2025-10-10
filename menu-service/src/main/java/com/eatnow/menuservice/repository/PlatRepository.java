package com.eatnow.menuservice.repository;

import com.eatnow.menuservice.model.Plat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlatRepository extends JpaRepository<Plat, UUID> {
}