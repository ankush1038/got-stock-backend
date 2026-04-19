package com.ankush.gotstock.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
public class StockHolding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;

    private Integer quantity;

    private BigDecimal purchasePrice;

    private BigDecimal currentPrice;

    private BigDecimal gainLoss;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}