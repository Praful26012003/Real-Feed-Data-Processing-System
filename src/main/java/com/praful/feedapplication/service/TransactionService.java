package com.praful.feedapplication.service;

import com.praful.feedapplication.protos.GoldPerformanceDTO;
import com.praful.feedapplication.protos.TransactionDetailsDTO;
import jakarta.transaction.Transactional;

public interface TransactionService {
    @Transactional
    TransactionDetailsDTO buyGold(double assetWeightRequested);

    @Transactional
    TransactionDetailsDTO sellGold(double assetWeightRequested);

    TransactionDetailsDTO fetchTransactionDetailById(String id);

    GoldPerformanceDTO fetchAssetPerformance();
}
