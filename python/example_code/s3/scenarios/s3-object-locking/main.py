import argparse
from deploy import deploy_s3_object_locking
from demo import demo_s3_object_locking
from cleanup import clean_s3_object_locking

def main():
    parser = argparse.ArgumentParser(description="Amazon S3 Object Lock Workflow")
    parser.add_argument('-s', '--stage', choices=['deploy', 'demo', 'clean'], required=True, help="Stage to run: deploy, demo, clean")
    args = parser.parse_args()

    if args.stage == 'deploy':
        deploy_s3_object_locking()
    elif args.stage == 'demo':
        demo_s3_object_locking()
    elif args.stage == 'clean':
        clean_s3_object_locking()

if __name__ == "__main__":
    main()
