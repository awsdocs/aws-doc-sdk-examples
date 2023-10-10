// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier:  Apache-2.0

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Amazon.Comprehend;

namespace FsaServices.Models;

/// <summary>
/// The details of the analyzed sentiment.
/// </summary>
public class SentimentDetails
{
    public SentimentType Sentiment { get; set; }
    public string LanguageCode { get; set; }
}