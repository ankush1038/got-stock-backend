package com.ankush.gotstock.service;

import com.ankush.gotstock.dto.AlertDTO;
import com.ankush.gotstock.model.StockAlert;
import com.ankush.gotstock.model.StockHolding;
import com.ankush.gotstock.model.User;
import com.ankush.gotstock.repository.StockAlertRepository;
import com.ankush.gotstock.repository.StockHoldingRepository;
import com.ankush.gotstock.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Slf4j
public class StockAlertService {

    @Autowired
    private StockAlertRepository stockAlertRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExternalApiService externalApiService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private StockHoldingRepository stockHoldingRepository;

    // Create a new stock alert
    public void createAlert(AlertDTO alertDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);

        if (user == null) {
            log.error("User not found: {}", email);
            throw new RuntimeException("User not found");
        }

        StockAlert alert = new StockAlert();
        alert.setSymbol(alertDTO.getSymbol());
        alert.setUpperThreshold(alertDTO.getUpperThreshold());
        alert.setLowerThreshold(alertDTO.getLowerThreshold());
        alert.setUser(user);
        alert.setUpperThresholdEmailSent(false);
        alert.setLowerThresholdEmailSent(false);

        stockAlertRepository.save(alert);
        log.info("Stock alert created for user: {}, symbol: {}", user.getUsername(), alertDTO.getSymbol());
    }

    // Scheduled task to check all stock alerts every 5 minutes
    @Scheduled(fixedRate = 300000)
    public void checkStockAlerts() {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            List<StockAlert> alerts = stockAlertRepository.findByUser(user);

            for (StockAlert alert : alerts) {
                checkAlertThresholds(alert, user);
            }
        }
    }

    // Check thresholds
    private void checkAlertThresholds(StockAlert alert, User user) {
        try {

            BigDecimal currentPrice = BigDecimal.valueOf(
                    externalApiService.fetchStockData(alert.getSymbol())
            );

            boolean sendUpperEmail =
                    currentPrice.compareTo(BigDecimal.valueOf(alert.getUpperThreshold())) >= 0 &&
                            !alert.isUpperThresholdEmailSent();

            boolean sendLowerEmail =
                    currentPrice.compareTo(BigDecimal.valueOf(alert.getLowerThreshold())) <= 0 &&
                            !alert.isLowerThresholdEmailSent();

            if (sendUpperEmail) {
                emailService.sendEmail(
                        user.getEmail(),
                        "Stock Alert: Upper Threshold Reached for " + alert.getSymbol(),
                        "Dear " + user.getUsername() +
                                ",\n\nStock reached upper threshold.\nPrice: $" + currentPrice
                );

                alert.setUpperThresholdEmailSent(true);
                stockAlertRepository.save(alert);
            }

            if (sendLowerEmail) {
                emailService.sendEmail(
                        user.getEmail(),
                        "Stock Alert: Lower Threshold Reached for " + alert.getSymbol(),
                        "Dear " + user.getUsername() +
                                ",\n\nStock dropped below threshold.\nPrice: $" + currentPrice
                );

                alert.setLowerThresholdEmailSent(true);
                stockAlertRepository.save(alert);
            }

            // reset flags
            if (currentPrice.compareTo(BigDecimal.valueOf(alert.getUpperThreshold())) < 0) {
                alert.setUpperThresholdEmailSent(false);
            }

            if (currentPrice.compareTo(BigDecimal.valueOf(alert.getLowerThreshold())) > 0) {
                alert.setLowerThresholdEmailSent(false);
            }

        } catch (Exception e) {
            log.error("Error checking stock alert: {}", e.getMessage());
        }
    }

    /**
     * Calculates total portfolio value
     */
    private BigDecimal calculatePortfolioValue(Long userId) {

        List<StockHolding> holdings = stockHoldingRepository.findByUserId(userId);

        BigDecimal totalValue = BigDecimal.ZERO;

        for (StockHolding holding : holdings) {

            BigDecimal currentPrice = BigDecimal.valueOf(
                    externalApiService.fetchStockData(holding.getSymbol())
            );

            holding.setCurrentPrice(currentPrice);
            stockHoldingRepository.save(holding);

            BigDecimal gain = currentPrice
                    .subtract(holding.getPurchasePrice())
                    .multiply(BigDecimal.valueOf(holding.getQuantity()));

            totalValue = totalValue.add(gain);
        }

        return totalValue;
    }

    /**
     * Scheduled portfolio monitoring
     */
    @Scheduled(fixedRate = 1800000)
    public void checkPortfolioValueChanges() {

        List<User> users = userRepository.findAll();

        for (User user : users) {

            try {
                BigDecimal currentValue = calculatePortfolioValue(user.getId());

                BigDecimal previousValue = user.getPreviousPortfolioValue() != null
                        ? BigDecimal.valueOf(user.getPreviousPortfolioValue())
                        : BigDecimal.ZERO;

                if (previousValue.compareTo(BigDecimal.ZERO) > 0) {

                    BigDecimal change = currentValue.subtract(previousValue);

                    BigDecimal percentage = change
                            .divide(previousValue, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100));

                    if (percentage.abs().compareTo(BigDecimal.valueOf(5)) >= 0) {

                        emailService.sendEmail(
                                user.getEmail(),
                                "Portfolio Alert",
                                "Portfolio changed by " + percentage + "%"
                        );
                    }
                }

                user.setPreviousPortfolioValue(currentValue.doubleValue());
                userRepository.save(user);

            } catch (Exception e) {
                log.error("Portfolio check error: {}", e.getMessage());
            }
        }
    }
}