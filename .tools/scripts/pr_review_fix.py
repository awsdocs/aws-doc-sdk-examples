# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
AI-powered PR fix suggestion script for aws-doc-sdk-examples.

Reads the latest AI review, queries the same Bedrock KBs for context,
generates fixes, and posts them as GitHub suggested changes.
Never commits or pushes — the human decides what to accept.
"""

import boto3
import json
import os
import subprocess
import sys
from botocore.config import Config

REGION = "us-west-2"
MODEL_ID = "us.anthropic.claude-sonnet-4-6"
BEDROCK_CONFIG = Config(read_timeout=300, connect_timeout=10, retries={"max_attempts": 2})

# Same KB IDs as pr_review.py — maintains context alignment
LANGUAGE_KB_IDS = {
    "python": "VJYPXZTSXT",
    "java": "64P3VU9OAD",
    "dotnet": "CRSPSCYZIX",
}
CODING_STANDARDS_KB_ID = "Q2ZUWJJOIN"
STEERING_DOCS_KB_ID = "63A2M1LZ2E"

# Same language map as pr_review.py
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

FIX_PROMPT = """\
You are fixing code in a PR based on review feedback. You have access to:
- The review feedback (summary + inline comments + detailed review)
- The current file contents
- Premium comparable examples from the knowledge base
- Coding guidelines and steering docs

Your job: generate GitHub suggested changes that address EACH piece of feedback.

IMPORTANT RULES:
1. Address ALL feedback, including subjective suggestions about style and patterns.
2. Match the style/patterns of the comparable examples exactly.
3. Each suggestion must be a self-contained code replacement for specific lines.
4. If a suggestion requires creating a new file, provide the full file content
   and indicate it should be created (these can't be GitHub suggestions, so describe
   them clearly as "Create file: path/to/file.py" with the content).

Respond in valid JSON with this structure:
{
  "suggestions": [
    {
      "path": "relative/path/to/file.py",
      "start_line": 10,
      "end_line": 15,
      "replacement": "the replacement code for those lines",
      "comment": "Brief explanation of the fix"
    }
  ],
  "new_files": [
    {
      "path": "relative/path/to/new_file.py",
      "content": "full file content",
      "comment": "Why this file is needed"
    }
  ]
}

Rules for suggestions:
- start_line and end_line refer to the CURRENT file line numbers (1-indexed)
- The replacement replaces lines start_line through end_line (inclusive)
- Keep replacements minimal — only change what's needed
- If multiple nearby changes are needed, combine them into one suggestion
- Ensure the replacement maintains correct indentation
- Maximum 20 suggestions total

Rules for new_files:
- Only use this for files that don't exist yet
- Provide complete, working file content
- Follow the same patterns as comparable examples
- If the file would be very long (>100 lines), provide just the first 50 lines
  and a comment indicating the full structure needed
