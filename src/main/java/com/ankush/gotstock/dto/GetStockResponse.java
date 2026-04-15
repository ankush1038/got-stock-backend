package com.ankush.gotstock.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GetStockResponse {

    private List<StockHoldingDTO> list=new ArrayList<>();
    private Double totalPortfolioValue;
}
