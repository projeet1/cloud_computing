package uk.ac.ed.inf.acpCSW.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
public class S3Config {
    @Bean
    public S3Client s3Client(AcpConfig acpConfig) {
        return S3Client.builder()
                .endpointOverride(URI.create(acpConfig.getS3Endpoint()))
                .forcePathStyle(true)   // match tutor exactly
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create("test", "test")
                        )
                )
                .region(Region.US_EAST_1)
                .build();
    }

//    @Bean
//    public S3Client s3Client(AcpConfig acpConfig) {
//        return S3Client.builder()
//                .endpointOverride(URI.create(acpConfig.getS3Endpoint()))
//                .credentialsProvider(
//                        StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test"))
//                )
//                .region(Region.US_EAST_1)
//                // LocalStack needs path-style addressing
//                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
//                .build();
//    }
}
