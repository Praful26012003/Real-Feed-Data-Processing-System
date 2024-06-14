package com.praful.feedapplication.controller;

import com.praful.feedapplication.protos.PageRequestDTO;
import com.praful.feedapplication.protos.SpotPriceByPaginationDTO;
import com.praful.feedapplication.protos.SpotPriceResponseDTO;
import com.praful.feedapplication.service.AssetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gold")
public class AssetController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssetController.class);
    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping("/spot-price")
    public ResponseEntity<SpotPriceResponseDTO> fetchAssetSpotPrice() {
        LOGGER.info("Request to fetch Gold Spot Price");
        return ResponseEntity.ok(assetService.fetchAssetSpotPrice());
    }

    @PostMapping("/spot-prices")
    public ResponseEntity<SpotPriceByPaginationDTO> fetchAssetSpotPrices(@RequestBody PageRequestDTO pageRequestDTO) {
        LOGGER.info("page request to fetch spot prices data of gold- {}", pageRequestDTO);
        return ResponseEntity.ok(assetService.fetchAssetSpotPrices(pageRequestDTO));
    }
}
