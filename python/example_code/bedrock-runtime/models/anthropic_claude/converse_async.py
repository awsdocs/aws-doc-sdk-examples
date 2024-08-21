# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[python.example_code.bedrock-runtime.async.Converse_AnthropicClaude]
"""
Use the Conversation API to send a text message to Anthropic Claude. Streaming the
responses allows running the requests in parallel, speeding overall throughput for
several requests.
"""

import asyncio
import logging
import time
from collections.abc import AsyncIterator
from os import environ

import boto3
from botocore.exceptions import ClientError

logging.basicConfig(level=environ.get("LOG_LEVEL", "WARN").upper(), force=True)

# Create a Bedrock Runtime client in the AWS Region you want to use.
client = boto3.client("bedrock-runtime", region_name="us-east-1")

# Set the model ID, e.g., Claude 3 Haiku.
model_id = "anthropic.claude-3-haiku-20240307-v1:0"


async def converse_stream(user_message: str) -> AsyncIterator[str]:
    """Call Bedrock Runtime streaming. Yield each text item in the stream. Add a sleep(0) to unblock the asyncio loop."""
    conversation = [
        {
            "role": "user",
            "content": [{"text": user_message}],
        }
    ]

    try:
        yield f""""{user_message}":\n"""

        # Send the message to the model, using a basic inference configuration.
        response = client.converse_stream(
            modelId=model_id,
            messages=conversation,
            inferenceConfig={"maxTokens": 512, "temperature": 0.5, "topP": 0.9},
        )

        for chunk in response["stream"]:
            if "contentBlockDelta" in chunk:
                text = chunk["contentBlockDelta"]["delta"]["text"]
                print(f"In converse_stream {user_message} {text}")
                yield text
            # This await sleep(0) is necessary to allow the asyncio runtime an
            # opportunity to jump to other tasks in the block.
            await asyncio.sleep(0)

    except (ClientError, Exception) as e:
        print(f"ERROR: Can't invoke '{model_id}'. Reason: {e}")
        raise e


async def gather_stream(iterator: AsyncIterator[str]) -> str:
    return "".join([item async for item in iterator])


def make_tasks():
    prompts = [f"Count to {i * 10} in prime numbers" for i in range(2, 10)]
    return [converse_stream(prompt) for prompt in prompts]


async def main():
    start_parallel = time.time()
    parallel_results = await asyncio.gather(
        *[gather_stream(task) for task in make_tasks()]
    )
    end_parallel = time.time()

    start_sequential = time.time()
    sequential_results = [await gather_stream(task) for task in make_tasks()]
    end_sequential = time.time()

    logging.info("Parallel results: \n%s", parallel_results)
    logging.info("Sequential results:\n%s", sequential_results)

    print(f"Parallel took {end_parallel - start_parallel}s")  # EG 2.7 seconds
    print(f"Sequential took {end_sequential - start_sequential}s")  # EG 5.6 seconds

    print(
        "\n"
        "If you review the output of this program, you should see two sets of streaming log statements.\n"
        'The first should intermix "Count to 20", "Count to 30", etc. The second set should not mix,\n'
        'and be "Count to 20", "Count to 20", ... "Count to 90", " Count to 90".\n'
        "\n"
        "This shows the parallel nature of the first set of requests, and the sequential nature of the second set."
    )


if __name__ == "__main__":
    asyncio.run(main())


# snippet-end:[python.example_code.bedrock-runtime.async.Converse_AnthropicClaude]
