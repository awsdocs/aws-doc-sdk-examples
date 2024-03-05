# SoS editor
**This app is for internal AWS use**.


## Install Mac executable
1. In the command line at *./my-app*,  run *npm install*, then *npm install --save-dev @electron/packager*. 
2. For MacOS, install [Wine](https://www.winehq.org/) 1.6 via [Homebrew](https://brew.sh/).
3. Navigate to *./my-app/src.js/* and run *npx electron-packager*. 
4. In *./my-app/src/js/run.mjs* (line ~11), redefine the pathToExe to point to the location of your executable. 

For additional details/troubleshooting see https://github.com/electron/packager.

## Usage
When you have successfully created your executable:
1. Navigate to *./my-app/src/js/* and run *node run.mjs*.
2. Follow the instructions onscreen. 

Copyright Amazon.com, Inc. or its affiliates.
All Rights Reserved. SPDX-License-Identifier: Apache-2.0