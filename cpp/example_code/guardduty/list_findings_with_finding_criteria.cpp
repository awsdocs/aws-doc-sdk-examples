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
#include <aws/core/utils/Outcome.h>
#include <aws/guardduty/GuardDutyClient.h>
#include <aws/guardduty/model/ListFindingsRequest.h>
#include <aws/guardduty/model/ListFindingsResult.h>
#include <aws/guardduty/model/FindingCriteria.h>
#include <aws/guardduty/model/Condition.h>
#include <iostream>

/*
 * List all GuardDuty Findings that match the specified FindingCriteria.
 */

int main(int argc, char ** argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: list_findings_with_finding_criteria <detector_id> <condition_val>"
      "<criteria_val>" << std::endl;
    return 1;
  }

  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String detector_id(argv[1]);
    Aws::String condition_val(argv[2]);
    Aws::String criteria_val(argv[3]);

    Aws::GuardDuty::Model::Condition condition;
    Aws::GuardDuty::GuardDutyClient gd;
    Aws::GuardDuty::Model::ListFindingsRequest lffc_req;
    Aws::GuardDuty::Model::FindingCriteria finding_criteria;

    condition.AddEq(condition_val);
    finding_criteria.AddCriterion(criteria_val, condition);
    lffc_req.SetDetectorId(detector_id);
    lffc_req.SetFindingCriteria(finding_criteria);
    lffc_req.SetMaxResults(10);

    auto lffc_out = gd.ListFindings(lffc_req);

    if (lffc_out.IsSuccess())
    {
      std::cout << "Successfully listing the findings";
    }
    else
    {
      std::cout << "Error listing the findings " << lffc_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
