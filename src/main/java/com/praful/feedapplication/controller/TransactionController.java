package com.praful.feedapplication.controller;

import com.praful.feedapplication.exception.InvalidInputException;
import com.praful.feedapplication.protos.GoldPerformanceDTO;
import com.praful.feedapplication.protos.TransactionDetailsDTO;
import com.praful.feedapplication.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transaction")
public class TransactionController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssetController.class);
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/buy-gold")
    public ResponseEntity<TransactionDetailsDTO> buyGold(@RequestParam(name = "weight") double assetWeight) {
        if (assetWeight < 0) {
            throw new InvalidInputException("weight of gold requested should be positive");
        }
        LOGGER.info("request for buying gold of quantity {} gm", assetWeight);
        return new ResponseEntity<>(transactionService.buyGold(assetWeight), HttpStatus.OK);
    }

    @PostMapping("/sell-gold")
    public ResponseEntity<TransactionDetailsDTO> sellGold(@RequestParam(name = "weight") double assetWeight) {
        if (assetWeight < 0) {
            throw new InvalidInputException("weight of gold requested should be positive");
        }
        LOGGER.info("request for selling gold of quantity {} gm", assetWeight);
        return new ResponseEntity<>(transactionService.sellGold(assetWeight), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDetailsDTO> fetchTransactionDetailById(@PathVariable String id) {
        LOGGER.info("request for fetching the transaction with id {}", id);
        return new ResponseEntity<>(transactionService.fetchTransactionDetailById(id), HttpStatus.OK);
    }

    @GetMapping("/gold-performance")
    public ResponseEntity<GoldPerformanceDTO> fetchPerformanceOfAsset() {
        LOGGER.info("request for fetching the performance of Asset");
        return new ResponseEntity<>(transactionService.fetchAssetPerformance(), HttpStatus.OK);
    }
}
