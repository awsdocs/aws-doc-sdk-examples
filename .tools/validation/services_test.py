# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from pathlib import Path
import pytest
import yaml

import metadata_errors
from services import parse, Service, ServiceGuide


def load(path: Path) -> list[Service] | metadata_errors.MetadataErrors:
    root = Path(__file__).parent
    filename = root / "test_resources" / path
    with open(filename) as file:
        meta = yaml.safe_load(file)
    return parse(filename.name, meta)


def test_empty_services():
    examples = load("empty_services.yaml")
    assert examples._errors == [
        metadata_errors.MissingServiceBody(file="empty_services.yaml", id="sns")
    ]


def test_services_entity_usage():
    examples = load("entityusage_services.yaml")
    assert examples._errors == [
        metadata_errors.MappingMustBeEntity(
            file="entityusage_services.yaml", id="sns", field="long", value="SNSlong"
        ),
        metadata_errors.MappingMustBeEntity(
            file="entityusage_services.yaml", id="sns", field="short", value="SNS"
        ),
        metadata_errors.MissingField(
            file="entityusage_services.yaml",
            id="sns",
            field="version",
        ),
    ]


def test_services():
    examples = load("services.yaml")
    assert examples == {
        "s3": Service(
            short="&S3;",
            long="&S3long;",
            sort="S3",
            version="s3-2006-03-01",
            caveat="The examples in this section are pretty neat, and we recommend you print them out so you can read them in bed with a good glass of wine.",
            api_ref="AmazonS3/latest/API/Welcome.html",
            blurb="is storage for the internet. You can use Amazon S3 to store and retrieve any amount of data at any time, from anywhere on the web.",
            guide=ServiceGuide(
                subtitle="User Guide",
                url="AmazonS3/latest/userguide/Welcome.html",
            ),
        ),
        "sns": Service(
            short="&SNS;",
            long="&SNSlong;",
            sort="SNS",
            version="sns-2010-03-31",
            bundle="sqs",
        ),
        "sqs": Service(
            short="&SQS;",
            long="&SQSlong;",
            sort="SQS",
            tags={
                "product_categories": set(["Category 1", "Category 2"]),
            },
            version="sqs-2012-11-05",
            bundle="sqs",
        ),
        "textract": Service(
            short="&TEXTRACT;",
            long="&TEXTRACTlong;",
            sort="Textract",
            tags={"product_categories": set(["Category 1"])},
            version="textract-2018-06-27",
        ),
    }


if __name__ == "__main__":
    pytest.main([__file__, "-vv"])
