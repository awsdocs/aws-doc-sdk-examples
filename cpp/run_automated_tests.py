import sys
import getopt
import glob


# Script to run automated C++ tests.
#
# Types of automated tests.
# 1. Requires credentials and pre-configured resources.
# 2. Requires credentials and permissions.
# 3. Does not require credentials.


def build_tests(service="*"):
    files = glob.glob( f"example_code/{service}/tests/CMakeLists.txt")
    files.extend(glob.glob( f"example_code/{service}/gtests/CMakeLists.txt"))
    print(files)

    run_files = []

    if len (files) == 0:
        
    return [0, run_files]


def run_tests(run_files = [], type1=False, type2=False, type3=False):
    print("run tests")

    return 0


def main(argv):
    type1 = False
    type2 = False
    type3 = False
    service = "*"

    opts, args = getopt.getopt(argv, "h123s:")
    for opt, arg in opts:
        if opt == '-h':
            print('run_automated_tests.py -1 -2 -3 -s <service>')
            print('Where:')
            print(' 1. Requires credentials and pre-configured resources.')
            print(' 2. Requires credentials.')
            print(' 3. Does not require credentials.')
            print(' s. Test this service (regular expression).')
            sys.exit()
        elif opt in ("-1"):
            type1 = True
        elif opt in ("-2"):
            type2 = True
        elif opt in ("-3"):
            type3 = True
        elif opt in ("-s"):
            service = arg

    result = build_tests(service=service)

    if result == 0 :
        run_tests(type1=type1, type2=type2, type3=type3)


if __name__ == "__main__":
    main(sys.argv[1:])
