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
    ".py",
    ".java",
    ".js",
    ".ts",
    ".cs",
    ".go",
    ".rs",
    ".swift",
    ".rb",
    ".php",
    ".cpp",
    ".h",
    ".kt",
    ".sh",
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

SEVERITY DEFINITIONS — label every issue with exactly one, and do not inflate:
- **blocking**: A correctness, security, or won't-run defect — the code fails to
  compile/execute, produces wrong results, leaks resources, or has a security
  flaw. Reserve "blocking" for these. A concern is only blocking if you can
  point to the specific code that causes it.
- **nit**: Style, idiom, naming, type-annotation, or cosmetic suggestions. These
  include things like a nullable type annotation on a never-null value, or
  preferring one API over another. NEVER mark a nit as blocking.
- **policy**: Team-process observations that are not code defects — e.g. "no
  automated tests included". Note these as policy items, not blocking defects.
  The presence or absence of tests is a policy call for a human, not a defect.

EVIDENCE DISCIPLINE — avoid the most common false positives:
- Only assert a defect you can verify from the code actually provided. If the
  relevant code is truncated, not included, or you are inferring behavior you
  cannot see, say so instead of asserting a problem.
- Do NOT claim code is unreachable, that cleanup is missing, or that a function
  is only called on an error path unless the call site proving it is present in
  the provided context. For example, a `finally:` block runs on BOTH the success
  and exception paths — do not describe `finally` cleanup as exception-only.
- Verify identifiers against the shown source before flagging a typo or
  NameError; do not infer a missing/renamed symbol from a truncated line.

