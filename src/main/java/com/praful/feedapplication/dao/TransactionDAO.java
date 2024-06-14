package com.praful.feedapplication.dao;

import com.praful.feedapplication.protos.TransactionAddRequestEntity;
import com.praful.feedapplication.protos.TransactionDetailsEntity;
import com.praful.feedapplication.protos.TransactionDetailsListEntity;
import com.praful.feedapplication.protos.TransactionUpdateRequestEntity;

public interface TransactionDAO {
    int updateTransactionStatus(TransactionUpdateRequestEntity updatedTransaction);

    TransactionDetailsEntity addTransaction(TransactionAddRequestEntity newTransaction);

    TransactionDetailsEntity fetchTransactionDetailById(String transactionId);

   TransactionDetailsListEntity fetchTransactionDetailsBasedOnStatus(String transactionStatus);
}
