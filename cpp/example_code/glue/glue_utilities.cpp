/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


#include <aws/cloudformation/CloudFormationClient.h>
#include <aws/cloudformation/model/CreateStackRequest.h>
#include <aws/cloudformation/model/DeleteStackRequest.h>
#include <aws/cloudformation/model/DescribeStacksRequest.h>
#include <aws/iam/IAMClient.h>
#include <aws/iam/Model/GetRoleRequest.h>
#include <fstream>
#include "glue_samples.h"



int AwsDoc::Glue::askQuestionForIntRange(const Aws::String &string, int low,
                                         int high) {
    Aws::String resultString = askQuestion(string, [low, high](
            const Aws::String &string1) -> bool {
            try {
                int number = std::stoi(string1);
                bool result = number >= low && number <= high;
                if (!result) {
                    std::cout << "\nThe number is out of range." << std::endl;
                }
                return result;
            }
            catch (const std::invalid_argument &) {
                std::cout << "\nNot a valid number." << std::endl;
                return false;
            }
    });
    int result = 0;
    try {
        result = std::stoi(resultString);
    }
    catch (const std::invalid_argument &) {
        std::cerr << "DynamoDB::askQuestionForFloatRange string not an int "
                  << resultString << std::endl;
    }

    return result;
}


Aws::String AwsDoc::Glue::askQuestion(const Aws::String &string,
                                      const std::function<bool(
                                              Aws::String)> &test) {
    Aws::String result;
    do {
        std::cout << string;
        std::getline(std::cin, result);
        if (result.empty()) {
            std::cout << "Please enter some text." << std::endl;
        }
        if (!test(result)) {
            result.clear();
        }
    } while (result.empty());

    return result;
}

bool AwsDoc::Glue::getRoleArn(const Aws::String &roleName, Aws::String &roleArn,
                              const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::IAM::IAMClient client(clientConfig);

    Aws::IAM::Model::GetRoleRequest request;
    request.SetRoleName(roleName);

    Aws::IAM::Model::GetRoleOutcome outcome = client.GetRole(request);


    if (outcome.IsSuccess()) {
        std::cout << "Successfully retrieved role." << std::endl;
        roleArn = outcome.GetResult().GetRole().GetArn();
    }
    else {
        std::cerr << "Error retrieving role. " << outcome.GetError().GetMessage()
                  << std::endl;
    }

    return outcome.IsSuccess();
}

