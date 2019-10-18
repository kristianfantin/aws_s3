package com.kristian.domain.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.kristian.controller.dto.BucketS3;
import com.kristian.controller.dto.ParamsIn;
import com.kristian.controller.dto.S3KeyDTO;
import com.kristian.domain.dto.S3Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AWSS3 {

    private static final Logger LOGGER = LoggerFactory.getLogger(AWSS3.class);
    private AmazonS3 s3;
    private S3KeyDTO credentialProperties;


    public List<String> getFileNames(ParamsIn paramsIn, BucketS3 bucket, LocalDate date) {
        AWSS3 awss3 = setData(paramsIn.getS3KeyDTO());
        return awss3
                .list(bucket.getBucket(), bucket.getFolder(), date)
                .stream()
                .map(S3Template::getFile)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    S3Object download(ParamsIn paramsIn, String bucket, String source) {
        setData(paramsIn.getS3KeyDTO());
        return s3.getObject(new GetObjectRequest(bucket, source));
    }

    private AWSS3 setData(S3KeyDTO credentialProperties) {
        this.credentialProperties = credentialProperties;
        this.s3 = buildS3Instance();
        return this;
    }

    private AmazonS3 buildS3Instance() {
        return AmazonS3ClientBuilder.standard()
                .withCredentials(getCredentialsProvider())
                .withRegion(Regions.US_EAST_1)
                .build();
    }

    private AWSCredentialsProvider getCredentialsProvider() {
        return new AWSCredentialsProvider() {
            @Override
            public AWSCredentials getCredentials() {
                return new AWSCredentials() {
                    @Override
                    public String getAWSAccessKeyId() {
                        return credentialProperties.getAccessKeyId();
                    }

                    @Override
                    public String getAWSSecretKey() {
                        return credentialProperties.getSecretAccessKey();
                    }
                };
            }

            @Override
            public void refresh() { }
        };
    }

    private List<S3Template> list(String bucket, String prefix, LocalDate date) {
        List<S3Template> list = new ArrayList<>();

        ListObjectsV2Request req = new ListObjectsV2Request()
                .withBucketName(bucket)
                .withPrefix(prefix);

        try {
            ListObjectsV2Result result;

            do {
                result = s3.listObjectsV2(req);

                list.addAll(result.getObjectSummaries()
                        .stream()
                        .map(this::getS3Template)
                        .filter(t -> {
                            LocalDate dataModified = t.getDataCreated().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            return dataModified.isAfter(date);
                        })
                        .collect(Collectors.toList()));

                String token = result.getNextContinuationToken();
                req.setContinuationToken(token);
            } while (result.isTruncated());

        }
        catch (AmazonServiceException e) {
            LOGGER.error("AmazonServiceException: " + e.getMessage());
            e.printStackTrace();
        } catch (SdkClientException e) {
            LOGGER.error("SdkClientException: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    private S3Template getS3Template(S3ObjectSummary objectSummary) {
        S3Template template = new S3Template();
        template.setFile(objectSummary.getKey());
        template.setDataCreated(objectSummary.getLastModified());
        return template;
    }

}
