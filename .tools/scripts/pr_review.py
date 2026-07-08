# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
AI-powered PR review script for aws-doc-sdk-examples.

Retrieves comparable examples from Bedrock Knowledge Bases,
then uses Claude to review the PR against quality criteria.
"""

import boto3
import json
import os
import sys

REGION = "us-west-2"
MODEL_ID = "us.anthropic.claude-sonnet-4-20250514-v1:0"

# Map directory prefixes to language KB names
LANGUAGE_MAP = {
    "python": "python",
    "javav2": "java",
    "javascriptv3": "javascript",
    "dotnetv4": "dotnet",
    "rustv1": "rust",
    "gov2": "go",
    "swift": "swift",
    "ruby": "ruby",
    "php": "php",
    "cpp": "cpp",
    "kotlin": "kotlin",
}

REVIEW_CRITERIA = """
You are reviewing a code example PR for the AWS SDK documentation repository.
Evaluate the code against these criteria:

1. **Tested**: Does the PR include test files? Do the tests appear to cover the scenario adequately?
2. **Runnable**: Do imports resolve? Are dependencies declared? Is there anything obviously missing that would prevent execution?
3. **Guidelines conformance**: Does the code follow AWS SDK example best practices?
   - Clear comments explaining each step
   - Proper error handling
   - Resource cleanup
   - Minimal hardcoded values
   - Appropriate use of waiters/polling where needed
4. **Quality relative to comparables**: How does this example compare to similar examples in other languages? Is it as clear, complete, and idiomatic?

Be specific and actionable. Reference line numbers where possible.
If the PR looks good overall, say so briefly — don't invent issues.
Keep your review concise: no more than 10 points.
"""


def detect_language(files):
    """Detect the SDK language from PR file paths."""
    for file_path in files:
        for prefix, language in LANGUAGE_MAP.items():
            if file_path.startswith(prefix + "/"):
                return language
    return None


def detect_service(files):
    """Detect the AWS service from PR file paths."""
    for file_path in files:
        parts = file_path.split("/")
        # Pattern: {sdk}/example_code/{service}/...
        if len(parts) >= 3 and parts[1] == "example_code":
            return parts[2]
        # Pattern: {sdk}/scenarios/{service}/...
        if len(parts) >= 3 and parts[1] == "scenarios":
            return parts[2]
    return None


def get_kb_id(bedrock_agent, kb_name):
    """Look up a Knowledge Base ID by name."""
    try:
        response = bedrock_agent.list_knowledge_bases()
        for kb in response["knowledgeBaseSummaries"]:
            if kb["name"] == kb_name:
                return kb["knowledgeBaseId"]
    except Exception as e:
        print(f"Warning: Could not list knowledge bases: {e}")
    return None


def retrieve_comparables(bedrock_agent_runtime, kb_id, language, service, diff_summary):
    """Retrieve comparable examples from the Knowledge Base."""
    query = f"{service} example scenario"
    if language:
        query += f" (looking for examples in languages other than {language})"

    try:
        response = bedrock_agent_runtime.retrieve(
            knowledgeBaseId=kb_id,
            retrievalQuery={"text": query},
            retrievalConfiguration={
                "vectorSearchConfiguration": {"numberOfResults": 5}
            },
        )
        results = []
        for result in response.get("retrievalResults", []):
            content = result.get("content", {}).get("text", "")
            source = (
                result.get("location", {}).get("s3Location", {}).get("uri", "unknown")
            )
            results.append(f"### Source: {source}\n```\n{content[:2000]}\n```")

        return "\n\n".join(results) if results else "No comparable examples found."
    except Exception as e:
        print(f"Warning: KB retrieval failed: {e}")
        return "Could not retrieve comparable examples."


def retrieve_guidelines(bedrock_agent_runtime, kb_id):
    """Retrieve coding guidelines from the guidelines KB."""
    try:
        response = bedrock_agent_runtime.retrieve(
            knowledgeBaseId=kb_id,
            retrievalQuery={"text": "code example guidelines and standards"},
            retrievalConfiguration={
                "vectorSearchConfiguration": {"numberOfResults": 3}
            },
        )
        results = []
        for result in response.get("retrievalResults", []):
            content = result.get("content", {}).get("text", "")
            results.append(content[:1500])
        return "\n\n".join(results) if results else ""
    except Exception as e:
        print(f"Warning: Guidelines retrieval failed: {e}")
        return ""


def invoke_claude(bedrock_runtime, diff, comparables, guidelines, pr_title, pr_body):
    """Send the review request to Claude."""
    user_message = f"""## PR: {pr_title}

