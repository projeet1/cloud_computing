package uk.ac.ed.inf.acpCSW.configuration;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
@Configuration
public class AcpConfig {

    @Value("${acp.postgres}")
    private String postgres;

    @Value("${acp.s3}")
    private String s3Endpoint;

    @Value("${acp.dynamodb}")
    private String dynamoEndpoint;

    @Value("${acp.ilp}")
    private String ilpEndpoint;

    @PostConstruct
    public void logConfig() {
        System.out.println("acp.postgres = " + postgres);
        System.out.println("acp.s3 = " + s3Endpoint);
        System.out.println("acp.dynamodb = " + dynamoEndpoint);
        System.out.println("acp.ilp = " + ilpEndpoint);
    }

    public String getPostgres() { return postgres; }
    public String getS3Endpoint() { return s3Endpoint; }
    public String getDynamoEndpoint() { return dynamoEndpoint; }
    public String getIlpEndpoint() { return ilpEndpoint; }
}
