// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.DynamoDBv2.DataModel;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PamServices;

/// <summary>
/// Label object.
/// </summary>
[DynamoDBTable("Label")]
public class Label
{
    [DynamoDBProperty("Label")]
    public string LabelID { get; set; }


    [DynamoDBProperty("images")]
    public List<string> Images { get; set; }
}