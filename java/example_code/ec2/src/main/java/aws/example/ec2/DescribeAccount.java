// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

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

        try {
            DescribeAccountAttributesResult accountResults = ec2.describeAccountAttributes();
            List<AccountAttribute> accountList = accountResults.getAccountAttributes();

            for (ListIterator iter = accountList.listIterator(); iter.hasNext();) {

                AccountAttribute attribute = (AccountAttribute) iter.next();
                System.out.print("\n The name of the attribute is " + attribute.getAttributeName());
                List<AccountAttributeValue> values = attribute.getAttributeValues();

                // iterate through the attribute values
                for (ListIterator iterVals = values.listIterator(); iterVals.hasNext();) {
                    AccountAttributeValue myValue = (AccountAttributeValue) iterVals.next();
                    System.out.print("\n The value of the attribute is " + myValue.getAttributeValue());
                }
            }
            System.out.print("Done");
        } catch (Exception e) {
            e.getStackTrace();
        }
        // snippet-end:[ec2.java1.describe_account.main]
    }
}
// snippet-end:[ec2.java1.describe_account.complete]
