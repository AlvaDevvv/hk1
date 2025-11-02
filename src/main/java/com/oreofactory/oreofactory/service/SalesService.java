package com.oreofactory.oreofactory.service;

import com.oreofactory.oreofactory.dto.request.SaleRequestDTO;
import com.oreofactory.oreofactory.model.entity.Sale;
import com.oreofactory.oreofactory.model.entity.User;
import com.oreofactory.oreofactory.model.enums.Role;
import com.oreofactory.oreofactory.repository.SaleRepository;
import com.oreofactory.oreofactory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Transactional
public class SalesService {

    private final SaleRepository saleRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public Sale createSale(SaleRequestDTO request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Si el usuario es BRANCH, solo puede crear ventas en su sucursal
        if (Role.ROLE_BRANCH.equals(user.getRole()) && !user.getBranch().equals(request.getBranch())) {
            throw new RuntimeException("User can only create sales for their own branch");
        }

        Sale sale = modelMapper.map(request, Sale.class);
        sale.setSoldAt(request.getSoldAt() != null ? request.getSoldAt() : LocalDateTime.now());
        sale.setCreatedBy(username);

        return saleRepository.save(sale);
    }

    public Sale getSale(String id, Authentication authentication) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found"));

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Si el usuario es BRANCH, solo puede ver ventas de su sucursal
        if (Role.ROLE_BRANCH.equals(user.getRole()) && !user.getBranch().equals(sale.getBranch())) {
            throw new RuntimeException("User can only view sales from their own branch");
        }

        return sale;
    }

    public Page<Sale> getSales(LocalDate startDate, LocalDate endDate, String branch, Pageable pageable, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Si el usuario es BRANCH, solo puede ver ventas de su sucursal
        if (Role.ROLE_BRANCH.equals(user.getRole())) {
            branch = user.getBranch();
        }

        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : LocalDateTime.of(1970, 1, 1, 0, 0);
        LocalDateTime end = endDate != null ? endDate.atTime(LocalTime.MAX) : LocalDateTime.now();

        if (branch != null) {
            return saleRepository.findByBranchAndSoldAtBetween(branch, start, end, pageable);
        } else {
            return saleRepository.findBySoldAtBetween(start, end, pageable);
        }
    }

    // ✅ AGREGAR ESTE MÉTODO
    public Sale updateSale(String id, SaleRequestDTO request, Authentication authentication) {
        Sale existingSale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found"));

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Si el usuario es BRANCH, solo puede actualizar ventas de su sucursal
        if (Role.ROLE_BRANCH.equals(user.getRole()) && !user.getBranch().equals(existingSale.getBranch())) {
            throw new RuntimeException("User can only update sales from their own branch");
        }

        // Actualizar solo los campos permitidos
        existingSale.setSku(request.getSku());
        existingSale.setUnits(request.getUnits());
        existingSale.setPrice(request.getPrice());
        existingSale.setBranch(request.getBranch());

        if (request.getSoldAt() != null) {
            existingSale.setSoldAt(request.getSoldAt());
        }

        return saleRepository.save(existingSale);
    }

    // ✅ AGREGAR ESTE MÉTODO
    public void deleteSale(String id, Authentication authentication) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found"));

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Solo CENTRAL puede eliminar ventas
        if (!Role.ROLE_CENTRAL.equals(user.getRole())) {
            throw new RuntimeException("Only CENTRAL users can delete sales");
        }

        saleRepository.delete(sale);
    }
}