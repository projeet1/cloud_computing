package uk.ac.ed.inf.acpCSW.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@Configuration
public class DynamoConfig {

    @Bean
    public DynamoDbClient dynamoDbClient(AcpConfig acpConfig) {
        return DynamoDbClient.builder()
                .endpointOverride(URI.create(acpConfig.getDynamoEndpoint()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test"))
                )
                .region(Region.US_EAST_1)
                .build();
    }
}
