# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
import random
import time
import json
from faker import Faker

# Initialize Faker
fake = Faker()


# Function to generate a fake IP address
def generate_ip():
    return fake.ipv4()


# Function to generate a Unix timestamp
def generate_timestamp():
    return int(time.time())


# Function to generate an alert level
def generate_alert_level():
    return random.choice(["Low", "Medium", "High", "Critical"])


# Generate 2,550 sample records
records = []
for _ in range(2550):
    record = {
        "ip_address": generate_ip(),
        "timestamp": generate_timestamp(),
        "alert_level": generate_alert_level(),
    }
    records.append(record)

# Print sample records
for record in records[:5]:  # Print the first 5 records as a sample
    print(json.dumps(record, indent=2))

# Optionally save to a file
with open("../sample_records.json", "w") as f:
    json.dump(records, f, indent=2)

print("Generated 2,550 sample records.")
