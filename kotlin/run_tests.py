import os
import subprocess
import json
import datetime
import boto3
import time
import re
from botocore.exceptions import ClientError

# DynamoDB setup
# dynamodb = boto3.resource('dynamodb', region_name='us-east-1')
SUMMARY_TABLE = 'TestRunSummaries'  # Table for test run summaries

def run_command(command, cwd=None):
    """Run a shell command and return the returncode and output."""
    try:
        result = subprocess.run(command, cwd=cwd, check=True, text=True, capture_output=True)
        return result.returncode, result.stdout + result.stderr
    except subprocess.CalledProcessError as e:
        #print(f"Error running command: {e.cmd}\nReturn code: {e.returncode}\n{e.stdout or e.stderr}")
        return e.returncode, (e.stdout or '') + (e.stderr or '')

def parse_gradle_test_results(output):
    """Parse Gradle test results and return counts and failure summaries."""
    passed = failed = skipped = 0
    failure_summary_lines = []

    lines = output.splitlines()
    in_failure_block = False
    current_failure = ""
    
    for line in lines:
        stripped = line.strip()

        # Count test result types
        if re.match(r'.+ > .+ PASSED', stripped):
            passed += 1
        elif re.match(r'.+ > .+ FAILED', stripped):
            failed += 1
            in_failure_block = True
            failure_summary_lines.append(f"Failed: {stripped}")
        elif re.match(r'.+ > .+ SKIPPED', stripped):
            skipped += 1

        # Capture stack trace or explanation for the failure
        if in_failure_block:
            if stripped == '' or stripped.startswith('BUILD') or re.match(r'^\d+\) ', stripped):
                in_failure_block = False
            else:
                failure_summary_lines.append(line)

        # Capture the test class and method that failed
        match = re.match(r'(.+) \((.+)\)', stripped)  # Match "TestClass (testMethod)"
        if match:
            failure_details = f"Test Failed: {match.group(1)} - Method: {match.group(2)}"
            if failure_details not in failure_summary_lines:
                failure_summary_lines.append(failure_details)

    failure_summary = "\n".join(failure_summary_lines) if failure_summary_lines else "No failure details found."
    return passed, failed, skipped, failure_summary

def log_failure_to_dynamodb(service_name, service_path, failure_summary, run_id):
    """Log failed test results to DynamoDB."""
#     client = boto3.client('dynamodb', region_name='us-east-1')
    table_name = 'TestFailures'
    try:
        pass
#         client.put_item(
#             TableName=table_name,
#             Item={
#                 'ServiceName': {'S': service_name},
#                 'ServicePath': {'S': service_path},
#                 'ErrorSummary': {'S': failure_summary},
#                 'RunId': {'S': run_id},
#                 'Timestamp': {'S': str(datetime.datetime.utcnow())}
#             }
#         )
        #print(f"🛑 Logged failure to DynamoDB: {service_name}")
    except ClientError as e:
        pass
        #print(f"❗ Failed to log failure: {e}")

def has_test_annotations(service_path):
    """Check if the service contains any test files with @Test annotation."""
    test_dirs = [
        os.path.join(service_path, "src", "test", "kotlin"),
        os.path.join(service_path, "src", "test", "java")
    ]
    for test_dir in test_dirs:
        for root, _, files in os.walk(test_dir):
            for file in files:
                if file.endswith(".kt") or file.endswith(".java"):
                    full_path = os.path.join(root, file)
                    try:
                        with open(full_path, 'r', encoding='utf-8') as f:
                            content = f.read()
                            if "@Test" in content:
                                return True
                    except Exception as e:
                        pass
                        #print(f"⚠️ Could not read {full_path}: {e}")
    return False

def run_gradle_tests(service_path, run_id):
    """Run Gradle tests for a given service if test files with @Test exist."""
    #print(f"\n🔧 Testing: {service_path}")

    if not has_test_annotations(service_path):
        #print(f"⚠️ No test files with @Test found in {service_path}. Skipping.")
        return 0, 0, 0

    gradlew = os.path.join(service_path, "gradlew")
    if os.path.exists(gradlew):
        gradle_command = "./gradlew"
        os.chmod(gradlew, 0o775)
    else:
        build_file_path = os.path.join(service_path, "build.gradle.kts")
        if not os.path.isfile(build_file_path):
            #print(f"❌ No Gradle build file found in {service_path}. Skipping test for this service.")
            return 0, 0, 0
        gradle_command = "gradle"

    returncode, output = run_command([gradle_command, "test"], cwd=service_path)
    passed, failed, skipped, failure_summary = parse_gradle_test_results(output)
    status = "✅ Passed" if failed == 0 else "❌ Failed"
    #print(f"📊 Result: {status} — Passed: {passed}, Failed: {failed}, Skipped: {skipped}")

    if failed > 0:
        service_name = os.path.basename(service_path)
        # log_failure_to_dynamodb(service_name, service_path, failure_summary, run_id)

    return passed, failed, skipped

