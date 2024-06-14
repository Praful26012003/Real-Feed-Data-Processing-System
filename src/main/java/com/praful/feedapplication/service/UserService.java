package com.praful.feedapplication.service;

import com.praful.feedapplication.protos.AuthRequestDTO;
import com.praful.feedapplication.protos.AuthResponseDTO;
import com.praful.feedapplication.protos.TransactionDetailsListDTO;
import com.praful.feedapplication.protos.UserAssetDetailsDTO;
import com.praful.feedapplication.protos.UserRequestDTO;
import com.praful.feedapplication.protos.UserResponseDTO;

public interface UserService {
    UserResponseDTO addUser(UserRequestDTO userRequestDTO);

    AuthResponseDTO authenticateUser(AuthRequestDTO authRequestDTO);

    UserResponseDTO removeUser(String username);

    UserAssetDetailsDTO fetchUserAssetDetail(String username);

    TransactionDetailsListDTO fetchUserTransactionDetails(String username);
}