"""


def run_gh(args):
    """Run a gh CLI command and return stdout."""
    result = subprocess.run(
        ["gh", "api"] + args,
        capture_output=True,
        text=True,
    )
    if result.returncode != 0:
        print(f"gh api error: {result.stderr}")
        return None
    return result.stdout


def fetch_latest_ai_review(repo, pr_number):
    """Fetch the most recent AI review from the PR."""
    # Get all reviews on the PR
    reviews_json = run_gh([
        f"repos/{repo}/pulls/{pr_number}/reviews",
        "--paginate",
    ])
    if not reviews_json:
        return None, None

    reviews = json.loads(reviews_json)

    # Find the latest AI review (identified by the 🤖 marker)
    ai_review = None
    for review in reversed(reviews):
        body = review.get("body", "")
        if "🤖 AI Code Example Review" in body:
            ai_review = review
            break

    if not ai_review:
        return None, None

    review_id = ai_review["id"]
    review_body = ai_review.get("body", "")

    # Get inline comments from this review
    comments_json = run_gh([
        f"repos/{repo}/pulls/{pr_number}/reviews/{review_id}/comments",
        "--paginate",
    ])
    inline_comments = json.loads(comments_json) if comments_json else []

    return review_body, inline_comments


def fetch_pr_files(repo, pr_number):
    """Fetch the list of files and their contents from the PR."""
    files_json = run_gh([
        f"repos/{repo}/pulls/{pr_number}/files",
        "--paginate",
    ])
    if not files_json:
        return [], {}

    files_data = json.loads(files_json)
    file_paths = [f["filename"] for f in files_data]

    # Get the head SHA
    pr_json = run_gh([f"repos/{repo}/pulls/{pr_number}"])
    pr_data = json.loads(pr_json)
    head_sha = pr_data["head"]["sha"]

    # Fetch full contents of code files
    file_contents = {}
    for file_info in files_data:
        path = file_info["filename"]
        if file_info.get("status") == "removed":
            continue
        # Only fetch code files
        ext = os.path.splitext(path)[1]
        code_exts = {
            ".py", ".java", ".js", ".ts", ".cs", ".go", ".rs",
            ".swift", ".rb", ".php", ".cpp", ".h", ".kt", ".sh",
            ".yaml", ".yml", ".md", ".txt",
        }
        if ext not in code_exts:
            continue
        content_json = run_gh([
            f"repos/{repo}/contents/{path}?ref={head_sha}",
            "--jq", ".content",
        ])
        if content_json:
            try:
                import base64
                content = base64.b64decode(content_json.strip().strip('"')).decode("utf-8")
                file_contents[path] = content
            except Exception:
                pass

    return file_paths, file_contents


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
        if len(parts) >= 3 and parts[1] == "example_code":
            return parts[2]
        if len(parts) >= 3 and parts[1] == "scenarios":
            return parts[2]
        if len(parts) >= 2 and parts[0] in ("gov2", "dotnetv3", "dotnetv4"):
            return parts[1]
        if len(parts) >= 3 and parts[0] == "kotlin" and parts[1] == "services":
            return parts[2]
        if len(parts) >= 3 and parts[0] == "rustv1" and parts[1] == "examples":
            return parts[2]
    return None


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


def invoke_claude_for_fixes(
    bedrock_runtime, review_body, inline_comments, file_contents, comparables, guidelines
):
    """Send the fix request to Claude using streaming to avoid timeouts."""
    # Build the review feedback section
    feedback = f"## Review Summary\n{review_body}\n\n"
    if inline_comments:
        feedback += "## Inline Comments\n"
        for comment in inline_comments:
            path = comment.get("path", "")
            line = comment.get("original_line") or comment.get("line", "?")
            body = comment.get("body", "")
            feedback += f"- **{path}:{line}**: {body}\n"

    # Build the current files section (limit per-file size)
    files_section = "## Current File Contents\n\n"
    for path, content in file_contents.items():
        if len(content) > 10000:
            content = content[:10000] + "\n... [truncated]"
        files_section += f"### {path}\n```\n{content}\n```\n\n"

    user_message = f"""{feedback}

{files_section}

## Comparable Examples from Knowledge Base
{comparables[:20000]}

## Coding Guidelines
{guidelines[:5000]}
"""

    response = bedrock_runtime.invoke_model_with_response_stream(
        modelId=MODEL_ID,
        body=json.dumps(
            {
                "anthropic_version": "bedrock-2023-05-31",
                "max_tokens": 16384,
                "system": FIX_PROMPT,
                "messages": [{"role": "user", "content": user_message}],
            }
        ),
        contentType="application/json",
    )

    # Collect streamed response chunks
    result_text = ""
    for event in response["body"]:
        chunk = json.loads(event["chunk"]["bytes"])
        if chunk.get("type") == "content_block_delta":
            delta = chunk.get("delta", {})
            if delta.get("type") == "text_delta":
                result_text += delta.get("text", "")

    return result_text


def parse_fix_response(response_text):
    """Parse Claude's JSON response into structured fix data."""
    try:
        if "```json" in response_text:
            start = response_text.index("```json") + 7
            end = response_text.index("```", start)
            response_text = response_text[start:end].strip()
        elif "```" in response_text:
            start = response_text.index("```") + 3
            end = response_text.index("```", start)
            response_text = response_text[start:end].strip()
        return json.loads(response_text)
    except (json.JSONDecodeError, ValueError) as e:
        print(f"Warning: Could not parse fix response as JSON: {e}")
        # Try to salvage partial JSON by extracting complete suggestion objects
        suggestions = []
        try:
            # Find all complete suggestion objects in the truncated response
            import re
            # Match complete JSON objects within the suggestions array
            pattern = r'\{\s*"path"\s*:\s*"[^"]+"\s*,\s*"start_line"\s*:\s*\d+\s*,\s*"end_line"\s*:\s*\d+\s*,\s*"replacement"\s*:\s*"(?:[^"\\]|\\.)*"\s*,\s*"comment"\s*:\s*"(?:[^"\\]|\\.)*"\s*\}'
            matches = re.findall(pattern, response_text)
            for match in matches:
                try:
                    suggestions.append(json.loads(match))
                except json.JSONDecodeError:
                    continue
        except Exception:
            pass

        if suggestions:
            print(f"  Salvaged {len(suggestions)} suggestion(s) from partial response")
            return {"suggestions": suggestions, "new_files": []}

        print("  Could not salvage any suggestions from response.")
        print(f"  Response length: {len(response_text)} chars")
        print(f"  First 500 chars: {response_text[:500]}")
        return {"suggestions": [], "new_files": []}


