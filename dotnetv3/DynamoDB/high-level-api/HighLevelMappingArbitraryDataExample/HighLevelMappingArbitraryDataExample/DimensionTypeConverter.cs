// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

namespace HighLevelMappingArbitraryDataExample
{
    using System;
    using Amazon.DynamoDBv2.DataModel;
    using Amazon.DynamoDBv2.DocumentModel;

    // snippet-start:[dynamodb.dotnetv3.HighLevelMappingArbitraryData.DimensionTypeConverter]

    /// <summary>
    /// Includes methods to convert the complex type, DimensionType, to string
    /// and to convert a string to DimensionType.
    /// </summary>
    public class DimensionTypeConverter : IPropertyConverter
    {
        public DynamoDBEntry ToEntry(object value)
        {
            DimensionType bookDimensions = value as DimensionType;
            if (bookDimensions == null)
            {
                throw new ArgumentOutOfRangeException();
            }

            string data = string.Format($"{bookDimensions.Length} x {bookDimensions.Height} x {bookDimensions.Thickness}");

            DynamoDBEntry entry = new Primitive
            {
                Value = data,
            };
            return entry;
        }

        public object FromEntry(DynamoDBEntry entry)
        {
            Primitive primitive = entry as Primitive;
            if (primitive == null || !(primitive.Value is string) || string.IsNullOrEmpty((string)primitive.Value))
            {
                throw new ArgumentOutOfRangeException();
            }

            string[] data = ((string)primitive.Value).Split(new[] { " x " }, StringSplitOptions.None);
            if (data.Length != 3)
            {
                throw new ArgumentOutOfRangeException();
            }

            DimensionType complexData = new DimensionType
            {
                Length = Convert.ToDecimal(data[0]),
                Height = Convert.ToDecimal(data[1]),
                Thickness = Convert.ToDecimal(data[2]),
            };

            return complexData;
        }
    }

    // snippet-end:[dynamodb.dotnetv3.HighLevelMappingArbitraryData.DimensionTypeConverter]
}
