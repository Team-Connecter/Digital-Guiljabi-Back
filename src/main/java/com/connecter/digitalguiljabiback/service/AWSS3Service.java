package com.connecter.digitalguiljabiback.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class AWSS3Service {

    private final AmazonS3 s3;
    private final String bucketName = "digital-guiljabi";

    @Value("${aws.s3.accessKey}")
    private String accessKey ;

    @Value("${aws.s3.secretKey}")
    private String secretKey;

    public AWSS3Service() {
        try{
        final String endPoint = "https://kr.object.ncloudstorage.com";
        final String regionName = "kr-standard";

        // S3 client initialization
        s3 = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, regionName))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize AWSS3Service", e);
        }
    }

    public void uploadFileToFolder(String objectName, InputStream filePath) {

        String folderName = "sample/";
        // Upload local file to the specified folder
        String key = folderName + objectName;

        try {
//            s3.putObject(bucketName, key, new File(filePath));
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/jpeg"); // 이미지 타입에 맞게 설정

            s3.putObject(bucketName, key, filePath, metadata);
            System.out.format("Object %s has been uploaded to folder %s.\n", objectName, folderName);

            // Set bucket ACL
            AccessControlList bucketAcl = s3.getBucketAcl(bucketName);
            bucketAcl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
            s3.setBucketAcl(bucketName, bucketAcl);

            // Set object ACL
            AccessControlList objectAcl = s3.getObjectAcl(bucketName, key);
            objectAcl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
            s3.setObjectAcl(bucketName, key, objectAcl);


        } catch (AmazonS3Exception e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }
    }
}
