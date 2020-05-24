//snippet-sourcedescription:[DescribeAccount.java demonstrates how to get information about the AWS account.]
//snippet-keyword:[SDK for Java 1.0]
//snippet-keyword:[Code Sample]
//snippet-service:[ec2]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[scmacdon-aws]
/*
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

package aws.example.ec2;
// snippet-start:[ec2.java1.describe_account.complete]
// snippet-start:[ec2.java1.describe_account.import]
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.AccountAttributeValue;
import com.amazonaws.services.ec2.model.DescribeAccountAttributesResult;
import com.amazonaws.services.ec2.model.AccountAttribute;
import java.util.List;
import java.util.ListIterator;
// snippet-end:[ec2.java1.describe_account.import]


public class DescribeAccount {

    public static void main(String[] args) {

        // snippet-start:[ec2.java1.describe_account.main]
        AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        try{
            DescribeAccountAttributesResult accountResults = ec2.describeAccountAttributes();
            List<AccountAttribute> accountList = accountResults.getAccountAttributes();

            for (ListIterator iter = accountList.listIterator(); iter.hasNext(); ) {

                AccountAttribute attribute = (AccountAttribute) iter.next();
                System.out.print("\n The name of the attribute is "+attribute.getAttributeName());
                List<AccountAttributeValue> values = attribute.getAttributeValues();

                 //iterate through the attribute values
                for (ListIterator iterVals = values.listIterator(); iterVals.hasNext(); ) {
                    AccountAttributeValue myValue = (AccountAttributeValue) iterVals.next();
                    System.out.print("\n The value of the attribute is "+myValue.getAttributeValue());
                }
            }
            System.out.print("Done");
        }
        catch (Exception e)
        {
            e.getStackTrace();
        }
        // snippet-end:[ec2.java1.describe_account.main]
    }
}
// snippet-end:[ec2.java1.describe_account.complete]
