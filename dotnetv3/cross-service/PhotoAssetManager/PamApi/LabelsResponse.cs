// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using PamServices;

namespace PamApi;

public class LabelsResponse
{
    public Dictionary<string, LabelCount> Labels { get; set; }

    public LabelsResponse(List<Label> labelsList)
    {
        Labels = new Dictionary<string, LabelCount>();
        labelsList.ForEach(l => 
            Labels.Add(l.LabelID, new LabelCount(){Count = l.Images.Count}));
    }
}

/// <summary>
/// Count object for a label.
/// </summary>
public class LabelCount
{
    public int Count { get; set; }
}