def get_service_folders(base_dir):
    """Get a list of all service directories sorted alphabetically."""
    service_folders = []
    for root, dirs, files in os.walk(base_dir):
        for dir_name in dirs:
            gradle_file = os.path.join(root, dir_name, 'build.gradle.kts')
            if os.path.isfile(gradle_file):
                service_folders.append(os.path.join(root, dir_name))
    return sorted(service_folders)

def run_tests_for_all_services(base_dir, run_id):
    """Run tests for all services in alphabetical order."""
    service_folders = get_service_folders(base_dir)
    passed_total = failed_total = skipped_total = 0
    services_tested = 0

    current = 0
    limit = 1
    for service in service_folders:
        if current >= limit:
            break
        current += 1
        passed, failed, skipped = run_gradle_tests(service, run_id)
        passed_total += passed
        failed_total += failed
        skipped_total += skipped
        services_tested += 1

    return passed_total, failed_total, skipped_total, services_tested

def log_test_run_summary(run_id, tested_services, total_passed, total_failed, total_skipped, elapsed_seconds):
#     summary_table = dynamodb.Table(SUMMARY_TABLE)
    timestamp = datetime.datetime.utcnow().isoformat()

    item = {
        "RunId": run_id,
        "Timestamp": timestamp,
        "ServicesTested": tested_services,
        "TotalPassed": total_passed,
        "TotalFailed": total_failed,
        "TotalSkipped": total_skipped,
        "TotalTimeSeconds": int(elapsed_seconds),
        "Language": "Kotlin"
    }

    try:
        pass
#         summary_table.put_item(Item=item)
        #print(f"🗃️ Test run summary logged to DynamoDB with RunId: {run_id}")
    except ClientError as e:
        pass
        #print(f"❗ Failed to log test summary: {e}")

def write_language_test_stats(language: str, total_tests: int, passed_tests: int):
    """
    Write test statistics to the LanguageTestStats DynamoDB table.
    
    Parameters:
        language (str): Programming language name (e.g., "Kotlin").
        total_tests (int): Total number of tests run.
        passed_tests (int): Number of tests that passed.
    """
    pass_rate = (passed_tests / total_tests) * 100 if total_tests > 0 else 0

#     dynamodb = boto3.client("dynamodb", "us-east-1")

    item = {
        'Language': {'S': language},
        'TotalTests': {'N': str(total_tests)},
        'PassedTests': {'N': str(passed_tests)},
        'PassRate': {'N': str(round(pass_rate, 2))}
    }

    try:
        pass
#         dynamodb.put_item(
#             TableName="LanguageTestStats",
#             Item=item
#         )
        #print(f"✅ Wrote stats for {language}: {passed_tests}/{total_tests} passed ({pass_rate:.2f}%)")
    except ClientError as e:
        pass
        #print(f"❌ Failed to write stats for {language} to DynamoDB: {e}")

def main():
    repo_url = "https://github.com/awsdocs/aws-doc-sdk-examples.git"
    clone_dir = "/app/aws-doc-sdk-examples"
    ROOT_TEST_DIR = "kotlin/services"
    start_time = time.time()

    if not os.path.exists(clone_dir):
        #print(f"📥 Cloning repo: {repo_url}")
        returncode, output = run_command(["git", "clone", repo_url, clone_dir])
        if returncode != 0:
            #print("❌ Failed to clone repository.")
            return

    base_dir = os.path.join(clone_dir, ROOT_TEST_DIR)
    run_id = str(int(time.time()))
    passed_total, failed_total, skipped_total, services_tested = run_tests_for_all_services(base_dir, run_id)

    elapsed = time.time() - start_time
#     #print("\n===== ✅ Final Test Summary =====")
    #print(f"Services Tested: {services_tested}", file=sys.stderr)
    #print(f"Total Tests Passed: {passed_total}")
    #print(f"Total Tests Failed: {failed_total}")
    #print(f"Total Tests Skipped: {skipped_total}")
    #print(f"Total Time: {int(elapsed // 60)} min {int(elapsed % 60)} sec")
    print(
{
    "schema-version": "0.0.1",
    "results": {
        "tool": "gradle",
        "summary": {
            "tests": services_tested,
            "passed": passed_total,
            "failed": failed_total,
            "skipped": skipped_total,
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
#    log_test_run_summary(run_id, services_tested, passed_total, failed_total, skipped_total, elapsed)

    total =  passed_total + failed_total 
    write_language_test_stats("Kotlin", total, passed_total)

if __name__ == "__main__":
    main()
