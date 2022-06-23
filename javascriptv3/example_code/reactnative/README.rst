
Prerequisites
===================
1. Make sure npm and node is intalled.
2. Init react native environment.  :code:`npx react-native init YOUR_PROJECT_NAME`


Running the Example
===================
1. Replace your :code:`App.js` and :code:`package.json` with the example.
2. Change :code:`region` and :code:`indentityPoolId` in :code:`App.js` according to comment.
3. Change :code:`name` in :code:`package.json` if needed.
4. Install all packages for project. 

  :code:`npm install`

5. For IOS, we need to link `react-native-get-random-value` to native modules.

  :code:`cd ios`

  :code:`pod install`

6. Run the app in an IOS simulator.

  :code:`npx react-native run-ios`


Tips
===================
1. Actually we can install all packages of lastest version except for :code:`react-native-get-random-values`. Version :code:`@1.7.0` have issues when connecting with Native Modules so I change it to :code:`@1.6.0`. 
2. When importing :code:`react-native-url-poly-fill`. Keep it after  :code:`react-native-get-random-values` is imported.
