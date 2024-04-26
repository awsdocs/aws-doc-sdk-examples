// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include <aws/core/Aws.h>
#include <aws/storagegateway/StorageGatewayClient.h>
#include <aws/storagegateway/model/ListFileSharesRequest.h>
#include <aws/storagegateway/model/ListFileSharesResult.h>
#include <aws/core/utils/Outcome.h>
#include <iostream>

int main(int argc, char **argv) {
    if (argc != 2) {
        std::cout << "Usage: list_file_shares <gateway_arn>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String gateway_arn(argv[1]);

        Aws::StorageGateway::StorageGatewayClient storagegateway;

        Aws::StorageGateway::Model::ListFileSharesRequest lfs_req;

        lfs_req.SetGatewayARN(gateway_arn);

        Aws::String marker; // Used for pagination.
        Aws::Vector<Aws::StorageGateway::Model::FileShareInfo> all_file_shares;

        do {
            if (!marker.empty()) {
                lfs_req.SetMarker(marker);
            }
            auto lfs_out = storagegateway.ListFileShares(lfs_req);

            if (lfs_out.IsSuccess()) {
                auto &file_shares = lfs_out.GetResult().GetFileShareInfoList();
                all_file_shares.insert(all_file_shares.end(), file_shares.begin(),
                                       file_shares.end());
                marker = lfs_out.GetResult().GetMarker();
            }
            else {
                std::cout << "Error listing File share"
                          << lfs_out.GetError().GetMessage()
                          << std::endl;
            }

        } while (!marker.empty());

        std::cout << all_file_shares.size() << " file share(s) found" << std::endl;

        for (auto const &file_share: all_file_shares) {
            std::cout << " " << file_share.GetFileShareId() << std::endl;
        }
    }

    Aws::ShutdownAPI(options);
    return 0;
}
