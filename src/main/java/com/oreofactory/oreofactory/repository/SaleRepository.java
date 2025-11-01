package com.oreofactory.oreofactory.repository;

import com.oreofactory.oreofactory.model.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sale, String> {
    List<Sale> findByBranch(String branch);
    List<Sale> findByBranchAndSoldAtBetween(String branch, LocalDateTime from, LocalDateTime to, Pageable pageable);
    List<Sale> findBySoldAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);
}
