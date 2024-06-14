package com.praful.feedapplication.scheduler;

import java.util.List;

import com.praful.feedapplication.constants.CommonConstants;
import com.praful.feedapplication.dao.TransactionDAO;
import com.praful.feedapplication.dao.UserDAO;
import com.praful.feedapplication.protos.SqsMessage;
import com.praful.feedapplication.protos.TransactionDetailsEntity;
import com.praful.feedapplication.protos.TransactionUpdateRequestEntity;
import com.praful.feedapplication.protos.UserAssetDetailsEntity;
import com.praful.feedapplication.protos.UserBalanceUpdateRequestEntity;
import com.praful.feedapplication.protos.UserGoldUpdateRequestEntity;
import com.praful.feedapplication.utils.SqsUtils;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class SQSReadMessageScheduler {
    private final TransactionDAO transactionDAO;
    private final UserDAO userDAO;
    private final SqsUtils sqsUtils;

    public SQSReadMessageScheduler(TransactionDAO transactionDAO, UserDAO userDAO, SqsUtils sqsUtils) {
        this.transactionDAO = transactionDAO;
        this.userDAO = userDAO;
        this.sqsUtils = sqsUtils;
    }

    @Scheduled(fixedRate = 5000)
    public void receiveMessageFromQueue() {
        List<SqsMessage> sqsMessageList = sqsUtils.receiveMsg();
            for (SqsMessage m : sqsMessageList) {
                String transactionId = m.getTransactionId();
                double goldAmount = m.getGoldBalance();
                double transactionAmount = m.getAmountBalance();
                String mode = m.getMode();

                TransactionDetailsEntity transactionDetail = transactionDAO.fetchTransactionDetailById(transactionId);
                String username = transactionDetail.getUsername();

                UserAssetDetailsEntity userAsset = userDAO.fetchUserAssetDetail(username);

                double userGoldBalance = userAsset.getGoldBalance();
                double userAmountBalance = userAsset.getAmountBalance();

                if (mode.equals(CommonConstants.TransactionType.BUY.toString())) {
                    double userUpdatedGoldBalance = goldAmount + userGoldBalance;
                    UserGoldUpdateRequestEntity userGoldUpdateRequest = UserGoldUpdateRequestEntity.newBuilder()
                                    .setUsername(username)
                                    .setUpdatedGoldBalance(userUpdatedGoldBalance).build();
                    userDAO.updateUserGoldBalance(userGoldUpdateRequest);

                    TransactionUpdateRequestEntity transactionUpdateRequest = TransactionUpdateRequestEntity.newBuilder()
                                    .setTransactionId(transactionId)
                                    .setStatus(CommonConstants.TransactionStatus.SUCCESSFUL.toString()).build();

                    transactionDAO.updateTransactionStatus(transactionUpdateRequest);
                } else {
                    double userUpdatedAmountBalance = transactionAmount + userAmountBalance;
                    UserBalanceUpdateRequestEntity userGoldUpdateRequest = UserBalanceUpdateRequestEntity.newBuilder()
                            .setUsername(username)
                            .setUpdatedAmountBalance(userUpdatedAmountBalance).build();
                    userDAO.updateUserAmountBalance(userGoldUpdateRequest);

                    TransactionUpdateRequestEntity transactionUpdateRequest = TransactionUpdateRequestEntity.newBuilder()
                            .setTransactionId(transactionId)
                            .setStatus(CommonConstants.TransactionStatus.SUCCESSFUL.toString()).build();
                    transactionDAO.updateTransactionStatus(transactionUpdateRequest);
                }
            }
    }
}