IMPORTANT: You must respond in valid JSON format with this structure:
{
  "summary": "Overall pass/fail verdict in 1-2 sentences",
  "detailed_review": "Full detailed analysis as a numbered list. Be specific and actionable. Reference filenames where possible. Include up to 10 points. Cover what's good AND what needs work. Prefix each point with its severity in brackets, e.g. '[blocking]', '[nit]', or '[policy]'.",
  "inline_comments": [
    {
      "path": "relative/path/to/file.py",
      "line": 42,
      "severity": "nit",
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
- Prefix each point with its severity: [blocking], [nit], or [policy]
- Apply the SEVERITY DEFINITIONS above strictly — do not inflate nits or policy
  items to blocking
- If the PR looks good overall, say so and note any minor improvements
"""

INCREMENTAL_REVIEW_ADDENDUM = """
ADDITIONAL CONTEXT: This is a follow-up review. The PR was previously reviewed and has new commits.
The current code you are reviewing is at commit {head_sha}.
Here is the previous review feedback:

{previous_review}

CRITICAL RE-VERIFICATION RULES:
1. The previous feedback was written against an EARLIER commit. Before repeating
   any prior item, re-verify it against the CURRENT code shown to you (diff and
   full file contents at {head_sha}). If the current code no longer has the
   issue, treat it as ADDRESSED — do not re-raise it.
2. Never label an item "still unresolved" or "not addressed" unless you can point
   to the specific current line at {head_sha} that still exhibits it. A prior
   finding is not evidence; only the current code is.
3. Do NOT re-emit a previously-raised finding as if it were "new". If it appeared
   in earlier feedback, refer to it as a prior item and state whether it is now
   addressed or still present (with current-line evidence).
4. Your summary and your detailed points MUST agree. If your summary says an item
   was addressed, do not also list it as unresolved.

Focus your review on:
1. Whether previous suggestions have been addressed (verified against current code)
2. Any genuinely NEW issues introduced by the latest changes
3. Do NOT repeat feedback that has already been addressed
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
        # Pattern: scenarios/{category}/{service}/... (top-level scenarios dir)
        if len(parts) >= 3 and parts[0] == "scenarios":
            return f"scenarios/{parts[1]}/{parts[2]}"
        # Pattern: {sdk}/example_code/{service}/scenarios/{scenario_name}/...
        if "scenarios" in parts:
            idx = parts.index("scenarios")
            if idx + 1 < len(parts):
                return "/".join(parts[: idx + 2])
    return None


def _read_spec(path):
    """Read a SPECIFICATION.md, returning (path, contents) or None."""
    if path and os.path.isfile(path):
        try:
            with open(path, "r") as f:
                return path, f.read()
        except (IOError, OSError):
            return None
    return None


def get_specification(scenario_path, service, files):
    """Find and read the SPECIFICATION.md that actually belongs to this PR.

    Resolution is deliberately conservative — a wrong spec is worse than no
    spec, because it makes the reviewer evaluate the code against a different
    service's requirements. We therefore only accept a spec that is tied to the
    PR's own files or its detected scenario path, and never guess across
    unrelated scenario directories by service-name prefix.

    Returns a tuple of (resolved_path, contents), or None if no spec is
    confidently associated with this PR.

    Resolution order:
    1. A SPECIFICATION.md included directly in the PR's changed files.
    2. A SPECIFICATION.md sitting in the same directory as a changed file
       (co-located with the code being reviewed).
    3. The SPECIFICATION.md at the detected scenario_path.
    """
    # 1. A SPECIFICATION.md that is itself part of the PR diff.
    for file_path in files:
        if file_path.endswith("SPECIFICATION.md"):
            result = _read_spec(file_path)
            if result:
                return result

    # 2. A SPECIFICATION.md co-located with a changed file's directory.
    #    Only consider directories that the PR actually touches, so we can't
    #    wander into a different service's scenario.
    seen_dirs = []
    for file_path in files:
        directory = os.path.dirname(file_path)
        while directory and directory not in seen_dirs:
            seen_dirs.append(directory)
            candidate = os.path.join(directory, "SPECIFICATION.md")
            result = _read_spec(candidate)
            if result:
                return result
            # Walk up one level (e.g. .../scenarios/x/lang/ -> .../scenarios/x/)
            parent = os.path.dirname(directory)
            if parent == directory:
                break
            directory = parent

    # 3. The detected scenario path directly.
    if scenario_path:
        result = _read_spec(f"{scenario_path}/SPECIFICATION.md")
        if result:
            return result

    # No spec confidently associated with this PR. Intentionally do NOT
    # fall back to searching scenarios/ by service name — that fuzzy match
    # historically pulled an unrelated service's spec into the review.
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
                            inline_comments.append(f"- **{path}:{line_num}**: {body}")
                    except json.JSONDecodeError:
                        continue
                if inline_comments:
                    parts.append(
                        "### Previous Inline Comments:\n" + "\n".join(inline_comments)
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
        # Truncate very large files, and make the truncation explicit so the
        # model does not raise defects about code it cannot actually see.
        truncated = False
        if len(content) > 15000:
            content = content[:15000]
            truncated = True
        section = f"### {filename}\n```\n{content}\n```"
        if truncated:
            section += (
                "\n> NOTE: This file was truncated for length; content beyond "
                "the shown portion is NOT included. Do not raise defects about "
                "code that is not visible here — if a concern depends on the "
                "truncated region, say so instead of asserting a problem."
            )
        sections.append(section)

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
    specification_path,
    pr_title,
    pr_body,
    is_incremental,
    previous_review,
    head_sha,
):
    """Send the review request to Claude."""
    system_prompt = REVIEW_CRITERIA
    if is_incremental and previous_review:
        system_prompt += INCREMENTAL_REVIEW_ADDENDUM.format(
            previous_review=previous_review[:5000],
            head_sha=head_sha or "(unknown)",
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
This specification was resolved from the path `{specification_path}`. It is the
spec the repository tooling associated with the changed files. Before using it,
confirm it actually describes the same service and scenario as the code under
review. If the specification clearly covers a different service or feature than
the code (a tooling mismatch), do NOT raise specification-compliance defects —
instead note the apparent mismatch in `detailed_review` as an informational
item and review the code on its own merits.
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
    body += "---\n"
    if head_sha:
        body += f"<sub>Reviewed at commit `{head_sha}`. "
    else:
        body += "<sub>"
    body += "This review was generated automatically using Amazon Bedrock. "
    body += "It compares your changes against existing examples and coding guidelines. "
    body += "Findings are labeled by severity (blocking / nit / policy) and reflect "
    body += "only the commit above. Please use your judgment — this is advisory, not authoritative.</sub>"

    # Build comments array for the API
    comments = []
    if head_sha:
        for comment in inline_comments:
            path = comment.get("path", "")
            line = comment.get("line")
            comment_body = comment.get("body", "")

            # Validate the comment has required fields and path is in the PR
            if path and line and comment_body and path in pr_files:
                severity = str(comment.get("severity", "")).strip().lower()
                if severity in ("blocking", "nit", "policy"):
                    prefix = f"🤖 [{severity}] "
                else:
                    prefix = "🤖 "
                comments.append(
                    {
                        "path": path,
                        "line": int(line),
                        "side": "RIGHT",
                        "body": f"{prefix}{comment_body}",
                    }
                )
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

    # Load full file contents. Files are stored under their full relative path
    # (e.g. full_files/python/example_code/s3/foo.py) so that same-named files
    # across languages/services do not collide.
    full_files_dir = os.path.join(data_dir, "full_files")
    full_files = {}
    if os.path.isdir(full_files_dir):
        for root, _dirs, filenames in os.walk(full_files_dir):
            for filename in filenames:
                filepath = os.path.join(root, filename)
                relpath = os.path.relpath(filepath, full_files_dir)
                try:
                    with open(filepath, "r") as f:
                        content = f.read()
                        if content.strip():
                            full_files[relpath] = content
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
    if scenario_path:
        print(f"Detected scenario path: {scenario_path}")
    spec_result = get_specification(scenario_path, service, files)
    specification = None
    specification_path = None
    if spec_result:
        specification_path, specification = spec_result
        print(
            f"Found SPECIFICATION.md at {specification_path} "
            f"({len(specification)} chars)"
        )
    else:
        print("No SPECIFICATION.md confidently associated with this PR")

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
        specification_path,
        pr_title,
        pr_body,
        is_incremental,
        previous_review,
        head_sha,
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
