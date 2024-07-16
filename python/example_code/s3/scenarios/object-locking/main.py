# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
from cleanup import clean_s3_object_locking
from demo import demo_s3_object_locking
from deploy import deploy_s3_object_locking


def main():
    input("Ready to Deploy? Press Enter to continue...")
    deploy_s3_object_locking()

    input("Ready to do Demo? Press Enter to continue...")
    demo_s3_object_locking()

    input("Ready to Clean up? Press Enter to continue...")
    clean_s3_object_locking()


if __name__ == "__main__":
    main()
