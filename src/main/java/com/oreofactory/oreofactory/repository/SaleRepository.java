package com.oreofactory.oreofactory.repository;

import com.oreofactory.oreofactory.model.entity.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sale, String> {
    List<Sale> findByBranch(String branch);
    List<Sale> findBySoldAtBetween(LocalDateTime start, LocalDateTime end);
    List<Sale> findByBranchAndSoldAtBetween(String branch, LocalDateTime start, LocalDateTime end);

    @Query("SELECT s FROM Sale s WHERE s.soldAt BETWEEN :start AND :end AND (:branch IS NULL OR s.branch = :branch)")
    List<Sale> findByDateRangeAndOptionalBranch(@Param("start") LocalDateTime start,
                                                @Param("end") LocalDateTime end,
                                                @Param("branch") String branch);

    Page<Sale> findByBranchAndSoldAtBetween(String branch, LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<Sale> findBySoldAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
}
