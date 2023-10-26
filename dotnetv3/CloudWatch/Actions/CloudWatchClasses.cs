// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using System.Text.Json.Serialization;

namespace CloudWatchActions;

public class Left
{
    [JsonPropertyName("min")]
    public int Min { get; set; }

    [JsonPropertyName("max")]
    public int Max { get; set; }
}

public class Properties
{
    [JsonPropertyName("markdown")]
    public string Markdown { get; set; } = null!;

    [JsonPropertyName("metrics")]
    public List<List<object>> Metrics { get; set; } = null!;

    [JsonPropertyName("view")]
    public string View { get; set; } = null!;

    [JsonPropertyName("region")]
    public string Region { get; set; } = null!;

    [JsonPropertyName("stat")]
    public string Stat { get; set; } = null!;

    [JsonPropertyName("period")]
    public int? Period { get; set; } = null!;

    [JsonPropertyName("yAxis")]
    public YAxis YAxis { get; set; } = null!;

    [JsonPropertyName("stacked")]
    public bool? Stacked { get; set; } = null!;

    [JsonPropertyName("title")]
    public string Title { get; set; } = null!;

    [JsonPropertyName("setPeriodToTimeRange")]
    public bool? SetPeriodToTimeRange { get; set; } = null!;

    [JsonPropertyName("liveData")]
    public bool? LiveData { get; set; } = null!;

    [JsonPropertyName("sparkline")]
    public bool? Sparkline { get; set; } = null!;

    [JsonPropertyName("trend")]
    public bool? Trend { get; set; } = null!;

    [JsonPropertyName("alarms")]
    public List<string> Alarms { get; set; } = null!;
}

public class DashboardModel
{
    [JsonPropertyName("widgets")]
    public List<Widget> Widgets { get; set; } = null!;
}

public class Widget
{
    [JsonPropertyName("type")]
    public string Type { get; set; } = null!;

    [JsonPropertyName("x")]
    public int X { get; set; }

    [JsonPropertyName("y")]
    public int Y { get; set; }

    [JsonPropertyName("width")]
    public int Width { get; set; }

    [JsonPropertyName("height")]
    public int Height { get; set; }

    [JsonPropertyName("properties")]
    public Properties Properties { get; set; }
}

public class YAxis
{
    [JsonPropertyName("left")]
    public Left Left { get; set; } = null!;
}