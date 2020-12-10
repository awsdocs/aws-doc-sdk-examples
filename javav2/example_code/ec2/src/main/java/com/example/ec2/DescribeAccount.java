//snippet-sourcedescription:[DescribeAccount.java demonstrates how to get information about the Amazon Elastic Compute Cloud (Amazon EC2) account.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon EC2]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/01/2020]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.ec2;

// snippet-start:[ec2.java2.describe_account.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.AccountAttribute;
import software.amazon.awssdk.services.ec2.model.AccountAttributeValue;
import software.amazon.awssdk.services.ec2.model.DescribeAccountAttributesResponse;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
import java.util.List;
import java.util.ListIterator;
// snippet-end:[ec2.java2.describe_account.import]

public class DescribeAccount {

    public static void main(String[] args) {

        //Create an Ec2Client object
        Region region = Region.US_WEST_2;
        Ec2Client ec2 = Ec2Client.builder()
                .region(region)
                .build();

        describeEC2Account(ec2);
        System.out.print("Done");
        ec2.close();
     }

     // snippet-start:[ec2.java2.describe_account.main]
     public static void describeEC2Account(Ec2Client ec2) {

        try{
            DescribeAccountAttributesResponse accountResults = ec2.describeAccountAttributes();
            List<AccountAttribute> accountList = accountResults.accountAttributes();

            for (ListIterator iter = accountList.listIterator(); iter.hasNext(); ) {

                AccountAttribute attribute = (AccountAttribute) iter.next();
                System.out.print("\n The name of the attribute is "+attribute.attributeName());
                List<AccountAttributeValue> values = attribute.attributeValues();

                for (ListIterator iterVals = values.listIterator(); iterVals.hasNext(); ) {
                    AccountAttributeValue myValue = (AccountAttributeValue) iterVals.next();
                    System.out.print("\n The value of the attribute is "+myValue.attributeValue());
                }
            }

        } catch (Ec2Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        // snippet-end:[ec2.java2.describe_account.main]
    }
}
