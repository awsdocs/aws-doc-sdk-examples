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
    public string Markdown { get; set; }

    [JsonPropertyName("metrics")]
    public List<List<object>> Metrics { get; set; }

    [JsonPropertyName("view")]
    public string View { get; set; }

    [JsonPropertyName("region")]
    public string Region { get; set; }

    [JsonPropertyName("stat")]
    public string Stat { get; set; }

    [JsonPropertyName("period")]
    public int? Period { get; set; }

    [JsonPropertyName("yAxis")]
    public YAxis YAxis { get; set; }

    [JsonPropertyName("stacked")]
    public bool? Stacked { get; set; }

    [JsonPropertyName("title")]
    public string Title { get; set; }

    [JsonPropertyName("setPeriodToTimeRange")]
    public bool? SetPeriodToTimeRange { get; set; }

    [JsonPropertyName("liveData")]
    public bool? LiveData { get; set; }

    [JsonPropertyName("sparkline")]
    public bool? Sparkline { get; set; }

    [JsonPropertyName("trend")]
    public bool? Trend { get; set; }

    [JsonPropertyName("alarms")]
    public List<string> Alarms { get; set; }
}

public class DashboardModel
{
    [JsonPropertyName("widgets")]
    public List<Widget> Widgets { get; set; }
}

public class Widget
{
    [JsonPropertyName("type")]
    public string Type { get; set; }

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
    public Left Left { get; set; }
}