/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

using System;

namespace WebApplicationDynamoDB.Controllers
{
    public class WorkItem
    {
        public string Id { get; set; }

        public string Name { get; set; }

        public string Guide { get; set; }

        public string Date { get; set; }

        public string Description { get; set; }

        public string Status { get; set; }
    }
}
