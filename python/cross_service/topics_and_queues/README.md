# Topics and Queues Cross-Service Scenario

## Overview

This scenario demonstrates messaging with topics and queues using Amazon Simple Notification Service (Amazon SNS) and Amazon Simple Queue Service (Amazon SQS). The scenario shows how to create topics, queues, subscribe queues to topics, publish messages, and handle message filtering.

## What it demonstrates

- Create SNS topics (standard and FIFO)
- Create SQS queues (standard and FIFO) 
- Configure queue policies to allow SNS message delivery
- Subscribe queues to topics with optional message filtering
- Publish messages with attributes and FIFO-specific parameters
- Poll queues for messages and display results
- Clean up resources (delete queues, unsubscribe, delete topics)

## Files

- `topics_and_queues_scenario.py` - Main scenario orchestration
- `sns_wrapper.py` - SNS operations wrapper class
- `sqs_wrapper.py` - SQS operations wrapper class  
- `requirements.txt` - Python dependencies
- `test/` - Integration tests

## Prerequisites

- Python 3.8 or later
- AWS credentials configured (via AWS CLI, environment variables, or IAM roles)
- Appropriate AWS permissions for SNS and SQS operations

## Setup

1. Install dependencies:
```bash
pip install -r requirements.txt
```

2. Ensure AWS credentials are configured:
```bash
aws configure
```

## Running the scenario

```bash
python topics_and_queues_scenario.py
```

## Scenario workflow

### 1. Topic Setup
- Choose between standard or FIFO topic
- For FIFO topics, configure deduplication options
- Create the SNS topic

### 2. Queue Setup  
- Create SQS queues (matching topic type)
- Configure queue policies to allow SNS message delivery
- Subscribe queues to the topic with optional message filtering

### 3. Message Publishing
- Publish messages to the topic
- For FIFO topics, specify message group ID and optional deduplication ID
- Add tone attributes for message filtering

### 4. Message Polling
- Poll each queue for messages
- Display message contents
- Delete messages after processing

### 5. Cleanup
- Option to delete queues
- Unsubscribe from topics  
- Option to delete topics

## FIFO Features

When using FIFO topics and queues, the scenario demonstrates:

- **Message Ordering**: Messages within the same message group are delivered in order
- **Deduplication**: Prevents duplicate message delivery using deduplication IDs or content-based deduplication
- **Message Filtering**: Filter messages by tone attribute (cheerful, funny, serious, sincere)

## Error Handling

The scenario includes comprehensive error handling:
- AWS service errors are caught and logged
- User-friendly error messages
- Graceful cleanup on failures
- Validation of user inputs

## Architecture

```
┌─────────────────┐    ┌─────────────────┐
│   SNS Topic     │    │   SQS Queue 1   │
│                 ├────┤                 │
│  (Standard or   │    │  (with optional │
│   FIFO)         │    │   filtering)    │
└─────────────────┘    └─────────────────┘
         │              
         │              ┌─────────────────┐
         └──────────────┤   SQS Queue 2   │
                        │                 │
                        │  (with optional │
                        │   filtering)    │
                        └─────────────────┘
```

## Testing

Run the integration tests:
```bash
cd test
python -m pytest test_topics_and_queues_scenario.py -v
```

## Clean up

The scenario provides interactive cleanup options at the end. You can also manually clean up resources:

1. Delete SQS queues from the AWS Console
2. Delete SNS topics from the AWS Console
3. Subscriptions are automatically deleted when queues are deleted

## Related AWS Services

- [Amazon SNS Documentation](https://docs.aws.amazon.com/sns/)
- [Amazon SQS Documentation](https://docs.aws.amazon.com/sqs/)
- [SNS Message Filtering](https://docs.aws.amazon.com/sns/latest/dg/sns-message-filtering.html)
- [FIFO Topics](https://docs.aws.amazon.com/sns/latest/dg/sns-fifo-topics.html)
