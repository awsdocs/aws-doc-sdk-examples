import subprocess
import os
import sys
import re
import time
import uuid
from datetime import datetime

import boto3
from botocore.exceptions import ClientError

# Configuration
GIT_REPO = "https://github.com/awsdocs/aws-doc-sdk-examples.git"
CLONE_DIR = "/app/aws-doc-sdk-examples"
ROOT_TEST_DIR = "javav2/example_code"

# Initialize DynamoDB client
# dynamodb = boto3.resource("dynamodb", region_name="us-east-1")
FAILURE_TABLE = "TestFailures"
SUMMARY_TABLE = "TestRunSummaries"

def run_command(cmd, cwd=None):
#     print(f"Running: {' '.join(cmd)}")
    result = subprocess.run(cmd, cwd=cwd, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
#     print(result.stdout)
    return result.returncode, result.stdout

def clone_repo():
    if os.path.exists(CLONE_DIR):
#         print("Repo already cloned.")
        return
    result = run_command(["git", "clone", GIT_REPO, CLONE_DIR])
    if result[0] != 0:
        sys.exit("❌ Failed to clone repo.")

def parse_test_results(output):
    passed = failed = skipped = 0
    match = re.search(r"Tests run: (\d+), Failures: (\d+), Errors: (\d+), Skipped: (\d+)", output)
    if match:
        tests_run = int(match.group(1))
        failures = int(match.group(2))
        errors = int(match.group(3))
        skipped = int(match.group(4))
        passed = tests_run - (failures + errors + skipped)
    return passed, failures + errors, skipped

def has_integration_tests(path):
    for root, _, files in os.walk(path):
        for file in files:
            if file.endswith(".java"):
                with open(os.path.join(root, file), encoding="utf-8") as f:
                    content = f.read()
                    if "@Test" in content and '@Tag("IntegrationTest")' in content:
                        return True
    return False

def log_failure_to_dynamodb(service_name, test_path, output, run_id):
#     table = dynamodb.Table(FAILURE_TABLE)
    timestamp = datetime.utcnow().isoformat()

    # Extract failure/error details
    error_summary = "Unknown error"
    match = re.search(r"(Tests run: .*?)(\n.*?at .*?Exception.*?)\n", output, re.DOTALL)
    if match:
        error_summary = match.group(2).strip()
    else:
        error_lines = [line for line in output.splitlines() if "Exception" in line or "FAILURE" in line]
        if error_lines:
            error_summary = error_lines[-1].strip()

    try:
        pass
#         table.put_item(
#             Item={
#                 "RunId": run_id,
#                 "ServiceName": service_name,
#                 "Timestamp": timestamp,
#                 "TestPath": test_path,
#                 "ErrorSummary": error_summary
#             }
#         )
#         print(f"🗃️ Logged failure for {service_name} to DynamoDB with RunId: {run_id}.")
    except ClientError as e:
        print(f"❗ Failed to log test failure: {e}")

def run_maven_tests(service_path, run_id):
#     print(f"\n🔧 Testing: {service_path}")
    returncode, output = run_command(["mvn", "clean", "test", "-Dgroups=IntegrationTest"], cwd=service_path)
    passed, failed, skipped = parse_test_results(output)
    status = "✅ Passed" if failed == 0 else "❌ Failed"
#     print(f"📊 Result: {status} — Passed: {passed}, Failed: {failed}, Skipped: {skipped}")

    if failed > 0:
        service_name = os.path.basename(service_path)
        log_failure_to_dynamodb(service_name, service_path, output, run_id)

    return passed, failed, skipped

def log_test_run_summary(run_id, tested_services, total_passed, total_failed, total_skipped, elapsed_seconds):
#     summary_table = dynamodb.Table(SUMMARY_TABLE)
    timestamp = datetime.utcnow().isoformat()

    try:
        pass
#         summary_table.put_item(
#             Item={
#                 "RunId": run_id,
#                 "Timestamp": timestamp,
#                 "ServicesTested": tested_services,
#                 "TotalPassed": total_passed,
#                 "TotalFailed": total_failed,
#                 "TotalSkipped": total_skipped,
#                 "TotalTimeSeconds": int(elapsed_seconds),
#                 "Language": "Java"
#             }
#         )
#         print(f"🗃️ Test run summary logged to DynamoDB with RunId: {run_id}")
    except ClientError as e:
        pass
#         print(f"❗ Failed to log test summary: {e}")

def write_language_test_stats(language: str, total_tests: int, passed_tests: int):
    """
    Write test statistics to the LanguageTestStats DynamoDB table.
    
    Parameters:
        language (str): Programming language name (e.g., "Kotlin").
        total_tests (int): Total number of tests run.
        passed_tests (int): Number of tests that passed.
    """
    pass_rate = (passed_tests / total_tests) * 100 if total_tests > 0 else 0

#     dynamodb = boto3.client("dynamodb")

    item = {
        'Language': {'S': language},
        'TotalTests': {'N': str(total_tests)},
        'PassedTests': {'N': str(passed_tests)},
        'PassRate': {'N': str(round(pass_rate, 2))}
    }

#     try:
#         dynamodb.put_item(
#             TableName="LanguageTestStats",
#             Item=item
#         )
#         print(f"✅ Wrote stats for {language}: {passed_tests}/{total_tests} passed ({pass_rate:.2f}%)")
#     except ClientError as e:
#         print(f"❌ Failed to write stats for {language} to DynamoDB: {e}")


def main():
    clone_repo()

    total_passed = total_failed = total_skipped = 0
    tested_services = 0
    start_time = time.time()
    run_id = str(uuid.uuid4())  # Generate RunId at the beginning

    root_test_path = os.path.join(CLONE_DIR, ROOT_TEST_DIR)
    service_dirs = sorted(
        [os.path.join(root_test_path, d) for d in os.listdir(root_test_path) if os.path.isdir(os.path.join(root_test_path, d))],
        key=lambda path: os.path.basename(path).lower()
    )

    current = 0
    max = 1
    for service_path in service_dirs:
        if os.path.exists(os.path.join(service_path, "pom.xml")) and has_integration_tests(service_path):
            if current >= max:
                break
            current += 1
            passed, failed, skipped = run_maven_tests(service_path, run_id)
            total_passed += passed
            total_failed += failed
            total_skipped += skipped
            tested_services += 1
    elapsed = time.time() - start_time
#     print("\n===== ✅ Final Test Summary =====")
#     print(f"Services Tested: {tested_services}")
#     print(f"Total Tests Passed: {total_passed}")
#     print(f"Total Tests Failed: {total_failed}")
#     print(f"Total Tests Skipped: {total_skipped}")
#     print(f"Total Time: {int(elapsed // 60)} min {int(elapsed % 60)} sec")

    print(
{
    "schema-version": "0.0.1",
    "results": {
        "tool": "gradle",
        "summary": {
            "tests": tested_services,
            "passed": total_passed,
            "failed": total_failed,
            "skipped": total_skipped,
            "start_time": 0,
            "stop_time": int(elapsed)
        },
        "tests": [
            {
                "name": "test 1",
                "status": "failed",
                "duration": int(elapsed),
                "message": "apigateway",
                "log": "probably something to do with credentials"
            }
        ]
    }
})

    # 🪪 Log to DynamoDB at the END of all testing
    log_test_run_summary(run_id, tested_services, total_passed, total_failed, total_skipped, elapsed)

    total =  total_passed + total_failed 
    write_language_test_stats("Java", total, total_passed)     

if __name__ == "__main__":
    main()
