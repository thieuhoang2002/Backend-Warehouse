package com.backend.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.warehouse.entity.Position;

public interface PositionRepository extends JpaRepository<Position, Long> {
}
