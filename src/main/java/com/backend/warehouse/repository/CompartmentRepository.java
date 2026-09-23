package com.backend.warehouse.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backend.warehouse.entity.Compartment;
import com.backend.warehouse.entity.Shelf;

import jakarta.persistence.LockModeType;


public interface CompartmentRepository extends JpaRepository<Compartment, Long> {
    Optional<Compartment> findByNameCompAndLayerIndexAndShelf(String nameComp, int layerIndex, Shelf shelf);
    Compartment findByShelfAndNameComp(Shelf shelf, String nameComp);

    @Query("SELECT SUM(c.quantity) FROM Compartment c WHERE c.item.itemId = :itemId")
    Integer sumQuantityByItemId(@Param("itemId") Long itemId);

    List<Compartment> findByItem_ItemId(Long itemId);

    // Pessimistic lock - dùng cho checkout để tránh race condition
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Compartment c WHERE c.compId = :id")
    Optional<Compartment> findByIdWithLock(@Param("id") Long id);
}


