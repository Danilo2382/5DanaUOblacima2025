package com.danilocicvaric.canteen_system.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.textract.TextractClient;


@Configuration
public class AwsConfig {

    private final Region awsRegion = Region.EU_CENTRAL_1;

    @Bean
    public SqsClient sqsClient() {
        return SqsClient.builder().region(awsRegion).build();
    }

    @Bean
    public TextractClient textractClient() {
        return TextractClient.builder().region(awsRegion).build();
    }
}
