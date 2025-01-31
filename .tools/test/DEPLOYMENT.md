# Deployment Instructions

This repository contains infrastructure deployment scripts for testing SDK example code. The infrastructure is managed through AWS CDK and can be deployed in two ways:
1. [deploy.py](#1-using-the-deploy-script)
2. [invoking the CDK directly](#2-invoking-cdk-directly)

## Option 1. Using `deploy.py`

The [deploy.py](stacks/deploy.py) script is the primary method for deploying the infrastructure stacks.
It exists in order to facilitate stack deployments to an infinite number of AWS accounts, without requiring the user to
fetch new tokens and set new variables for each deployment.

### Deployment types
The script handles three types of deployments:

1. **Images Stack** (`images`):
   - Creates empty ECR private repositories for all tools listed in [targets.yaml](stacks/config/targets.yaml)
   - Users must implement their own image versioning and pushing mechanism
   - Example: GitHub Actions with OIDC provider works well for this purpose

2. **Admin Stack** (`admin`):
   - Deploys event emission infrastructure
   - Creates IAM policies for cross-account event subscription
   - **Required**: Must be deployed before any plugin stacks
   - Works with single or multiple accounts listed in [targets.yaml](stacks/config/targets.yaml)

3. **Plugin Stack** (`plugin`):
   - Deploys two stacks to each account in [targets.yaml](stacks/config/targets.yaml):
     1. Plugin stack that subscribes to admin stack events
     2. Account nuker stack that cleans up residual test resources
   - Requires `admin` stack to be deployed first

### Environment
It is designed to run from the command line interface (CLI) on macOS or Linux systems. You can use the default terminal emulator on macOS, such as zsh or bash, or any other terminal emulator of your choice.

### Why subprocess?
The script uses Python's subprocess module to execute the AWS Cloud Development Kit (CDK) command-line interface (CLI) commands. While the CDK provides a Python CDK library, 1) we use the TypeScript version per team standard, and 2) that Python CDK library does not expose a way to invoke the script itself from within a Python script. As a consequence, we are stuck using the `subprocess` module to invoke the CDK CLI commands for our TypeScript stack.

### Script Prerequisites

- Command line interface (CLI) on macOS installed, such as zsh or bash
- Python 3.11 installed
- AWS CLI and CDK installed and configured (NodeJS 18+)
- Admin-like IAM permissions on the role assumed (`AdministratorAccess` will work for non-production test environments).
- Configuration files [resources.yaml](stacks/config/resources.yaml) and [targets.yaml](stacks/config/targets.yaml)
- Environment variables set for:
  - `TOKEN_TOOL`: Path to credential management tool
  - `TOKEN_PROVIDER`: Identity provider for AWS credentials
- Dependencies installed in Python virtual environment:
```
python -m venv .venv && source .venv/bin/activate && pip install -r requirements.txt``
```

### Note on EnvVars `TOKEN_TOOL` and `TOKEN_PROVIDER`
These environment variables are designed to partly obscure the tooling used by AWS.
The `get_tokens` function on [deploy.py#L167](stacks/deploy.py#L167) may require additional refactoring to comply with whatever token tool you are using.

### Usage

#### Command Syntax

```bash
cd stacks ; python deploy.py <stack-type>
```

Replace `<stack-type>` with one of the supported stacks:

- `admin`: Deploys admin-specific resources.
- `images`: Deploys image-related resources.
- `plugin`: Deploys plugin-specific resources.
  - To deploy only a specific language's plugin only, pass `--language <language>` where `<language>` is an account name in [targets.yaml](stacks/config/targets.yaml). E.g. `python`

## Technical Notes
This creates some brittleness but provides necessary flexibility for cross-account deployments

#### Additional Notes
Some non-obvious quirks of the script include:
 - programmatic file traversing to the required CDK directory based on the type and language of CDK deployment (`typescript` is the default).
 - a random-seeming sleep period after deployment to avoid conflicts with the previous CDK operation that may have not killed its thread yet.
 - more generally, extensive use of the `subprocess` module which creates some acceptable brittleness that may result in future regression.
---

## Option 2. Invoking CDK directly

This option involves navigating to each stack directory([images](stacks/images), [admin](stacks/admin), or [plugin](stacks/plugin)) and running the `cdk` commands explained below.

Required steps for all stack types:
1. Set Python virtualenv within [plugin directory](stacks/plugin/admin).
1. Get AWS account tokens for target account.
1. Run `cdk bootstrap` and `cdk deploy`.

### Special details for `plugin` type
For the `plugin` type, there are a few important details: 
1. User must also run `export LANGUAGE_NAME=python` if your tool is `python`.
1. For the stack to begin accepting test events, you must set `status` to `enabled` for your tool (e.g. `python`) in [targets.yaml](stacks/config/targets.yaml) and redeploy the `admin` stack.
1. To manually trigger test runs, [submit a test job](#submit-test-job) in AWS Batch.

## Testing & Validation
Users can trigger test runs from within the AWS Console after deploying the `plugin` stack for their chosen tool.

### Submit test job

Users can trigger test runs from within the AWS Console after deploying the `plugin` stack for their chosen tool.

Steps:
1. Log into console for tool AWS account (e.g. `python`)
1. Navigate to "Job Definitions".
   ![](docs/validation-flow-1.jpg)
1. Click "Submit Job".
   ![](docs/validation-flow-2.jpg)
1. Add name, select queue, and click "Next".
   ![](docs/validation-flow-3.jpg)
1. Click "Next".
   ![](docs/validation-flow-4.jpg)
1. Click "Create job".
   ![](docs/validation-flow-5.jpg)
1. [Validate results of test job](#view-test-run-results)

### View test run results
1. Log into console for tool AWS account (e.g. `python`)
1. Click `Jobs` and select the only job queue.
2. Toggle `Load all jobs`.
1. View job details by clicking the hyperlinked value in the `Name` field.
2. When status is `SUCCEEDED` or `FAILED`, click "Logging" tab.
   ![](docs/validation-flow-6.jpg)
