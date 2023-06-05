// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace PamServices;

/// <summary>
/// The response object for a labels request.
/// </summary>
public class LabelsResponse
{
    public Dictionary<string, LabelCount> labels { get; set; }

    public LabelsResponse(List<Label> labelsList)
    {
        labels = new Dictionary<string, LabelCount>();
        labelsList.ForEach(l =>
            labels.Add(l.LabelID, new LabelCount() { count = l.Images.Count }));
    }
}

/// <summary>
/// Count object for a label.
/// </summary>
public class LabelCount
{
    public int count { get; set; }
}