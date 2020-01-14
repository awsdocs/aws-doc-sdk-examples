 
//snippet-sourcedescription:[create_repository.cpp demonstrates how to create an AWS CodeCommit repository.]
//snippet-keyword:[C++]
//snippet-sourcesyntax:[cpp]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS CodeCommit]
//snippet-service:[codecommit]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[tapasweni-pathak]


/*
   Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at
    http://aws.amazon.com/apache2.0/
   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h>
#include <aws/codecommit/CodeCommitClient.h>
#include <aws/codecommit/model/CreateRepositoryRequest.h>
#include <aws/codecommit/model/CreateRepositoryResult.h>
#include <aws/codecommit/model/RepositoryMetadata.h>
#include <iostream>

/**
 * Creates a repository based on command line inputs
 */

int main(int argc, char ** argv)
{
  if (argc != 3)
  {
    std::cout << "Usage: create_repository <repository_name> <repository_description>"
              << std::endl;
    return 1;
  }

  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String repository_name(argv[1]);
    Aws::String repository_description(argv[2]);

    Aws::CodeCommit::CodeCommitClient codecommit;

    Aws::CodeCommit::Model::CreateRepositoryRequest cr_req;

    cr_req.SetRepositoryName(repository_name);
    cr_req.SetRepositoryDescription(repository_description);

    auto cr_out = codecommit.CreateRepository(cr_req);

    if (cr_out.IsSuccess())
    {
      std::cout << "Successfully created repository with repository id:"
                << cr_out.GetResult().GetRepositoryMetadata().GetRepositoryId() << std::endl;
    }
    else
    {
      std::cout << "Error creating repository" << cr_out.GetError().GetMessage()
                << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
