package com.ankush.gotstock.repository;

import com.ankush.gotstock.model.StockHolding;

import com.ankush.gotstock.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockHoldingRepository extends JpaRepository<StockHolding, Long> {
    List<StockHolding> findByUser(User user);
    List<StockHolding> findByUserId(Long userId);
    }

