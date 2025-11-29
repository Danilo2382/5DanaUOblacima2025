package com.danilocicvaric.canteen_system.services;

import com.danilocicvaric.canteen_system.dtos.StudentDtos.CreateStudentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.*;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TextractProcessingService {

    private final TextractClient textractClient;

    public CreateStudentRequest processDocument(String bucketName, String objectKey) {
        try {
            Document document = Document.builder()
                    .s3Object(software.amazon.awssdk.services.textract.model.S3Object.builder()
                            .bucket(bucketName)
                            .name(objectKey)
                            .build())
                    .build();

            AnalyzeDocumentRequest analyzeDocumentRequest = AnalyzeDocumentRequest.builder()
                    .document(document)
                    .featureTypes(FeatureType.FORMS)
                    .build();

            AnalyzeDocumentResponse response = textractClient.analyzeDocument(analyzeDocumentRequest);
            Map<String, String> extractedData = extractKeyValuePairs(response);

            return mapToCreateStudentRequest(extractedData);

        } catch (TextractException e) {
            throw new RuntimeException("Textract failed: " + e.getMessage());
        }
    }

    private Map<String, String> extractKeyValuePairs(AnalyzeDocumentResponse response) {
        Map<String, Block> blockMap = new HashMap<>();
        for (Block block : response.blocks())
            blockMap.put(block.id(), block);

        Map<String, String> extractedData = new HashMap<>();

        for (Block block : response.blocks()) {
            if (block.blockType() == BlockType.KEY_VALUE_SET && block.entityTypes().contains(EntityType.KEY)) {

                String key = getText(block, blockMap);
                String value = "";

                if (block.relationships() != null) {
                    for (Relationship relationship : block.relationships()) {
                        if (relationship.type() == RelationshipType.VALUE) {
                            for (String valueId : relationship.ids()) {
                                Block valueBlock = blockMap.get(valueId);
                                if (valueBlock != null && valueBlock.entityTypes().contains(EntityType.VALUE)) {
                                    value = getText(valueBlock, blockMap);
                                    break;
                                }
                            }
                        }
                    }
                }

                if (!key.isEmpty() && !value.isEmpty()) {
                    String cleanKey = key.trim().toLowerCase();

                    if (cleanKey.contains("name") || cleanKey.contains("ime"))
                        extractedData.put("Name", value);
                    else if (cleanKey.contains("email"))
                        extractedData.put("Email", value);
                    else if (cleanKey.contains("index"))
                        extractedData.put("IndexNumber", value);
                }
            }
        }

        extractedData.putIfAbsent("Name", "Unknown student");
        extractedData.putIfAbsent("Email", "unkown@example.com");
        extractedData.putIfAbsent("IndexNumber", "000000");
        extractedData.put("IsAdmin", "false");

        return extractedData;
    }

    private String getText(Block block, Map<String, Block> blockMap) {
        StringBuilder text = new StringBuilder();
        if (block.relationships() != null) {
            for (Relationship relationship : block.relationships()) {
                if (relationship.type() == RelationshipType.CHILD) {
                    for (String childId : relationship.ids()) {
                        Block childBlock = blockMap.get(childId);
                        if (childBlock != null && childBlock.text() != null)
                            text.append(childBlock.text()).append(" ");
                    }
                }
            }
        }
        return text.toString().trim();
    }

    private CreateStudentRequest mapToCreateStudentRequest(Map<String, String> extractedData) {
        String name = extractedData.getOrDefault("Name", "N/A");
        String email = extractedData.getOrDefault("Email", "placeholder@example.com");
        String indexNumber = extractedData.getOrDefault("IndexNumber", "000000");
        boolean isAdmin = Boolean.parseBoolean(extractedData.getOrDefault("IsAdmin", "false"));

        return new CreateStudentRequest(name, email, indexNumber, isAdmin);
    }
}
