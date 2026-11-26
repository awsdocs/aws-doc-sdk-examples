# Knowledge base sync

Some of the content in this repository is copied into Amazon Bedrock knowledge
bases (KBs). The code example generation agents and the PR review scripts in
`.tools/scripts/` retrieve from these KBs, so the content here shapes what the
agents generate and what reviewers are compared against.

To change what a KB holds, change its source in this repository and merge the
change to `main`. You don't need to edit the KBs or their S3 buckets directly.

## What gets synced

| KB | Source | S3 bucket |
|---|---|---|
| steering-docs | Everything under `steering_docs/` | `codeloom-steering-docs-codeloomdev` |
| final-specs | Every `SPECIFICATION.md` under `scenarios/` | `codeloom-final-specs-codeloomdev` |
| coding-standards | Pages in this repository's GitHub wiki whose names contain `Standards` or `Guidelines` | `codeloom-coding-standards-codeloomdev` |
| python-premium | Paths listed in `python/premium-ex.md` | `codeloom-python-premium-codeloomdev` |
| java-premium | Paths listed in `javav2/premium-ex.md` | `codeloom-java-premium-codeloomdev` |
| dotnet-premium | Paths listed in `dotnetv4/premium-ex.md` | `codeloom-dotnet-premium-codeloomdev` |

The workflow also syncs the `premium-ex.md` paths for javascriptv3, gov2,
rustv1, swift, ruby, php, cpp and kotlin to their own
`codeloom-<language>-premium-codeloomdev` buckets. These languages don't have a
KB yet, so their content is uploaded but not indexed. To change that, see
[Adding a KB for a new language](#things-to-know).

## How it works

Two GitHub Actions workflows handle the sync.

- **`.github/workflows/sync-S3-KB.yml`** runs on every push to `main`. It runs
  one job per target. Each job collects the source files, mirrors them to the
  target's S3 bucket with `aws s3 sync --delete` (some deletions aren't
  applied for the premium KBs, see [Things to know](#things-to-know)), and then
  invokes the `KB_Updater` Lambda function.
- **`.tools/lambda/KB_Updater/lambda_function.py`** maps the target to a KB ID
  (`KB_IDS`), starts a Bedrock ingestion job for that KB's S3 data source, and
  waits for the job to finish. `.github/workflows/KB_Updater.yml` deploys the
  function when `lambda_function.py` changes on `main`.

Both workflows can also be run manually from the **Actions** tab
(`workflow_dispatch`). For the sync workflow, choose a single target.

## How to change KB content

- **Steering docs and specs:** edit the files, open a PR, and merge it. The KB
  is updated when that push's **syncS3andKB** run finishes.
- **Premium examples:** to change an example's code, edit the example and
  merge. To add, remove or swap an example, edit the paths in
  `<sdk>/premium-ex.md`. Paths go under one of three headings: `## basics:`,
  `## feature-scenario:` or `## complex-feature-scenario:`. Each path starts
  with `/` and is relative to the SDK folder. End the file with a newline.
  The heading names are case-sensitive, and the colon is required. A heading
  with any other text after the colon isn't recognized, and its paths are
  ignored.
- **Coding standards:** edit the wiki page. Wiki edits don't trigger the
  workflow, so the KB picks up the change on the next push to `main`. To
  update it sooner, run **syncS3andKB** manually with `coding-standards`.

## Things to know

- **Every file under `steering_docs/` is synced,** including READMEs and
  notes. Don't put files there that you don't want in the KB. The same applies
  to wiki pages: any page whose name contains `Standards` or `Guidelines` is
  synced.
- **Some deletions aren't applied.** For the premium KBs, `--delete` runs only
  inside `basics/`, `feature-scenario/` and `complex-feature-scenario/`. Files
  outside those folders stay in the bucket. If you remove every path under a
  heading in `premium-ex.md`, that level is skipped, so its old content also
  stays. Removing that content needs a manual cleanup of the bucket.
- **A green run doesn't mean the KB updated.** The job passes as long as the
  Lambda function can be invoked. A missing KB (`statusCode` 400), an ingestion
  job that ends `FAILED`, or a job that is still running when the function
  stops waiting (about 8 minutes, reported as `UNKNOWN`) appears only in the
  response printed by the **Sync Knowledge Base Data Source** step. If a
  change doesn't show up in the KB, check that output: `statusCode` is 200 even
  when ingestion fails, so look at `status` under `ingestion_job` in `body`.
- **Adding a KB for a new language** means creating the KB, then adding its
  ID to `KB_IDS` in `lambda_function.py`. On the next sync, the Lambda function
  creates the S3 data source for the language's bucket if the KB doesn't have
  one yet.
