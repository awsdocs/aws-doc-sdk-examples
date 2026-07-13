# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
AI-powered PR review script for aws-doc-sdk-examples.

Features:
- Retrieves comparable examples from Bedrock Knowledge Bases
- Includes full file content (not just diff) for holistic review
- Retrieves SPECIFICATION.md for scenario PRs
- Supports incremental reviews on synchronize events
- Produces per-file inline comments via the GitHub Review API
"""

import boto3
import json
import os
import subprocess
import sys

REGION = "us-west-2"
MODEL_ID = "us.anthropic.claude-sonnet-4-6"

# Map directory prefixes to language identifiers
LANGUAGE_MAP = {
    "python": "python",
    "javav2": "java",
    "javascriptv3": "javascript",
    "dotnetv3": "dotnet",
    "dotnetv4": "dotnet",
    "rustv1": "rust",
    "gov2": "go",
    "swift": "swift",
    "ruby": "ruby",
    "php": "php",
    "cpp": "cpp",
    "kotlin": "kotlin",
}

# Bedrock Knowledge Base IDs (account 415879937535, us-west-2)
LANGUAGE_KB_IDS = {
    "python": "VJYPXZTSXT",
    "java": "64P3VU9OAD",
    "dotnet": "CRSPSCYZIX",
}
CODING_STANDARDS_KB_ID = "Q2ZUWJJOIN"
STEERING_DOCS_KB_ID = "63A2M1LZ2E"

# Code file extensions to include in full-file context
CODE_EXTENSIONS = {
    ".py", ".java", ".js", ".ts", ".cs", ".go", ".rs",
    ".swift", ".rb", ".php", ".cpp", ".h", ".kt", ".sh",
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
4. **Quality relative to comparables**: How does this example compare to the premium reference examples? Does it follow the same structure, patterns, and idioms?
5. **Specification compliance** (if a SPECIFICATION.md is provided): Does the implementation satisfy the requirements described in the specification?

IMPORTANT: You must respond in valid JSON format with this structure:
{
  "summary": "Overall pass/fail verdict in 1-2 sentences",
  "detailed_review": "Full detailed analysis as a numbered list. Be specific and actionable. Reference filenames where possible. Include up to 10 points. Cover what's good AND what needs work.",
  "inline_comments": [
    {
      "path": "relative/path/to/file.py",
      "line": 42,
      "body": "Specific actionable feedback for this line"
    }
  ]
}

Rules for inline_comments:
- Only include comments where you can confidently identify the exact line number from the diff
- The "line" must be a line number from a @@ hunk header in the diff (a line that was added or is context)
- The "path" must exactly match a filename from the PR file list
- Keep each comment concise and actionable
- Maximum 10 inline comments
- If you cannot confidently determine line numbers, return an empty inline_comments array — put all feedback in detailed_review instead

Rules for detailed_review:
- Be specific and actionable, referencing filenames and methods
- Include up to 10 numbered points
- Cover both strengths and issues
- Note blocking issues vs. nice-to-haves
- If the PR looks good overall, say so and note any minor improvements
"""

INCREMENTAL_REVIEW_ADDENDUM = """
ADDITIONAL CONTEXT: This is a follow-up review. The PR was previously reviewed and has new commits.
Here is the previous review feedback:

{previous_review}

Focus your review on:
1. Whether the previous suggestions have been addressed
2. Any NEW issues introduced in the latest changes
3. Do NOT repeat feedback that has already been addressed

Mention in your summary which previous items were addressed and which (if any) remain.
"""


def detect_language(files):
    """Detect the SDK language from PR file paths."""
    for file_path in files:
        for prefix, language in LANGUAGE_MAP.items():
            if file_path.startswith(prefix + "/"):
                return language
    return None


def detect_sdk_prefix(files):
    """Detect the SDK directory prefix from PR file paths."""
    for file_path in files:
        for prefix in LANGUAGE_MAP:
            if file_path.startswith(prefix + "/"):
                return prefix
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
        # Pattern: gov2/{service}/... or dotnetv3/{service}/...
        if len(parts) >= 2 and parts[0] in ("gov2", "dotnetv3", "dotnetv4"):
            return parts[1]
        # Pattern: kotlin/services/{service}/...
        if len(parts) >= 3 and parts[0] == "kotlin" and parts[1] == "services":
            return parts[2]
        # Pattern: rustv1/examples/{service}/...
        if len(parts) >= 3 and parts[0] == "rustv1" and parts[1] == "examples":
            return parts[2]
    return None


