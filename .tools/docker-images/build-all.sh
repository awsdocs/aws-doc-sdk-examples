#!/bin/bash
# Build and push all SDK example Docker images to ECR
#
# Usage:
#   ./build-all.sh                  # Build all images
#   ./build-all.sh python java      # Build specific images
#
# Prerequisites:
#   - AWS credentials with ECR push access
#   - Docker installed

set -euo pipefail

ACCOUNT_ID="801093629784"
REGION="us-east-1"
REGISTRY="${ACCOUNT_ID}.dkr.ecr.${REGION}.amazonaws.com"
REPO_PREFIX="sdk-examples"

ALL_LANGUAGES=(python java dotnet javascript go rust ruby php kotlin cpp swift)

# Use arguments if provided, otherwise build all
if [ $# -gt 0 ]; then
  LANGUAGES=("$@")
else
  LANGUAGES=("${ALL_LANGUAGES[@]}")
fi

echo "=== Authenticating to ECR ==="
aws ecr get-login-password --region "${REGION}" | \
  docker login --username AWS --password-stdin "${REGISTRY}"

for lang in "${LANGUAGES[@]}"; do
  REPO_NAME="${REPO_PREFIX}-${lang}"
  FULL_TAG="${REGISTRY}/${REPO_NAME}:latest"
  DATE_TAG="${REGISTRY}/${REPO_NAME}:$(date +%Y%m%d)"

  echo ""
  echo "=== Building ${lang} ==="

  # Ensure ECR repository exists
  aws ecr describe-repositories --repository-names "${REPO_NAME}" --region "${REGION}" 2>/dev/null || \
    aws ecr create-repository --repository-name "${REPO_NAME}" --region "${REGION}" \
      --image-scanning-configuration scanOnPush=true

  # Build
  docker build -t "${FULL_TAG}" -t "${DATE_TAG}" "${lang}/"

  # Push both tags
  echo "  Pushing ${FULL_TAG}"
  docker push "${FULL_TAG}"
  echo "  Pushing ${DATE_TAG}"
  docker push "${DATE_TAG}"

  echo "  ✅ ${lang} done"
done

echo ""
echo "=== All images built and pushed ==="