def post_suggestions(repo, pr_number, suggestions, new_files, file_contents):
    """Post suggestions as a GitHub review with suggested changes."""
    # Get the current head SHA for the review
    pr_json = run_gh([f"repos/{repo}/pulls/{pr_number}"])
    pr_data = json.loads(pr_json)
    head_sha = pr_data["head"]["sha"]

    comments = []
    for suggestion in suggestions:
        path = suggestion.get("path", "")
        start_line = suggestion.get("start_line")
        end_line = suggestion.get("end_line")
        replacement = suggestion.get("replacement", "")
        comment_text = suggestion.get("comment", "")

        # Validate the path exists in the PR
        if path not in file_contents:
            print(f"  Skipping suggestion for {path} (not in PR files)")
            continue

        # Build GitHub suggestion syntax
        suggestion_body = f"🤖 **Suggested fix:** {comment_text}\n\n"
        suggestion_body += f"```suggestion\n{replacement}\n```"

        comment_obj = {
            "path": path,
            "body": suggestion_body,
            "side": "RIGHT",
        }

        # Single-line vs multi-line
        if start_line == end_line:
            comment_obj["line"] = end_line
        else:
            comment_obj["start_line"] = start_line
            comment_obj["line"] = end_line

        comments.append(comment_obj)

    # Build body with new file suggestions (can't be GitHub suggestions)
    body = "## 🤖 AI Suggested Fixes\n\n"
    body += "The following suggestions address the review feedback using the same "
    body += "knowledge base context as the reviewer.\n\n"

    if new_files:
        body += "### New Files Needed\n\n"
        for new_file in new_files:
            path = new_file.get("path", "")
            content = new_file.get("content", "")
            comment_text = new_file.get("comment", "")
            body += f"**Create `{path}`** — {comment_text}\n\n"
            body += f"<details><summary>Click to expand file content</summary>\n\n"
            body += f"```\n{content}\n```\n\n</details>\n\n"

    if not comments and not new_files:
        print("No actionable suggestions to post.")
        return

    body += f"\n---\n<sub>Generated using the same Bedrock KBs as the reviewer. "
    body += "Accept suggestions individually or batch them.</sub>"

    # Post as a review with comments
    payload = {
        "event": "COMMENT",
        "body": body,
        "commit_id": head_sha,
    }
    if comments:
        payload["comments"] = comments

    payload_json = json.dumps(payload)

    result = subprocess.run(
        [
            "gh", "api",
            f"repos/{repo}/pulls/{pr_number}/reviews",
            "--method", "POST",
            "--input", "-",
        ],
        input=payload_json,
        capture_output=True,
        text=True,
    )

    if result.returncode != 0:
        print(f"Error posting review: {result.stderr}")
        # If the review with inline comments fails, try posting body-only
        if comments:
            print("Retrying without inline comments...")
            payload.pop("comments", None)
            payload_json = json.dumps(payload)
            result = subprocess.run(
                [
                    "gh", "api",
                    f"repos/{repo}/pulls/{pr_number}/reviews",
                    "--method", "POST",
                    "--input", "-",
                ],
                input=payload_json,
                capture_output=True,
                text=True,
            )
            if result.returncode != 0:
                print(f"Error posting body-only review: {result.stderr}")
            else:
                print("Posted review (body only, inline comments failed).")
    else:
        print(f"Posted review with {len(comments)} suggestion(s).")


