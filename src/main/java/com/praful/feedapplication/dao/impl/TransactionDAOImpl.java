package com.praful.feedapplication.dao.impl;

import java.util.List;

import com.praful.feedapplication.controller.AssetController;
import com.praful.feedapplication.dao.SQLHandler;
import com.praful.feedapplication.dao.TransactionDAO;
import com.praful.feedapplication.exception.DatabaseException;
import com.praful.feedapplication.protos.TransactionDetailsEntity;
import com.praful.feedapplication.protos.TransactionDetailsListEntity;
import com.praful.feedapplication.protos.TransactionAddRequestEntity;
import com.praful.feedapplication.protos.TransactionUpdateRequestEntity;
import com.praful.feedapplication.utils.DateTimeConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionDAOImpl implements TransactionDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssetController.class);
    private final JdbcTemplate jdbcTemplate;
    private final DateTimeConverterUtils dateTimeUtils;

    public TransactionDAOImpl(JdbcTemplate jdbcTemplate, DateTimeConverterUtils dateTimeUtils) {
        this.jdbcTemplate = jdbcTemplate;
        this.dateTimeUtils = dateTimeUtils;
    }

    public int updateTransactionStatus(TransactionUpdateRequestEntity updatedTransaction) {
        int rowsUpdated = 0;
        rowsUpdated = jdbcTemplate.update(SQLHandler.UPDATE_TRANSACTION_STATUS, updatedTransaction.getStatus(), updatedTransaction.getTransactionId());
        if (rowsUpdated == 0) {
            throw new DatabaseException("Error updating transaction status");
        }
        return rowsUpdated;
    }

    public TransactionDetailsEntity addTransaction(TransactionAddRequestEntity newTransaction) {
        jdbcTemplate.update(SQLHandler.INSERT_TRANSACTION, newTransaction.getTransactionId(), newTransaction.getUsername(), newTransaction.getGoldBalance(), newTransaction.getAmountBalance(), newTransaction.getMode(), newTransaction.getTransactionTime(), newTransaction.getStatus());
        return TransactionDetailsEntity.newBuilder().setTransactionId(newTransaction.getTransactionId())
                .setTransactionTime(newTransaction.getTransactionTime())
                .setGoldBalance(newTransaction.getGoldBalance())
                .setAmountBalance(newTransaction.getAmountBalance())
                .setUsername(newTransaction.getUsername())
                .setMode(newTransaction.getMode())
                .setStatus(newTransaction.getStatus())
                .build();
    }

    public TransactionDetailsEntity fetchTransactionDetailById(String transactionId) {
        TransactionDetailsEntity transactionDetail = jdbcTemplate.query(SQLHandler.FETCH_TRANSACTION_BY_TRANSACTION_ID, new Object[]{transactionId},  rs -> {
            if (rs.next()) {
                TransactionDetailsEntity.Builder entity = TransactionDetailsEntity.newBuilder();
                entity.setTransactionId(rs.getString("transaction_id"));
                entity.setTransactionTime(dateTimeUtils.convertTimestampToString(rs.getTimestamp("update_ts")));
                entity.setUsername(rs.getString("username"));
                entity.setGoldBalance(rs.getDouble("gold"));
                entity.setAmountBalance(rs.getDouble("amount"));
                entity.setMode(rs.getString("mode"));
                entity.setStatus(rs.getString("status"));

                return entity.build();
            }
            return null;
        });

        return transactionDetail;
    }

    public TransactionDetailsListEntity fetchTransactionDetailsBasedOnStatus(String transactionStatus) {
        LOGGER.info("request for pending transactions");
        List<TransactionDetailsEntity> resultList = jdbcTemplate.query(SQLHandler.FETCH_TRANSACTION_BY_STATUS, (rs, rowNum) -> {
            TransactionDetailsEntity.Builder entity = TransactionDetailsEntity.newBuilder();

            entity.setTransactionId(rs.getString("transaction_id"));
            entity.setTransactionTime(dateTimeUtils.convertTimestampToString(rs.getTimestamp("update_ts")));
            entity.setUsername(rs.getString("username"));
            entity.setGoldBalance(rs.getDouble("gold"));
            entity.setAmountBalance(rs.getDouble("amount"));
            entity.setMode(rs.getString("mode"));
            entity.setStatus(rs.getString("status"));

            return entity.build();

        }, transactionStatus);

        return TransactionDetailsListEntity.newBuilder().addAllTransactions(resultList).build();
    }

}
