package com.praful.feedapplication.controller;

import com.praful.feedapplication.protos.AuthRequestDTO;
import com.praful.feedapplication.protos.AuthResponseDTO;
import com.praful.feedapplication.protos.TransactionDetailsListDTO;
import com.praful.feedapplication.protos.UserAssetDetailsDTO;
import com.praful.feedapplication.protos.UserRequestDTO;
import com.praful.feedapplication.protos.UserResponseDTO;
import com.praful.feedapplication.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssetController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDTO> userSignup(@RequestBody UserRequestDTO userRequestDTO) {
        LOGGER.info("Request for signup - {}", userRequestDTO);
            return new ResponseEntity<>(userService.addUser(userRequestDTO), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> userLogin(@RequestBody AuthRequestDTO authRequestDTO) {
        LOGGER.info("Request for login for user {}", authRequestDTO.getUsername());
            return new ResponseEntity<>(userService.authenticateUser(authRequestDTO), HttpStatus.OK);
    }

    @GetMapping("/asset-details")
    public ResponseEntity<UserAssetDetailsDTO> fetchUserAssetDetails() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        LOGGER.info("request for fetching asset details by user {}", username);
        return new ResponseEntity<>(userService.fetchUserAssetDetail(username), HttpStatus.OK);
    }

    @GetMapping("/transaction-details")
    public ResponseEntity<TransactionDetailsListDTO> fetchUserTransactionDetails() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        LOGGER.info("request for fetching all the transaction by user {}", username);
        return new ResponseEntity<>(userService.fetchUserTransactionDetails(username), HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<UserResponseDTO> deleteUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        LOGGER.warn("Request for deleting user {}", username);
        return new ResponseEntity<>(userService.removeUser(username), HttpStatus.OK);
    }
}
