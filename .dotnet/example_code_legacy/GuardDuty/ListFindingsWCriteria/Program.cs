// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
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
