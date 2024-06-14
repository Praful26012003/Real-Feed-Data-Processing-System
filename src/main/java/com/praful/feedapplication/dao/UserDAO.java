package com.praful.feedapplication.dao;

import com.praful.feedapplication.protos.TransactionDetailsListEntity;
import com.praful.feedapplication.protos.UserAssetDetailsEntity;
import com.praful.feedapplication.protos.UserBalanceUpdateRequestEntity;
import com.praful.feedapplication.protos.UserGoldUpdateRequestEntity;
import com.praful.feedapplication.protos.UserRequestEntity;
import com.praful.feedapplication.protos.UserResponseEntity;

public interface UserDAO {
    UserResponseEntity loadUserByUsername(String username);

    UserResponseEntity addUser(UserRequestEntity newUser);

    UserResponseEntity removeUser(String username);

    UserAssetDetailsEntity fetchUserAssetDetail(String username);

    int updateUserAmountBalance(UserBalanceUpdateRequestEntity userBalanceUpdateRequest);

    int updateUserGoldBalance(UserGoldUpdateRequestEntity userGoldUpdateRequest);

    TransactionDetailsListEntity fetchUserTransactionDetails(String username);
}
