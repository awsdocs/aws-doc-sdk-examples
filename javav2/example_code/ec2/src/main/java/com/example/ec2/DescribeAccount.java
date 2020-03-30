//snippet-sourcedescription:[DescribeAccount.java demonstrates how to get information about the AWS account.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[ec2]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/02/2020]
//snippet-sourceauthor:[scmacdon]
/*
 * Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.example.ec2;
// snippet-start:[ec2.java2.describe_account.complete]

// snippet-start:[ec2.java2.describe_account.import]
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

        // snippet-start:[ec2.java2.describe_account.main]
        Ec2Client ec2 = Ec2Client.create();

        try{
            DescribeAccountAttributesResponse accountResults = ec2.describeAccountAttributes();

            List<AccountAttribute> accountList = accountResults.accountAttributes();

            for (ListIterator iter = accountList.listIterator(); iter.hasNext(); ) {

                AccountAttribute attribute = (AccountAttribute) iter.next();
                System.out.print("\n The name of the attribute is "+attribute.attributeName());
                List<AccountAttributeValue> values = attribute.attributeValues();

                //iterate through the attribute values
                for (ListIterator iterVals = values.listIterator(); iterVals.hasNext(); ) {
                    AccountAttributeValue myValue = (AccountAttributeValue) iterVals.next();
                    System.out.print("\n The value of the attribute is "+myValue.attributeValue());
                }
            }
            System.out.print("Done");

        } catch (Ec2Exception e) {
            e.getStackTrace();
        }
        // snippet-end:[ec2.java2.describe_account.main]
    }
}
// snippet-end:[ec2.java2.describe_account.complete]
