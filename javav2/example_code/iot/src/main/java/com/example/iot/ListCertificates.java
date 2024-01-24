package com.example.iot;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.Certificate;
import software.amazon.awssdk.services.iot.model.ListCertificatesResponse;

import java.util.List;

public class ListCertificates {
    public static void main(String[] args) {
        System.out.println("List your IoT Certificates");

        IotClient iotClient = IotClient.builder()
            .region(Region.US_EAST_1)
            .build();

        ListCertificatesResponse response = iotClient.listCertificates();
        List<Certificate> certList = response.certificates();
        for (Certificate cert : certList) {
            System.out.println("Cert id: " + cert.certificateId());
            System.out.println("Cert Arn: " + cert.certificateArn());
        }
    }
    }
