/*
   Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
#include <aws/core/Aws.h>

#include <aws/ec2/EC2Client.h>
#include <aws/ec2/model/DescribeInstancesRequest.h>
#include <aws/ec2/model/DescribeInstancesResponse.h>

#include <iostream>



/**
 * Describes all ec2 instances associated with an account
 */
int main(int argc, char** argv)
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);

    {
        Aws::EC2::EC2Client ec2_client;

        Aws::EC2::Model::DescribeInstancesRequest describeInstancesRequest;

        bool header = false;
        bool done = false;
        while (!done)
        {
            auto describeInstancesOutcome = ec2_client.DescribeInstances(describeInstancesRequest);
            if (describeInstancesOutcome.IsSuccess())
            {
                if (!header)
                {
                    std::cout << std::left << std::setw(48) << "Name"
                    << std::setw(20) << "ID"
                    << std::setw(15) << "Ami"
                    << std::setw(15) << "Type"
                    << std::setw(15) << "State"
                    << std::setw(15) << "Monitoring" << std::endl;
                    header = true;
                }

                const auto &reservations = describeInstancesOutcome.GetResult().GetReservations();
                for (const auto &reservation : reservations)
                {
                    const auto &instances = reservation.GetInstances();
                    for (const auto &instance : instances)
                    {
                        Aws::String instanceStateString = Aws::EC2::Model::InstanceStateNameMapper::GetNameForInstanceStateName(
                                instance.GetState().GetName());
                        Aws::String typeString = Aws::EC2::Model::InstanceTypeMapper::GetNameForInstanceType(
                                instance.GetInstanceType());
                        Aws::String monitoringString = Aws::EC2::Model::MonitoringStateMapper::GetNameForMonitoringState(
                                instance.GetMonitoring().GetState());
                        Aws::String nameString = "Unknown";

                        const auto &tags = instance.GetTags();
                        auto nameIter = std::find_if(tags.cbegin(), tags.cend(), [](const Aws::EC2::Model::Tag &tag)
                            { return tag.GetKey() == "Name"; });
                        if (nameIter != tags.cend())
                        {
                            nameString = nameIter->GetValue();
                        }
                        std::cout << std::setw(48) << nameString
                        << std::setw(20) << instance.GetInstanceId()
                        << std::setw(15) << instance.GetImageId()
                        << std::setw(15) << typeString
                        << std::setw(15) << instanceStateString
                        << std::setw(15) << monitoringString << std::endl;
                    }
                }

                if (describeInstancesOutcome.GetResult().GetNextToken().size() > 0)
                {
                    describeInstancesRequest.SetNextToken(describeInstancesOutcome.GetResult().GetNextToken());
                }
                else
                {
                    done = true;
                }
            }
            else
            {
                std::cout << "Failed to describe ec2 instances:" << describeInstancesOutcome.GetError().GetMessage() <<
                std::endl;
                done = true;
            }
        }
    }

    Aws::ShutdownAPI(options);

    return 0;
}



