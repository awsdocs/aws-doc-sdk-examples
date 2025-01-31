// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include <aws/core/Aws.h>
#include <aws/ec2/EC2Client.h>
#include <aws/ec2/model/DescribeVolumesRequest.h>
#include <aws/ec2/model/DescribeVolumesResponse.h>
#include <iostream>

int main(int argc, char **argv) {
    if (argc != 1) {
        std::cout << "Usage: describe_volumes" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::EC2::EC2Client ec2;

        Aws::EC2::Model::DescribeVolumesRequest dv_req;

        Aws::Vector<Aws::EC2::Model::Volume> all_volumes;
        Aws::String next_token;

        do {
            if (!next_token.empty()) {
                dv_req.SetNextToken(next_token);
            }
            auto dv_out = ec2.DescribeVolumes(dv_req);

            if (dv_out.IsSuccess()) {
                const auto &volumes = dv_out.GetResult().GetVolumes();
                all_volumes.insert(all_volumes.end(), volumes.begin(), volumes.end());

                next_token = dv_out.GetResult().GetNextToken();
            }
            else {
                std::cout << "Error describing volumes"
                          << dv_out.GetError().GetMessage()
                          << std::endl;
                break;
            }
        } while (!next_token.empty());

        std::cout << all_volumes.size() << " volume(s) found:" << std::endl;
        for (auto val: all_volumes) {
            std::cout << " " << val.GetVolumeId() << std::endl;
        }
        std::cout << std::endl;
    }

    Aws::ShutdownAPI(options);
    return 0;
}
