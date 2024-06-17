// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Amazon.Runtime;
using Amazon.Scheduler;

namespace ServiceActions;

public class SchedulerWrapper
{
    private readonly IAmazonScheduler _amazonScheduler;
    public SchedulerWrapper(IAmazonScheduler amazonScheduler)
    {
        _amazonScheduler = amazonScheduler;
    }
}