
Prerequisites
=============
1. Make sure npm and node is intalled.
2. Init react native environment. `npx react-native init YOUR_PROJECT_NAME`


Running the Example
=============
1. Replace your `App.js` and `package.json` with the example.
2. Change `region` and `indentityPoolId` in `App.js` according to comment.
3. Change `name` in `package.json` if needed.
4. Install all packages for project. 
`npm install`
5. For IOS, we need to link `react-native-get-random-value` to native modules.
`cd ios`
`pod install`
6. Run the app in an IOS simulator.
`npx react-native run-ios`

Tips
=============
1. Actually we can install all packages of lastest version except for `react-native-get-random-value`. Version `@1.7.0` have issues when connecting with Native Modules so I change it to `@1.6.0`. 
2. When importing `react-native-url-poly-fill`. Keep it after  `react-native-get-random-values` is imported.