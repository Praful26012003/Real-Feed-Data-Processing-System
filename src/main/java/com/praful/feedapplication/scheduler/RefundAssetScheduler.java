package com.praful.feedapplication.scheduler;

import java.util.List;

import com.praful.feedapplication.constants.CommonConstants;
import com.praful.feedapplication.controller.AssetController;
import com.praful.feedapplication.dao.TransactionDAO;
import com.praful.feedapplication.dao.UserDAO;
import com.praful.feedapplication.protos.TransactionDetailsEntity;
import com.praful.feedapplication.protos.TransactionDetailsListEntity;
import com.praful.feedapplication.protos.TransactionUpdateRequestEntity;
import com.praful.feedapplication.protos.UserAssetDetailsEntity;
import com.praful.feedapplication.protos.UserBalanceUpdateRequestEntity;
import com.praful.feedapplication.protos.UserGoldUpdateRequestEntity;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class RefundAssetScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssetController.class);
    private final TransactionDAO transactionDAO;
    private final UserDAO userDAO;

    public RefundAssetScheduler(TransactionDAO transactionDAO, UserDAO userDAO) {
        this.transactionDAO = transactionDAO;
        this.userDAO = userDAO;
    }

    @Scheduled(fixedRate = 10000)
    @Transactional
    public void refundAsset() {
        LOGGER.info("Refund scheduler is running");
        String transactionStatus = CommonConstants.TransactionStatus.PENDING.toString();
        TransactionDetailsListEntity pendingTransactionsResponse = transactionDAO.fetchTransactionDetailsBasedOnStatus(transactionStatus);
        List<TransactionDetailsEntity> pendingTransactionsList = pendingTransactionsResponse.getTransactionsList();
        LOGGER.info("Pending transactions {}",pendingTransactionsList);
        pendingTransactionsList.forEach(pendingTransaction -> {
            String mode = pendingTransaction.getMode();
            UserAssetDetailsEntity userAsset = userDAO.fetchUserAssetDetail(pendingTransaction.getUsername());

            if (mode.equals(CommonConstants.TransactionType.BUY.toString())) {
                double userAmountBalance = userAsset.getAmountBalance();
                LOGGER.info("user amount balance is {}", userAmountBalance);
                double userAmountToBeRefunded = pendingTransaction.getAmountBalance();
                LOGGER.info("user amount to be refunded is {}", userAmountToBeRefunded);
                double userUpdatedBalance = userAmountBalance + userAmountToBeRefunded;
                UserBalanceUpdateRequestEntity userBalanceUpdateRequest = UserBalanceUpdateRequestEntity.newBuilder()
                        .setUpdatedAmountBalance(userUpdatedBalance).setUsername(pendingTransaction.getUsername()).build();

                userDAO.updateUserAmountBalance(userBalanceUpdateRequest);
            } else {
                double userGoldBalance = userAsset.getGoldBalance();
                double userGoldToBeRefunded = pendingTransaction.getGoldBalance();
                double userUpdatedGoldBalance = userGoldBalance + userGoldToBeRefunded;
                UserGoldUpdateRequestEntity userGoldUpdateRequest = UserGoldUpdateRequestEntity.newBuilder()
                        .setUsername(pendingTransaction.getUsername())
                        .setUpdatedGoldBalance(userUpdatedGoldBalance).build();
                userDAO.updateUserGoldBalance(userGoldUpdateRequest);
            }
            TransactionUpdateRequestEntity transactionUpdateRequest = TransactionUpdateRequestEntity.newBuilder()
                    .setTransactionId(pendingTransaction.getTransactionId())
                    .setStatus(CommonConstants.TransactionStatus.REFUNDED.toString()).build();
            transactionDAO.updateTransactionStatus(transactionUpdateRequest);
        });
    }
}