def detect_scenario_path(files):
    """Detect if the PR is for a scenario and return the scenario directory."""
    for file_path in files:
        parts = file_path.split("/")
        # Pattern: scenarios/{scenario_name}/... (top-level scenarios dir)
        if len(parts) >= 2 and parts[0] == "scenarios":
            return f"scenarios/{parts[1]}"
        # Pattern: {sdk}/example_code/{service}/scenarios/{scenario_name}/...
        if "scenarios" in parts:
            idx = parts.index("scenarios")
            if idx + 1 < len(parts):
                return "/".join(parts[: idx + 2])
    return None


def get_specification(scenario_path):
    """Try to find and read SPECIFICATION.md for a scenario."""
    # Check common locations
    candidates = [
        f"{scenario_path}/SPECIFICATION.md",
        f"{scenario_path}/../SPECIFICATION.md",
    ]

    # Also check the top-level scenarios directory
    parts = scenario_path.split("/")
    if len(parts) >= 2:
        # e.g., scenarios/s3_basics/SPECIFICATION.md
        candidates.append(f"scenarios/{parts[-1]}/SPECIFICATION.md")

    for candidate in candidates:
        if os.path.isfile(candidate):
            try:
                with open(candidate, "r") as f:
                    return f.read()
            except (IOError, OSError):
                continue
    return None


def load_previous_reviews(data_dir="/tmp"):
    """Load previous review comments (summary + inline) for incremental review."""
    parts = []

    # Load summary comments
    try:
        with open(os.path.join(data_dir, "previous_reviews.txt"), "r") as f:
            content = f.read().strip()
            if content:
                parts.append("### Previous Summary Review:\n" + content)
    except FileNotFoundError:
        pass

    # Load inline comments
    try:
        with open(os.path.join(data_dir, "previous_inline_comments.json"), "r") as f:
            content = f.read().strip()
            if content:
                inline_comments = []
                for line in content.splitlines():
                    try:
                        comment = json.loads(line)
                        path = comment.get("path", "")
                        line_num = comment.get("line", "")
                        body = comment.get("body", "")
                        if path and body:
                            inline_comments.append(
                                f"- **{path}:{line_num}**: {body}"
                            )
                    except json.JSONDecodeError:
                        continue
                if inline_comments:
                    parts.append(
                        "### Previous Inline Comments:\n"
                        + "\n".join(inline_comments)
                    )
    except FileNotFoundError:
        pass

    return "\n\n".join(parts) if parts else None


def retrieve_comparables(bedrock_agent_runtime, kb_id, language, service):
    """Retrieve comparable examples from the Knowledge Base."""
    query = f"{service} example scenario in {language}"
    try:
        response = bedrock_agent_runtime.retrieve(
            knowledgeBaseId=kb_id,
            retrievalQuery={"text": query},
            retrievalConfiguration={
                "vectorSearchConfiguration": {"numberOfResults": 10}
            },
        )

        MIN_SCORE = 0.3
        source_chunks = {}
        for result in response.get("retrievalResults", []):
            score = result.get("score", 0)
            source = (
                result.get("location", {}).get("s3Location", {}).get("uri", "unknown")
            )
            print(f"  KB result: score={score:.3f} source={source}")
            if score < MIN_SCORE:
                continue
            content = result.get("content", {}).get("text", "")
            if source not in source_chunks:
                source_chunks[source] = []
            source_chunks[source].append(content)

        results = []
        for source, chunks in source_chunks.items():
            combined = "\n\n".join(chunks)
            results.append(f"### Source: {source}\n```\n{combined}\n```")
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


def build_full_files_context(full_files, files):
    """Build a context section with full file contents."""
    if not full_files:
        return ""

    sections = []
    for filename, content in full_files.items():
        # Truncate very large files
        if len(content) > 15000:
            content = content[:15000] + "\n... [truncated]"
        sections.append(f"### {filename}\n```\n{content}\n```")

    if not sections:
        return ""

    return "## Full File Contents\n\n" + "\n\n".join(sections)


