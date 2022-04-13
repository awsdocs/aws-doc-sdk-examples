// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

namespace HighLevelMappingArbitraryDataExample
{
  // snippet-start:[dynamodb.dotnet35.HighLevelMappingArbitraryData.DimensionType]

  /// <summary>
  /// Defines the dimensions of a book.
  /// </summary>
  public class DimensionType
  {
    public decimal Length { get; set; }

    public decimal Height { get; set; }

    public decimal Thickness { get; set; }
  }

  // snippet-end:[dynamodb.dotnet35.HighLevelMappingArbitraryData.DimensionType]
}
