package com.praful.feedapplication.dao.impl;

import java.util.List;

import com.praful.feedapplication.constants.CommonConstants;
import com.praful.feedapplication.dao.SQLHandler;
import com.praful.feedapplication.dao.UserDAO;
import com.praful.feedapplication.exception.DatabaseException;
import com.praful.feedapplication.protos.TransactionDetailsEntity;
import com.praful.feedapplication.protos.UserBalanceUpdateRequestEntity;
import com.praful.feedapplication.protos.UserGoldUpdateRequestEntity;
import com.praful.feedapplication.protos.UserRequestEntity;
import com.praful.feedapplication.protos.UserResponseEntity;
import com.praful.feedapplication.protos.UserAssetDetailsEntity;
import com.praful.feedapplication.protos.TransactionDetailsListEntity;
import com.praful.feedapplication.utils.DateTimeConverterUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserDAOImpl implements UserDAO {

    private final JdbcTemplate jdbcTemplate;
    private final DateTimeConverterUtils dateTimeUtils;

    public UserDAOImpl(JdbcTemplate jdbcTemplate, DateTimeConverterUtils dateTimeUtils) {
        this.jdbcTemplate = jdbcTemplate;
        this.dateTimeUtils = dateTimeUtils;
    }

    public UserResponseEntity loadUserByUsername(String username) {
        UserResponseEntity result = jdbcTemplate.query(SQLHandler.FETCH_USER, new Object[]{username},  rs -> {
            if (rs.next()) {
                UserResponseEntity.Builder entity = UserResponseEntity.newBuilder();
                entity.setUsername(rs.getString("username"));
                entity.setPassword(rs.getString("password"));
                return entity.build();
            }
            return null;
        });
        return result;
    }

    public UserResponseEntity addUser(UserRequestEntity newUser) {
        jdbcTemplate.update(SQLHandler.INSERT_USER, newUser.getUsername(), newUser.getPassword());
        jdbcTemplate.update(SQLHandler.INSERT_USER_ASSET, newUser.getUsername(), CommonConstants.USER_INITIAL_GOLD_BALANCE, CommonConstants.USER_INITIAL_AMOUNT_BALANCE);
        return UserResponseEntity.newBuilder().setUsername(newUser.getUsername()).setMessage(CommonConstants.USER_ADDED).build();
    }

    public UserResponseEntity removeUser(String username) {
        jdbcTemplate.update(SQLHandler.REMOVE_USER, username);
        return UserResponseEntity.newBuilder().setUsername(username).setMessage(CommonConstants.USER_DELETED).build();
    }

    public UserAssetDetailsEntity fetchUserAssetDetail(String username) {
        UserAssetDetailsEntity result = jdbcTemplate.query(SQLHandler.FETCH_USER_ASSET_DETAIL, new Object[]{username}, rs -> {
            if (rs.next()) {
                UserAssetDetailsEntity.Builder entity = UserAssetDetailsEntity.newBuilder();
                entity.setLedgerId(rs.getInt("ledger_id"));
                entity.setUsername(rs.getString("username"));
                entity.setGoldBalance(rs.getDouble("gold"));
                entity.setAmountBalance(rs.getDouble("amount"));

                return entity.build();
            }
            return null;
        });

        return result;
    }

    public TransactionDetailsListEntity fetchUserTransactionDetails(String username) {
        List<TransactionDetailsEntity> resultList = jdbcTemplate.query(SQLHandler.FETCH_USER_TRANSACTIONS, (rs, rowNum) -> {
            TransactionDetailsEntity.Builder entity = TransactionDetailsEntity.newBuilder();

            entity.setTransactionId(rs.getString("transaction_id"));
            entity.setTransactionTime(dateTimeUtils.convertTimestampToString(rs.getTimestamp("update_ts")));
            entity.setUsername(rs.getString("username"));
            entity.setGoldBalance(rs.getDouble("gold"));
            entity.setAmountBalance(rs.getDouble("amount"));
            entity.setMode(rs.getString("mode"));
            entity.setStatus(rs.getString("status"));

            return entity.build();

        }, username);

        return TransactionDetailsListEntity.newBuilder().addAllTransactions(resultList).build();
    }

    public int updateUserAmountBalance(UserBalanceUpdateRequestEntity userBalanceUpdateRequest) {
        int rowsUpdated = 0;
        rowsUpdated = jdbcTemplate.update(SQLHandler.UPDATE_USER_AMOUNT_BALANCE, userBalanceUpdateRequest.getUpdatedAmountBalance(), userBalanceUpdateRequest.getUsername());
        if (rowsUpdated == 0) {
            throw new DatabaseException("Error updating user amount balance");
        }
        return rowsUpdated;
    }

    public int updateUserGoldBalance(UserGoldUpdateRequestEntity userGoldUpdateRequest) {
        int rowsUpdated = 0;
        rowsUpdated = jdbcTemplate.update(SQLHandler.UPDATE_USER_GOLD_BALANCE, userGoldUpdateRequest.getUpdatedGoldBalance(), userGoldUpdateRequest.getUsername());
        if (rowsUpdated == 0) {
            throw new DatabaseException("Error updating user gold balance");
        }
        return rowsUpdated;
    }
}
