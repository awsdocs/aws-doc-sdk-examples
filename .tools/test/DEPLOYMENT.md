# Deployment Instructions
There are two wats to deploy the code in this directory.

## 1. Deploy script
To deploy any stack in this directory, run the [deploy.py](stacks/deploy.py) script.

### Script Prerequisites
- Python 3.x installed
- Required Python packages: `argparse`, `subprocess`, `os`, `yaml`, `time`
- AWS CLI and CDK installed and configured
- Appropriate permissions to execute AWS and shell commands
- Configuration files `resources.yaml` and `targets.yaml` placed in a `config` directory within the same directory as the script

### Usage

#### Command Syntax
```bash
python deploy.py --type <deployment_type>
```
Replace `<deployment_type>` with one of the supported types:

- `admin`: Deploys admin-specific resources.
- `images`: Deploys image-related resources.
- `plugin`: Deploys plugin-specific resources.

#### Additional Notes
The script automatically navigates to the required directory based on the type and language of deployment (typescript is the default).

Environment variables are set and used during the deployment process.

Errors during command execution are caught and displayed.

The script includes a sleep period after deployment to avoid conflicts with simultaneous CDK operations.

Make sure to check the script's output for any errors or confirmation messages that indicate the deployment's success or failure. Adjust the config files as necessary to match your deployment requirements.

---

## 2. CDK invocation
The second option involves navigating to each stack directory and running the CDK commands.

The following instructions assume a "plugin account" (the AWS account where testing activities will occur) of "python" (corresponding to a Docker image) per [this repository's configuration](config/targets.yaml).
You can replace Python with any of the other languages listed in this repository's configuration.

To request an alternate configuration for your own repository or use case, please [submit an issue](https://github.com/awsdocs/aws-doc-sdk-examples/issues/new?labels=type%2Fenhancement&labels=Tools&title=%5BEnhancement%5D%3A+Weathertop+Customization+Request&&) with the `Tools` label.

### 1. Deploy Plugin Stack for your language (e.g. Python)
User will:
1. Set Python virtualenv within [plugin directory](plugin/admin).
1. `export LANAUGE_NAME=python`.
1. Get AWS account tokens for plugin account.
2. `cdk bootstrap` and `cdk deploy`.

### 2. Enable Consumer Stack to receive event notifications
User will:
1. Set `status` to `enabled` in [targets.yaml](config/targets.yaml) for your language
1. Raise PR.

Admin will:
1. Approve and merge PR.
1. Set Python virtualenv within [admin directory](stacks/admin).
1. Get Admin account tokens.
1. `cdk bootstrap` and `cdk deploy`.
1. Request that user [submit a test job](#3-submit-test-job).

### 3. Submit test job
User will:
1. Log into console for Python account
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
1. [Validate results of test job](#3-optional-view-test-job-results)

### 3. Optional: View CloudWatch job results in Batch
1. Navigate to a job
1. When status is `SUCCEEDED` or `FAILED`, click "Logging" tab.
![](docs/validation-flow-6.jpg)