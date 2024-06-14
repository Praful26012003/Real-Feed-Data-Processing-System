package com.praful.feedapplication.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.praful.feedapplication.constants.CommonConstants;
import com.praful.feedapplication.controller.AssetController;
import org.slf4j.Logger;
import com.praful.feedapplication.dao.UserDAO;
import com.praful.feedapplication.exception.DuplicateElementException;
import com.praful.feedapplication.exception.InternalServerErrorException;
import com.praful.feedapplication.exception.UsernameNotFoundException;
import com.praful.feedapplication.exception.validation.AttributeValidation;
import com.praful.feedapplication.protos.AuthRequestDTO;
import com.praful.feedapplication.protos.AuthResponseDTO;
import com.praful.feedapplication.protos.UserAssetDetailsDTO;
import com.praful.feedapplication.protos.TransactionDetailsDTO;
import com.praful.feedapplication.protos.UserRequestDTO;
import com.praful.feedapplication.protos.UserRequestEntity;
import com.praful.feedapplication.protos.TransactionDetailsListDTO;
import com.praful.feedapplication.protos.UserAssetDetailsEntity;
import com.praful.feedapplication.protos.TransactionDetailsEntity;
import com.praful.feedapplication.protos.TransactionDetailsListEntity;
import com.praful.feedapplication.protos.UserResponseDTO;
import com.praful.feedapplication.protos.UserResponseEntity;
import com.praful.feedapplication.service.UserService;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AssetController.class);
    private final PasswordEncoder passwordEncoder;
    private final AttributeValidation attributeValidation;
    private final UserDAO userDAO;
    private final JwtServiceImpl jwtServiceImpl;
    private final AuthenticationManager authenticationManager;

    public UserServiceImpl(PasswordEncoder passwordEncoder, AttributeValidation attributeValidation, UserDAO userDAO, JwtServiceImpl jwtServiceImpl,
                           AuthenticationManager authenticationManager) {
        this.passwordEncoder = passwordEncoder;
        this.attributeValidation = attributeValidation;
        this.userDAO = userDAO;
        this.jwtServiceImpl = jwtServiceImpl;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public UserResponseDTO addUser(UserRequestDTO userRequestDTO) {
        String username = userRequestDTO.getUsername();
        attributeValidation.emailValidation(username);
        String password = userRequestDTO.getPassword();
        String encryptedPassword = passwordEncoder.encode(password);

        UserResponseEntity user = userDAO.loadUserByUsername(username);
        if (user != null) {
            throw new DuplicateElementException("User already exists");
        }

        UserRequestEntity newUser = UserRequestEntity.newBuilder().setUsername(username)
            .setPassword(encryptedPassword).build();

        UserResponseEntity userResponse = userDAO.addUser(newUser);

        return UserResponseDTO.newBuilder().setMessage(CommonConstants.USER_CREATED)
            .setUsername(userResponse.getUsername()).build();
    }

    public AuthResponseDTO authenticateUser(AuthRequestDTO authRequestDTO) {
        String username = authRequestDTO.getUsername();
        String password = authRequestDTO.getPassword();
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        if (authentication.isAuthenticated()) {
            String jwtToken = jwtServiceImpl.generateToken(username);
            return AuthResponseDTO.newBuilder()
                .setUsername(username)
                .setAccessToken(jwtToken).build();
        } else {
            LOGGER.error("username not found in the database-{}", username);
            throw new UsernameNotFoundException("invalid user request !");
        }
    }
    @Override
    public UserResponseDTO removeUser(String username) {
        try {
            UserResponseEntity userResponse = userDAO.removeUser(username);
            return UserResponseDTO.newBuilder()
                .setUsername(userResponse.getUsername())
                .setMessage(userResponse.getMessage())
                .build();
        } catch (Exception e) {
            throw new InternalServerErrorException("User is not present in the database");
        }
    }

    @Override
    public UserAssetDetailsDTO fetchUserAssetDetail(String username) {
        UserAssetDetailsEntity userAsset = userDAO.fetchUserAssetDetail(username);

        return UserAssetDetailsDTO.newBuilder().setLedgerId(userAsset.getLedgerId())
            .setUsername(userAsset.getUsername())
            .setGoldBalance(userAsset.getGoldBalance())
            .setAmountBalance(userAsset.getAmountBalance())
            .build();
    }

    @Override
    public TransactionDetailsListDTO fetchUserTransactionDetails(String username) {
        TransactionDetailsListEntity transactionDetailsResponse = userDAO.fetchUserTransactionDetails(username);
        List<TransactionDetailsEntity> userTransactionsListEntity = transactionDetailsResponse.getTransactionsList();

        List<TransactionDetailsDTO> userTransactionsListDto = new ArrayList<>();

        userTransactionsListEntity.forEach(transaction -> {
            TransactionDetailsDTO eachTransactionDetail = TransactionDetailsDTO.newBuilder()
                .setTransactionId(transaction.getTransactionId())
                .setTransactionTime(transaction.getTransactionTime())
                .setGoldBalance(transaction.getGoldBalance())
                .setAmountBalance(transaction.getAmountBalance())
                .setMode(transaction.getMode())
                .setStatus(transaction.getStatus())
                .build();

            userTransactionsListDto.add(eachTransactionDetail);
        });
        return TransactionDetailsListDTO.newBuilder().addAllTransactions(userTransactionsListDto).build();
    }
}
