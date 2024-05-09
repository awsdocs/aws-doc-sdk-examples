# AWS Image Management Scripts

This directory contains scripts for managing Docker images with Amazon ECR (Elastic Container Registry).

## 1. **upload_image.sh**

Uploads a Docker image to AWS ECR.
**Usage**:

```
./upload_image.sh <image-name>
```

**Prerequisites**:

- Docker must be installed and running.
- AWS CLI must be configured via `aws configure` or equivalent command.
- Set the required environment variables for the `upload_image.sh` script:
  ```
  export REGISTRY_ACCOUNT=your-account-id
  export AWS_REGION=your-region
  ```
- The Dockerfile for the image should be located relative to the script path as `../../../<image-name>/Dockerfile`.

## 2. **delete_image.py**

Deletes all ECR repositories containing the keyword 'examples' in their names.

**Usage**:

```
python delete_image.py
```

**Prerequisites**:

- Python and Boto3 library must be installed.
- AWS CLI must be configured via `aws configure` or equivalent command.