def has_recent_fix_suggestion(repo, pr_number):
    """Check if we've already posted a fix suggestion for the latest review."""
    reviews_json = run_gh([
        f"repos/{repo}/pulls/{pr_number}/reviews",
        "--paginate",
    ])
    if not reviews_json:
        return False

    reviews = json.loads(reviews_json)

    # Find positions of latest AI review and latest fix suggestion
    last_review_idx = -1
    last_fix_idx = -1
    for i, review in enumerate(reviews):
        body = review.get("body", "")
        if "🤖 AI Code Example Review" in body:
            last_review_idx = i
        if "🤖 AI Suggested Fixes" in body:
            last_fix_idx = i

    # If we already posted a fix after the latest review, skip
    if last_fix_idx > last_review_idx:
        return True
    return False


def main():
    repo = os.environ.get("GITHUB_REPOSITORY", "")
    pr_number = os.environ.get("PR_NUMBER", "")

    if not repo or not pr_number:
        print("Error: GITHUB_REPOSITORY and PR_NUMBER must be set.")
        sys.exit(1)

    print(f"Generating fix suggestions for PR #{pr_number} in {repo}")

    # Check if we already posted suggestions for the latest review
    if has_recent_fix_suggestion(repo, pr_number):
        print("Fix suggestions already posted for the latest review. Exiting.")
        sys.exit(0)

    # 1. Fetch the latest AI review
    print("Fetching latest AI review...")
    review_body, inline_comments = fetch_latest_ai_review(repo, pr_number)
    if not review_body:
        print("No AI review found on this PR. Exiting.")
        sys.exit(0)

    print(f"  Found review with {len(inline_comments or [])} inline comment(s)")

    # 2. Fetch PR files and contents
    print("Fetching PR files...")
    file_paths, file_contents = fetch_pr_files(repo, pr_number)
    print(f"  Loaded {len(file_contents)} file(s)")

    if not file_contents:
        print("No file contents to fix. Exiting.")
        sys.exit(0)

    # 3. Detect language and service
    language = detect_language(file_paths)
    service = detect_service(file_paths)
    print(f"  Language: {language}, Service: {service}")

    # 4. Query the same KBs as the reviewer
    bedrock_agent_runtime = boto3.client("bedrock-agent-runtime", region_name=REGION)
    bedrock_runtime = boto3.client("bedrock-runtime", region_name=REGION, config=BEDROCK_CONFIG)

    comparables = "No comparable examples found."
    if language:
        kb_id = LANGUAGE_KB_IDS.get(language)
        if kb_id:
            print(f"Retrieving comparables from {language} KB ({kb_id})...")
            comparables = retrieve_comparables(
                bedrock_agent_runtime, kb_id, language, service
            )

    guidelines = ""
    if CODING_STANDARDS_KB_ID:
        print("Retrieving coding guidelines...")
        guidelines = retrieve_guidelines(bedrock_agent_runtime, CODING_STANDARDS_KB_ID)

    if STEERING_DOCS_KB_ID:
        print("Retrieving steering docs...")
        steering = retrieve_guidelines(bedrock_agent_runtime, STEERING_DOCS_KB_ID)
        if steering:
            guidelines += "\n\n" + steering

    # 5. Invoke Claude for fixes
    print("Generating fixes...")
    response_text = invoke_claude_for_fixes(
        bedrock_runtime,
        review_body,
        inline_comments,
        file_contents,
        comparables,
        guidelines,
    )

    # 6. Parse response
    fix_data = parse_fix_response(response_text)
    suggestions = fix_data.get("suggestions", [])
    new_files = fix_data.get("new_files", [])
    print(f"  Generated {len(suggestions)} suggestion(s) and {len(new_files)} new file(s)")

    if not suggestions and not new_files:
        print("No fixes needed. Exiting.")
        sys.exit(0)

    # 7. Post suggestions
    print("Posting suggestions...")
    post_suggestions(repo, pr_number, suggestions, new_files, file_contents)
    print("Done.")


if __name__ == "__main__":
    main()
