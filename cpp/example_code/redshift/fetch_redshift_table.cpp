 
//snippet-sourcedescription:[fetch_redshift_table.cpp demonstrates how to retrieve data from an Amazon Redshift table. ]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Redshift]
//snippet-service:[redshift]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[tapasweni-pathak]


/*
Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at

http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.
*/
#include <aws/core/Aws.h>
#include <aws/redshift/RedshiftClient.h>
#include <aws/redshift/model/CreateClusterRequest.h>
#include <aws/redshift/model/CreateClusterResult.h>
#include <iostream>
#include <SQLAPI.h>

/**
 * Fetch from RedShift table based on command line input
 */

int main(int argc, char **argv)
{
  if (argc != 6)
  {
    std::cout << "Usage: fetch_redshift_table <db_name> <cluster_identifier>"
                 "<username> <password> <nodetype>";
    return 1;
  }
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String db_name(argv[1]);
    Aws::String cluster_identifier(argv[2]);
    Aws::String username(argv[3]);
    Aws::String password(argv[4]);
    Aws::String nodetype(argv[5]);

    Aws::Redshift::RedshiftClient redshift;

    Aws::Redshift::Model::CreateClusterRequest cc_req;

    cc_req.SetDBName(db_name);
    cc_req.SetClusterIdentifier(cluster_identifier);
    cc_req.SetMasterUsername(username);
    cc_req.SetMasterUserPassword(password);
    cc_req.SetNodeType(nodetype);

    auto cc_out = ses.CreateCluster(cc_req);

    if (cc_out.IsSuccess())
    {
      std::cout << "Successfully created cluster." << std::endl;
    }

    else
    {
      std::cout << "Error creating cluster." << cc_out.GetError().GetMessage()
        << std::endl;
      return 1;
    }

    SAConnection connection;
    SACommand command;
    try
    {
      connection.Connect(db_name, username, password, SA_Redshift_Client);
      command.setCommandText("Select * from db_name");
      command.Execute();
    }
    catch (SAException &e)
    {
      std::cout << reinterpret_cast<const char *>e.ErrText() >> std::endl;
    }

  }

  Aws::ShutdownAPI(options);
  return 0;
}
