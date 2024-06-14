package com.praful.feedapplication.service.impl;

import java.util.UUID;

import com.praful.feedapplication.constants.CommonConstants;
import com.praful.feedapplication.protos.SqsMessage;
import com.praful.feedapplication.dao.AssetDAO;
import com.praful.feedapplication.dao.TransactionDAO;
import com.praful.feedapplication.dao.UserDAO;
import com.praful.feedapplication.exception.InsufficientUserBalanceException;
import com.praful.feedapplication.exception.SqsMessageException;
import com.praful.feedapplication.exception.UserNotAuthorizedException;
import com.praful.feedapplication.protos.GoldPerformanceDTO;
import com.praful.feedapplication.protos.TransactionDetailsDTO;
import com.praful.feedapplication.protos.SpotPriceResponseEntity;
import com.praful.feedapplication.protos.TransactionAddRequestEntity;
import com.praful.feedapplication.protos.UserBalanceUpdateRequestEntity;
import com.praful.feedapplication.protos.UserGoldUpdateRequestEntity;
import com.praful.feedapplication.protos.UserAssetDetailsEntity;
import com.praful.feedapplication.protos.TransactionDetailsEntity;
import com.praful.feedapplication.service.TransactionService;
import com.praful.feedapplication.utils.DateTimeConverterUtils;
import com.praful.feedapplication.utils.SqsUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionDAO transactionDAO;
    private final AssetDAO assetDAO;
    private final UserDAO userDAO;
    private final SqsUtils sqsUtils;

    private final DateTimeConverterUtils dateTimeUtils;

    public TransactionServiceImpl(TransactionDAO transactionDAO, AssetDAO assetDAO,
                                  UserDAO userDAO, SqsUtils sqsUtils, DateTimeConverterUtils dateTimeUtils) {
        this.transactionDAO = transactionDAO;
        this.assetDAO = assetDAO;
        this.userDAO = userDAO;
        this.sqsUtils = sqsUtils;
        this.dateTimeUtils = dateTimeUtils;
    }

    @Override
    public TransactionDetailsDTO buyGold(double assetWeight) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        UserAssetDetailsEntity userAsset = userDAO.fetchUserAssetDetail(username);
        double userAmountBalance = userAsset.getAmountBalance();

        SpotPriceResponseEntity goldSpotPrice = assetDAO.fetchAssetSpotPrice();
        double goldValue = goldSpotPrice.getValue();

        double requiredAmount = assetWeight * goldValue;

        String currentTime = dateTimeUtils.getCurrentTimestamp();
        String transactionId = UUID.randomUUID().toString();

        TransactionDetailsDTO.Builder transactionDetailDto = TransactionDetailsDTO.newBuilder();
        transactionDetailDto.setTransactionId(transactionId);
        transactionDetailDto.setTransactionTime(currentTime);
        transactionDetailDto.setUsername(username);
        transactionDetailDto.setGoldBalance(assetWeight);
        transactionDetailDto.setAmountBalance(requiredAmount);
        transactionDetailDto.setMode(CommonConstants.TransactionType.BUY.toString());

        TransactionAddRequestEntity.Builder newTransaction = TransactionAddRequestEntity.newBuilder()
            .setTransactionId(transactionId)
            .setTransactionTime(currentTime)
            .setUsername(username)
            .setGoldBalance(assetWeight)
            .setAmountBalance(requiredAmount)
            .setMode(CommonConstants.TransactionType.BUY.toString());


        if (requiredAmount <= userAmountBalance) {
            double userUpdatedAmountBalance = userAmountBalance - requiredAmount;

            UserBalanceUpdateRequestEntity userBalanceUpdateRequest = UserBalanceUpdateRequestEntity
                .newBuilder().setUsername(username).setUpdatedAmountBalance(userUpdatedAmountBalance).build();

            userDAO.updateUserAmountBalance(userBalanceUpdateRequest);

            transactionDetailDto.setStatus(CommonConstants.TransactionStatus.PROCESSING.toString());
            newTransaction.setStatus(CommonConstants.TransactionStatus.PENDING.toString());

            TransactionDetailsEntity transactionDetailsEntity = transactionDAO.addTransaction(newTransaction.build());

            SqsMessage.Builder sqsMessage = SqsMessage.newBuilder();
            sqsMessage.setTransactionId(transactionDetailsEntity.getTransactionId());
            sqsMessage.setGoldBalance(transactionDetailsEntity.getGoldBalance());
            sqsMessage.setAmountBalance(transactionDetailsEntity.getAmountBalance());
            sqsMessage.setMode(CommonConstants.TransactionType.BUY.toString());

            try {
                sqsUtils.sendMsg(sqsMessage.build());
            } catch (Exception e) {
                throw new SqsMessageException("Error sending the message into the sqs");
            }
        } else {
            transactionDetailDto.setStatus(CommonConstants.TransactionStatus.FAILED.toString());
            newTransaction.setStatus(CommonConstants.TransactionStatus.FAILED.toString());
            transactionDAO.addTransaction(newTransaction.build());
      }
        return transactionDetailDto.build();
    }

    @Override
    public TransactionDetailsDTO sellGold(double assetWeight) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        UserAssetDetailsEntity userAsset = userDAO.fetchUserAssetDetail(username);
        double userGoldBalance = userAsset.getGoldBalance();

        SpotPriceResponseEntity goldSpotPrice = assetDAO.fetchAssetSpotPrice();
        double goldValue = goldSpotPrice.getValue();

        double amountGain = assetWeight * goldValue;

        String timeStamp = dateTimeUtils.getCurrentTimestamp();
        String transactionId = UUID.randomUUID().toString();

        TransactionDetailsDTO.Builder transactionDetailDto = TransactionDetailsDTO.newBuilder();
        transactionDetailDto.setTransactionId(transactionId);
        transactionDetailDto.setTransactionTime(timeStamp);
        transactionDetailDto.setGoldBalance(assetWeight);
        transactionDetailDto.setAmountBalance(amountGain);
        transactionDetailDto.setMode(CommonConstants.TransactionType.SELL.toString());

        TransactionAddRequestEntity.Builder newTransaction = TransactionAddRequestEntity.newBuilder()
            .setTransactionId(transactionId)
            .setTransactionTime(timeStamp)
            .setUsername(username)
            .setGoldBalance(assetWeight)
            .setAmountBalance(amountGain)
            .setMode(CommonConstants.TransactionType.SELL.toString());

        if (assetWeight <= userGoldBalance) {
            double userUpdatedGoldBalance = userGoldBalance - assetWeight;

            UserGoldUpdateRequestEntity userGoldUpdateRequest = UserGoldUpdateRequestEntity.newBuilder()
                .setUsername(username)
                .setUpdatedGoldBalance(userUpdatedGoldBalance).build();

            userDAO.updateUserGoldBalance(userGoldUpdateRequest);

            transactionDetailDto.setStatus(CommonConstants.TransactionStatus.PROCESSING.toString());
            newTransaction.setStatus(CommonConstants.TransactionStatus.PENDING.toString());

            TransactionDetailsEntity transactionDetailsEntity = transactionDAO.addTransaction(newTransaction.build());

            SqsMessage.Builder sqsMessage = SqsMessage.newBuilder();
            sqsMessage.setTransactionId(transactionDetailsEntity.getTransactionId());
            sqsMessage.setGoldBalance(transactionDetailsEntity.getGoldBalance());
            sqsMessage.setAmountBalance(transactionDetailsEntity.getAmountBalance());
            sqsMessage.setMode(CommonConstants.TransactionType.SELL.toString());

            try {
                sqsUtils.sendMsg(sqsMessage.build());
            } catch (Exception e) {
                throw new SqsMessageException("Error sending the message into the sqs");
            }
        } else {
            transactionDetailDto.setStatus(CommonConstants.TransactionStatus.FAILED.toString());
            newTransaction.setStatus(CommonConstants.TransactionStatus.FAILED.toString());

            transactionDAO.addTransaction(newTransaction.build());
        }

        return transactionDetailDto.build();
    }

    @Override
    public TransactionDetailsDTO fetchTransactionDetailById(String id) {

        TransactionDetailsEntity transactionDetailOfId = transactionDAO.fetchTransactionDetailById(id);

        TransactionDetailsDTO transactionDetailResponse = TransactionDetailsDTO.newBuilder()
                .setTransactionId(transactionDetailOfId.getTransactionId())
                .setTransactionTime(transactionDetailOfId.getTransactionTime())
                .setUsername(transactionDetailOfId.getUsername())
                .setGoldBalance(transactionDetailOfId.getGoldBalance())
                .setAmountBalance(transactionDetailOfId.getAmountBalance())
                .setMode(transactionDetailOfId.getMode())
                .setStatus(transactionDetailOfId.getStatus())
                .build();

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentLoggedInUser = userDetails.getUsername();

        String userFetched = transactionDetailOfId.getUsername();


        if (userFetched.equals(currentLoggedInUser)) {
            return transactionDetailResponse;
        }

        throw new UserNotAuthorizedException("You are not allowed to access this resource");
    }

    @Override
    public GoldPerformanceDTO fetchAssetPerformance() {

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        UserAssetDetailsEntity userAsset = userDAO.fetchUserAssetDetail(username);

        double userGoldBalance = userAsset.getGoldBalance();
        double userAmountBalance = userAsset.getAmountBalance();

        if (userGoldBalance == 0) {
            throw new InsufficientUserBalanceException("User has no gold right now");
        }

        SpotPriceResponseEntity goldSpotPrice = assetDAO.fetchAssetSpotPrice();
        double goldValue = goldSpotPrice.getValue();
        double userGoldValue = userGoldBalance * goldValue;

        double userInitialAmountBalance = CommonConstants.USER_INITIAL_AMOUNT_BALANCE;
        double userInvestedAmount = userInitialAmountBalance - userAmountBalance;

        double profitOrLoss = userGoldValue - userInvestedAmount;
        double percentage = (profitOrLoss / userInitialAmountBalance) * 100;

        return GoldPerformanceDTO.newBuilder().setUsername(username)
                .setGoldBalance(userGoldBalance)
                .setInvestedAmount(userInvestedAmount)
                .setPerformance(percentage).build();
    }
}