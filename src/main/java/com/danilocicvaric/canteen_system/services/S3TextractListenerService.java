package com.danilocicvaric.canteen_system.services;

import com.danilocicvaric.canteen_system.dtos.StudentDtos.CreateStudentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
public class S3TextractListenerService {

    private final TextractProcessingService textractService;
    private final IStudentService studentService;
    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${sqs.queue.url}")
    private String queueUrl;

    @Scheduled(fixedDelay = 5000)
    public void pollSqsQueue() {
        ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .waitTimeSeconds(5)
                .visibilityTimeout(60)
                .build();

        try {
            ReceiveMessageResponse receiveResponse = sqsClient.receiveMessage(receiveRequest);
            List<Message> messages = receiveResponse.messages();

            if (!messages.isEmpty())
                log.info("Received {} messages from SQS queue. Running...", messages.size());

            for (Message message : messages) {
                try {
                    processMessage(message);

                    deleteMessage(message);

                } catch (Exception e) {
                    log.error("Fatal error while processing message: {}. Message stays in queue", message.messageId(), e);
                }
            }
        } catch (SqsException e) {
            log.error("Error while fetching messages from SQS-a. Details: {}", e.getMessage());
        }
    }

    private void processMessage(Message message) throws Exception {
        JsonNode root = objectMapper.readTree(message.body());
        JsonNode s3EventNode;

        JsonNode messageNode = root.path("Message");
        if (messageNode.isTextual()) {
            s3EventNode = objectMapper.readTree(messageNode.asText());
            log.debug("SNS omotač pronađen i parsiran.");
        } else
            s3EventNode = root;

        JsonNode recordsNode = s3EventNode.path("Records");
        if (!recordsNode.isArray() || recordsNode.isEmpty())
            return;

        JsonNode s3Node = recordsNode.get(0).path("s3");

        String bucketName = s3Node.path("bucket").path("name").asText();
        String objectKey = s3Node.path("object").path("key").asText();

        if (bucketName.isEmpty() || objectKey.isEmpty())
            return;

        String decodedObjectKey = java.net.URLDecoder.decode(objectKey, StandardCharsets.UTF_8);

        CreateStudentRequest studentRequest = textractService.processDocument(bucketName, decodedObjectKey);

        studentService.update(studentRequest);

        textractService.processDocument(bucketName, decodedObjectKey);
    }

    private void deleteMessage(Message message) {
        DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(message.receiptHandle())
                .build();
        sqsClient.deleteMessage(deleteRequest);
    }
}