package com.praful.feedapplication.utils;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.praful.feedapplication.exception.SqsMessageException;
import com.praful.feedapplication.protos.SqsMessage;
import org.springframework.stereotype.Component;

@Component
public class SqsUtils {
    private final AmazonSQS sqs;
    private final String endpoint;

    public SqsUtils(AmazonSQS sqs) {
        this.sqs = sqs;
        this.endpoint = System.getenv("SqsUri");
    }

    public void sendMsg(SqsMessage sqsMessage) throws InvalidProtocolBufferException {
        String messageBody = JsonFormat.printer().print(sqsMessage);
        SendMessageRequest sendMessageRequest = new SendMessageRequest()
                .withQueueUrl(endpoint)
                .withMessageBody(messageBody);
        sqs.sendMessage(sendMessageRequest);
    }

    public List<SqsMessage> receiveMsg() {

        List<Message> messages = sqs.receiveMessage(endpoint).getMessages();

        List<SqsMessage> sqsMessageList = new ArrayList<>();

            for (Message m : messages) {
                SqsMessage.Builder sqsMessage = SqsMessage.newBuilder();
                try {
                    JsonFormat.parser().ignoringUnknownFields().merge(m.getBody(), sqsMessage);
                    sqsMessageList.add(sqsMessage.build());
                } catch (Exception e) {
                    throw new SqsMessageException("Error converting the sqs message to proto");
                }
                sqs.deleteMessage(endpoint, m.getReceiptHandle());
            }
        return sqsMessageList;
    }

}
