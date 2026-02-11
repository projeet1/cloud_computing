package uk.ac.ed.inf.acpCSW.configuration;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AcpConfig {
    @Value("${ACP_POSTGRES:jdbc:postgresql://localhost:5432/acp}")
    private String postgres;

    @Value("${ACP_S3:http://localhost:4566}")
    private String s3Endpoint;

    @Value("${ACP_DYNAMODB:http://localhost:4566}")
    private String dynamoEndpoint;

    // prefer ACP_URL_ENDPOINT if present, otherwise ACP_ILP_ENDPOINT, otherwise default
    @Value("${ACP_URL_ENDPOINT:${ACP_ILP_ENDPOINT:https://ilp-rest-2025-bvh6e9hschfagrgy.ukwest-01.azurewebsites.net}}")
    private String ilpEndpoint;


    @PostConstruct
    public void logConfig() {
        System.out.println("ACP_POSTGRES = " + postgres);
        System.out.println("ACP_S3 = " + s3Endpoint);
        System.out.println("ACP_DYNAMODB = " + dynamoEndpoint);
        System.out.println("ACP_ILP_ENDPOINT = " + ilpEndpoint);
    }

    public String getPostgres() {
        return postgres;
    }

    public String getS3Endpoint() {
        return s3Endpoint;
    }

    public String getDynamoEndpoint() {
        return dynamoEndpoint;
    }

    public String getIlpEndpoint() {
        return ilpEndpoint;
    }
}
