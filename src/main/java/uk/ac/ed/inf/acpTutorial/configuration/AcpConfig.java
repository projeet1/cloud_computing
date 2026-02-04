package uk.ac.ed.inf.acpTutorial.configuration;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AcpConfig {

    @Value("${ACP_POSTGRES}")
    private String postgres;

    @Value("${ACP_S3}")
    private String s3Endpoint;

    @Value("${ACP_DYNAMODB}")
    private String dynamoEndpoint;

    @Value("${ACP_ILP_ENDPOINT}")
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
