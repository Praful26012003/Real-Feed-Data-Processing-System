package com.praful.feedapplication.scheduler;

import com.praful.feedapplication.protos.SpotPriceRequestDTO;
import com.praful.feedapplication.protos.SpotPricesRequestEntity;
import com.praful.feedapplication.service.AssetService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@EnableScheduling
public class GoldPriceScheduler {
    private final RestTemplate restTemplate;
    private final AssetService assetService;

    public GoldPriceScheduler(RestTemplate restTemplate, AssetService assetService) {
        this.restTemplate = restTemplate;
        this.assetService = assetService;
    }

    @Scheduled(fixedRate = 300000)
    public void fetchAssetSpotPrice() {
        String uri = "https://goldbroker.com/api/spot-price?metal=XAU&currency=INR&weight_unit=g";
        SpotPriceRequestDTO spotPrice = restTemplate.getForObject(uri, SpotPriceRequestDTO.class);
        assetService.addAssetSpotPrice(spotPrice);
    }

    @Scheduled(fixedRate = 86400000)
    public void fetchHistoricalSpotPrice() {
        String uri = "https://goldbroker.com/api/spot-prices?metal=XAU&currency=INR&weight_unit=g";
        SpotPricesRequestEntity spotPrices = restTemplate.getForObject(uri, SpotPricesRequestEntity.class);
        assetService.addAssetHistoricalSpotPrice(spotPrices);
    }
}
