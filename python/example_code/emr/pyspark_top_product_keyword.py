# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to write a script that queries historical Amazon review data that is
stored in a public Amazon S3 bucket. The query returns the top reviewed products from
a category that contain a keyword in their product titles.

This script is intended to be run an Amazon EMR job step and uses PySpark to manage
running the query on the cluster instances.

To learn more about the Amazon Customer Reviews Dataset, see the README:
    https://s3.amazonaws.com/amazon-reviews-pds/readme.html
"""

# snippet-start:[emr.python.spark.top_category_reviews]
import argparse
from pyspark.sql import SparkSession
from pyspark.sql import functions as func


def query_review_data(category, title_keyword, count, output_uri):
    """
    Query the Amazon review dataset for top reviews from a category that contain a
    keyword in their product titles. The output of the query is written as JSON
    to the specified output URI.

    :param category: The category to query, such as Books or Grocery.
    :param title_keyword: The keyword that must be included in each returned product
                          title.
    :param count: The number of results to return.
    :param output_uri: The URI where the output JSON files are stored, typically an
                       Amazon S3 bucket, such as 's3://example-bucket/review-output'.
    """
    with SparkSession.builder.getOrCreate() as spark:
        input_uri = f's3://amazon-reviews-pds/parquet/product_category={category}'
        df = spark.read.parquet(input_uri)
        query_agg = df.filter(df.verified_purchase == 'Y') \
            .where(func.lower(func.col('product_title')).like(f'%{title_keyword}%')) \
            .groupBy('product_title') \
            .agg({'star_rating': 'avg', 'review_id': 'count'}) \
            .filter(func.col('count(review_id)') >= 50) \
            .sort(func.desc('avg(star_rating)')) \
            .limit(count) \
            .select(func.col('product_title').alias('product'),
                    func.col('count(review_id)').alias('review_count'),
                    func.col('avg(star_rating)').alias('review_avg_stars'))
        query_agg.write.mode('overwrite').json(output_uri)


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--category')
    parser.add_argument('--title_keyword')
    parser.add_argument('--count', type=int)
    parser.add_argument('--output_uri')
    args = parser.parse_args()
    query_review_data(args.category, args.title_keyword, args.count, args.output_uri)
# snippet-end:[emr.python.spark.top_category_reviews]