def invoke_claude(
    bedrock_runtime,
    diff,
    full_files_context,
    comparables,
    guidelines,
    specification,
    pr_title,
    pr_body,
    is_incremental,
    previous_review,
):
    """Send the review request to Claude."""
    system_prompt = REVIEW_CRITERIA
    if is_incremental and previous_review:
        system_prompt += INCREMENTAL_REVIEW_ADDENDUM.format(
            previous_review=previous_review[:5000]
        )

    user_message = f"""## PR: {pr_title}

### PR Description
{pr_body or "No description provided."}

### Code Changes (diff)
```diff
{diff[:80000]}
```

{full_files_context}

### Comparable Examples from Knowledge Base
{comparables}

### Coding Guidelines
{guidelines}
"""

    if specification:
        user_message += f"""
### SPECIFICATION.md (requirements for this scenario)
```markdown
{specification[:10000]}
```
"""

    response = bedrock_runtime.invoke_model(
        modelId=MODEL_ID,
        body=json.dumps(
            {
                "anthropic_version": "bedrock-2023-05-31",
                "max_tokens": 4096,
                "system": system_prompt,
                "messages": [{"role": "user", "content": user_message}],
            }
        ),
        contentType="application/json",
    )

    response_body = json.loads(response["body"].read())
    return response_body["content"][0]["text"]


def parse_review_response(response_text):
    """Parse Claude's JSON response into structured review data."""
    # Try to extract JSON from the response
    try:
        # Handle case where response is wrapped in markdown code block
        if "```json" in response_text:
            start = response_text.index("```json") + 7
            end = response_text.index("```", start)
            response_text = response_text[start:end].strip()
        elif "```" in response_text:
            start = response_text.index("```") + 3
            end = response_text.index("```", start)
            response_text = response_text[start:end].strip()

        return json.loads(response_text)
    except (json.JSONDecodeError, ValueError):
        # If JSON parsing fails, return as a simple summary
        return {"summary": response_text, "inline_comments": []}


def build_review_payload(parsed_review, pr_files, head_sha):
    """Build the GitHub Pull Request Review API payload."""
    summary = parsed_review.get("summary", "No summary provided.")
    detailed_review = parsed_review.get("detailed_review", "")
    inline_comments = parsed_review.get("inline_comments", [])

    # Build the review body
    body = f"## 🤖 AI Code Example Review\n\n{summary}\n\n"
    if detailed_review:
        body += f"### Detailed Review\n\n{detailed_review}\n\n"
    body += "---\n<sub>This review was generated automatically using Amazon Bedrock. "
    body += "It compares your changes against existing examples and coding guidelines. "
    body += "Please use your judgment — this is advisory, not authoritative.</sub>"

    # Build comments array for the API
    comments = []
    if head_sha:
        for comment in inline_comments:
            path = comment.get("path", "")
            line = comment.get("line")
            comment_body = comment.get("body", "")

            # Validate the comment has required fields and path is in the PR
            if path and line and comment_body and path in pr_files:
                comments.append({
                    "path": path,
                    "line": int(line),
                    "side": "RIGHT",
                    "subject_type": "line",
                    "body": f"🤖 {comment_body}",
                })
    else:
        print("Warning: head_sha is empty, skipping inline comments")

    payload = {"event": "COMMENT", "body": body}

    # commit_id is required for inline comments to be placed correctly
    if head_sha:
        payload["commit_id"] = head_sha
    else:
        print("Warning: No commit_id available. Review will be summary-only.")

    if comments:
        payload["comments"] = comments

    return payload


def set_output(name, value):
    """Set a GitHub Actions output variable."""
    output_file = os.environ.get("GITHUB_OUTPUT", "")
    if output_file:
        with open(output_file, "a") as f:
            f.write(f"{name}={value}\n")


