package com.backend.warehouse.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.warehouse.entity.Shelf;

public interface ShelfRepository extends JpaRepository<Shelf, Long> {
    List<Shelf> findByWarehouse_WarehouseId(Long warehouseId);
}
