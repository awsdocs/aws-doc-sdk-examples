import argparse
import subprocess
import os
import yaml
import time


def run_shell_command(command, env_vars=None):
    """Run a shell command and return its output"""
    # Prepare the environment
    env = os.environ.copy()
    if env_vars:
        env.update(env_vars)

    print("COMMAND: " + command)
    try:
        output = subprocess.check_output(
            command, shell=True, stderr=subprocess.STDOUT, env=env
        )
        print(f"Command output: {output.decode()}")
    except subprocess.CalledProcessError as e:
        print(f"Error executing command: {e.output.decode()}")


def deploy_resources(account_id, account_name, dir, lang='typescript'):
    """Deploy resources to the given account"""
    if dir not in os.getcwd():
        os.chdir(f"{dir}/{lang}")
    # Get AWS tokens for the account
    get_tokens_command = f"ada credentials update --account {account_id} --provider isengard --role weathertop-cdk-deployments --once"
    run_shell_command(get_tokens_command)
    export_name_command = f"export TOOL_NAME={account_name}"
    run_shell_command(export_name_command)
    deploy_command = "cdk deploy --require-approval never"
    print(deploy_command)
    run_shell_command(deploy_command, env_vars={"TOOL_NAME": f"{account_name}"})
    # Be gentle and give CDK ps's a few moments to clear
    # Error you may see if you remove below line:
    #    Another CLI (PID=12345) is currently synthing to cdk.out.
    #    Invoke the CLI in sequence, or use '--output' to synth into different directories.
    time.sleep(15)


def main():
    parser = argparse.ArgumentParser(description="admin, images, or plugin flag.")
    parser.add_argument("--type", type=str, help="Either admin, images, or plugin")
    args = parser.parse_args()

    if "admin" in args.type or "images" in args.type:
        with open(".config/resources.yaml", "r") as file:
            data = yaml.safe_load(file)
        accounts = {
            "admin": {"account_id": f"{data['admin_acct']}", "status": "enabled"}
        }
    elif "plugin" in args.type:
        with open(".config/targets.yaml", "r") as file:
            accounts = yaml.safe_load(file)
    else:
        raise "Invalid parameter"

    for account_name, account_info in accounts.items():
        print(
            f"Reading from account {account_name} with ID {account_info['account_id']}"
        )
        deploy_resources(account_info["account_id"], account_name, args.type)


if __name__ == "__main__":
    main()
