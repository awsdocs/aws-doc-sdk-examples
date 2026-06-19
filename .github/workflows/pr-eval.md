---
name: Code Examples PR Review

on:
  pull_request:
    types: [opened, synchronize]

permissions:
  contents: read
  copilot-requests: write

safe-outputs:
  add-comment: null

---

# PR Checklist evaluation
  
Review the changes in this PR, and write a comment if the PR includes:
  
- Mocks in integration tests.
- Hard-coded bucket names or regions for AWS resources.
- Emojis in any code.
- List operations without proper pagination.
  