def main():
    # Determine data directory (artifact-based or legacy /tmp)
    data_dir = os.environ.get("PR_DATA_DIR", "/tmp")

    # Read PR metadata
    metadata_file = os.path.join(data_dir, "metadata.json")
    if os.path.isfile(metadata_file):
        with open(metadata_file, "r") as f:
            metadata = json.load(f)
        pr_title = metadata.get("pr_title", "")
        pr_body = metadata.get("pr_body", "")
        head_sha = metadata.get("head_sha", "")
        event_action = metadata.get("event_action", "opened")
        pr_number = metadata.get("pr_number", "")
    else:
        # Fallback to environment variables (legacy mode)
        pr_title = os.environ.get("PR_TITLE", "")
        pr_body = os.environ.get("PR_BODY", "")
        head_sha = os.environ.get("PR_HEAD_SHA", "")
        event_action = os.environ.get("PR_EVENT_ACTION", "opened")
        pr_number = os.environ.get("PR_NUMBER", "")

    is_incremental = event_action == "synchronize"

    # Read diff and file list
    diff_file = os.path.join(data_dir, "pr_diff.txt")
    files_file = os.path.join(data_dir, "pr_files.txt")
    try:
        with open(diff_file, "r") as f:
            diff = f.read()
        with open(files_file, "r") as f:
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
    sdk_prefix = detect_sdk_prefix(files)

    print(f"Detected language: {language}")
    print(f"Detected service: {service}")
    print(f"Detected SDK prefix: {sdk_prefix}")
    print(f"Event action: {event_action} (incremental: {is_incremental})")
    print(f"Head SHA: {head_sha or '(empty)'}")

    if not service:
        print("Could not detect AWS service from file paths. Skipping review.")
        set_output("has_review", "false")
        sys.exit(0)

    # Load full file contents
    full_files_dir = os.path.join(data_dir, "full_files")
    full_files = {}
    if os.path.isdir(full_files_dir):
        for filename in os.listdir(full_files_dir):
            filepath = os.path.join(full_files_dir, filename)
            try:
                with open(filepath, "r") as f:
                    content = f.read()
                    if content.strip():
                        full_files[filename] = content
            except (IOError, UnicodeDecodeError):
                continue

    print(f"Loaded {len(full_files)} full file(s) for context")
    full_files_context = build_full_files_context(full_files, files)

    # Load previous reviews for incremental mode
    previous_review = None
    if is_incremental:
        previous_review = load_previous_reviews(data_dir)
        if previous_review:
            print("Loaded previous review for incremental comparison")
        else:
            print("No previous review found, doing full review")

    # Check for SPECIFICATION.md
    scenario_path = detect_scenario_path(files)
    specification = None
    if scenario_path:
        print(f"Detected scenario path: {scenario_path}")
        specification = get_specification(scenario_path)
        if specification:
            print(f"Found SPECIFICATION.md ({len(specification)} chars)")
        else:
            print("No SPECIFICATION.md found for this scenario")

    # Initialize Bedrock clients
    bedrock_agent_runtime = boto3.client("bedrock-agent-runtime", region_name=REGION)
    bedrock_runtime = boto3.client("bedrock-runtime", region_name=REGION)

    # Retrieve comparable examples
    comparables = "No comparable examples found."
    if language:
        kb_id = LANGUAGE_KB_IDS.get(language)
        if kb_id:
            print(f"Retrieving comparables from {language} KB ({kb_id})...")
            comparables = retrieve_comparables(
                bedrock_agent_runtime, kb_id, language, service
            )

    # Retrieve guidelines
    guidelines = ""
    if CODING_STANDARDS_KB_ID:
        print("Retrieving coding guidelines...")
        guidelines = retrieve_guidelines(bedrock_agent_runtime, CODING_STANDARDS_KB_ID)

    if STEERING_DOCS_KB_ID:
        print("Retrieving steering docs...")
        steering = retrieve_guidelines(bedrock_agent_runtime, STEERING_DOCS_KB_ID)
        if steering:
            guidelines += "\n\n" + steering

    # Invoke Claude for review
    print("Generating AI review...")
    review_text = invoke_claude(
        bedrock_runtime,
        diff,
        full_files_context,
        comparables,
        guidelines,
        specification,
        pr_title,
        pr_body,
        is_incremental,
        previous_review,
    )

    # Parse the structured response
    parsed_review = parse_review_response(review_text)
    print(
        f"Review parsed: {len(parsed_review.get('inline_comments', []))} inline comments"
    )

    # Build the review API payload
    payload = build_review_payload(parsed_review, files, head_sha)

    # Write the payload for the workflow to post
    with open("/tmp/review_payload.json", "w") as f:
        json.dump(payload, f)

    set_output("has_review", "true")
    print("Review generated successfully.")



if __name__ == "__main__":
    main()
