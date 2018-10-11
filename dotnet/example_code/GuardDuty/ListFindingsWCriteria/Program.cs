 
//snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
//snippet-keyword:[dotnet]
//snippet-keyword:[.NET]
//snippet-keyword:[Code Sample]
//snippet-service:[<<ADD SERVICE>>]
//snippet-sourcetype:[<<snippet or full-example>>]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


/*******************************************************************************
* Copyright 2009-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License"). You may
* not use this file except in compliance with the License. A copy of the
* License is located at
*
* http://aws.amazon.com/apache2.0/
*
* or in the "license" file accompanying this file. This file is
* distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the specific
* language governing permissions and limitations under the License.
*******************************************************************************/
using System;
using System.Text;
using System.Threading.Tasks;
using Amazon;
using Amazon.GuardDuty;
using Amazon.GuardDuty.Model;

namespace ListFindingsWCriteria
{
    class Program
    {
        static void Main(string[] args)
        {
            String detectorId = "cdc02b15f9f520a8882c959g3e95c24b";
            
            FindingCriteria criteria = new FindingCriteria();
            
            Condition condition = new Condition();
            condition.Eq.Add("Recon:EC2/PortProbeUnprotectedPort");
            condition.Eq.Add("Recon:EC2/Portscan");
            
            criteria.Criterion.Add("type", condition);
            
            using(var gdClient = new AmazonGuardDutyClient(RegionEndpoint.USWest2))
            {

                var request = new ListFindingsRequest
                {
                    DetectorId = detectorId,
                    FindingCriteria = criteria,
                };
    
                Task<ListFindingsResponse> response = gdClient.ListFindingsAsync(request);
                response.Wait();

                foreach (String findingId in response.Result.FindingIds)
                {
                    Console.WriteLine(findingId.ToString());
                }
            }
        }

    }
}
