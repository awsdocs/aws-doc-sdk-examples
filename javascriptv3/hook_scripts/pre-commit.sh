#!/bin/bash

set -e

#!/bin/bash
echo $(pwd) 

# Lint
npm run lint

# Rather than running `test.sh` directly, control
# is left with the package configuration.
# npm test