### PR Description
{pr_body or "No description provided."}

### Code Changes (diff)
```diff
{diff[:30000]}
```

### Comparable Examples from Other Languages
{comparables}

### Coding Guidelines
{guidelines}
"""

    response = bedrock_runtime.invoke_model(
        modelId=MODEL_ID,
        body=json.dumps(
            {
                "anthropic_version": "bedrock-2023-05-31",
                "max_tokens": 4096,
                "system": REVIEW_CRITERIA,
                "messages": [{"role": "user", "content": user_message}],
            }
        ),
        contentType="application/json",
    )

    response_body = json.loads(response["body"].read())
    return response_body["content"][0]["text"]


def set_output(name, value):
    """Set a GitHub Actions output variable."""
    output_file = os.environ.get("GITHUB_OUTPUT", "")
    if output_file:
        with open(output_file, "a") as f:
            f.write(f"{name}={value}\n")


def main():
    # Read PR metadata
    pr_title = os.environ.get("PR_TITLE", "")
    pr_body = os.environ.get("PR_BODY", "")

    # Read diff and file list
    try:
        with open("/tmp/pr_diff.txt", "r") as f:
            diff = f.read()
        with open("/tmp/pr_files.txt", "r") as f:
            files = [line.strip() for line in f.readlines() if line.strip()]
    except FileNotFoundError:
        print("Error: PR diff files not found.")
        set_output("has_review", "false")
        sys.exit(0)

    if not diff.strip():
        print("No diff content found. Skipping review.")
        set_output("has_review", "false")
        sys.exit(0)

    # Detect language and service
    language = detect_language(files)
    service = detect_service(files)

    print(f"Detected language: {language}")
    print(f"Detected service: {service}")

    if not service:
        print("Could not detect AWS service from file paths. Skipping review.")
        set_output("has_review", "false")
        sys.exit(0)

    # Initialize Bedrock clients
    bedrock_agent = boto3.client("bedrock-agent", region_name=REGION)
    bedrock_agent_runtime = boto3.client("bedrock-agent-runtime", region_name=REGION)
    bedrock_runtime = boto3.client("bedrock-runtime", region_name=REGION)

    # Retrieve comparable examples
    comparables = "No comparable examples found."
    if language:
        # Try language-specific KB first
        kb_name = f"{language}-premium-KB"
        kb_id = get_kb_id(bedrock_agent, kb_name)
        if kb_id:
            print(f"Retrieving comparables from {kb_name}...")
            comparables = retrieve_comparables(
                bedrock_agent_runtime, kb_id, language, service, diff[:1000]
            )

    # Retrieve guidelines
    guidelines = ""
    guidelines_kb_id = get_kb_id(bedrock_agent, "coding-standards-KB")
    if guidelines_kb_id:
        print("Retrieving coding guidelines...")
        guidelines = retrieve_guidelines(bedrock_agent_runtime, guidelines_kb_id)

    # Also try steering docs
    steering_kb_id = get_kb_id(bedrock_agent, "steering-docs-KB")
    if steering_kb_id:
        print("Retrieving steering docs...")
        steering = retrieve_guidelines(bedrock_agent_runtime, steering_kb_id)
        if steering:
            guidelines += "\n\n" + steering

    # Invoke Claude for review
    print("Generating AI review...")
    review = invoke_claude(
        bedrock_runtime, diff, comparables, guidelines, pr_title, pr_body
    )

    # Write review comment
    comment = f"""## 🤖 AI Code Example Review

{review}

---
<sub>This review was generated automatically using Amazon Bedrock. It compares your changes against existing examples and coding guidelines. Please use your judgment — this is advisory, not authoritative.</sub>
"""

    with open("/tmp/review_comment.md", "w") as f:
        f.write(comment)

    set_output("has_review", "true")
    print("Review generated successfully.")


if __name__ == "__main__":
    main()
