# Sample Flutter App for Integrating with AWS Cognito and implementing Authentication Functionality.

## Prerequisite
  - Install Android Studio or the Android Command-Line Tools from [https://developer.android.com/](https://developer.android.com/studio/install).
  - Install Flutter from [https://docs.flutter.dev/get-started](https://docs.flutter.dev/get-started/install/macos/mobile-android?tab=download).
  - Optionally install IDE with Flutter Plugin for [Visual Studio Code](https://docs.flutter.dev/tools/vs-code), [Android Studio](https://docs.flutter.dev/tools/android-studio) or [IntelliJ IDEA](https://plugins.jetbrains.com/plugin/9212-flutter).
  - Accept Licences using `flutter doctor --android-licenses`.
  - Verify Flutter Installation using `flutter doctor -v`.
  - Add Android emulator using `flutter emulators --create --name android-device`.
  - Launch Android emulator using `flutter emulators --launch android-device`

## Step-1: Set Up AWS Cognito User Pool and App Client ID
  - Log in to the AWS Management Console.
  - Navigate to Amazon Cognito and select "Manage User Pools."
  - Click "Create a user pool," name it, and follow the prompts to configure settings. 
  - Set up an App Client if you haven't already.
  - Make sure to note your Pool ID and App Client ID (without a client secret).

## Step 2: Add Dependencies
  - Add dependency for AWS cognito by executing `flutter pub add amazon_cognito_identity_dart_2` in your flutter project.
  - Add dependency for Secure storage by executing `flutter pub add flutter_secure_storage` in your flutter project.
  - Verify these dependencies to your package's `pubspec.yaml`.

## Step 3: Initialize Cognito
Update `assets/config.json` and initialize your Cognito user pool with your Pool ID and App Client ID:
```
{
    "UserPoolID": "<<YOUR USER POOL ID>>",
    "ClientID": "<< YOUR CLIENT ID>>"
}
```  

## Step 4: Integrate with AWS Cognito APIs
See `cognito_manager.dart` for an example of integrating with AWS Cognito, e.g.,,

```
import 'package:amazon_cognito_identity_dart_2/cognito.dart';
import 'config.dart';

class CognitoServiceException implements Exception {
  final String message;
  CognitoServiceException(this.message);
}

class User {
  String username;
  bool userConfirmed;
  bool sessionValid;
  String? userSub;
  Map<String, dynamic> claims;

  User(this.username, this.userConfirmed, this.sessionValid, this.userSub,
      this.claims);
}

class CognitoManager {
  late final CognitoUserPool userPool;

  CognitoManager();

  Future<void> init() async {
    final config = await loadConfig();
    userPool = CognitoUserPool(config.userPoolID, config.clientID);
  }

  Future<User> signUp(String email, String password) async {
    final userAttributes = [
      AttributeArg(name: 'email', value: email),
      // Add other attributes as needed
    ];

    try {
      final result = await userPool.signUp(email, password,
          userAttributes: userAttributes);
      return User(
          email, result.userConfirmed ?? false, false, result.userSub, {});
    } catch (e) {
      throw CognitoServiceException(e.toString());
    }
  }

  Future<bool> confirmUser(String email, String confirmationCode) async {
    final cognitoUser = CognitoUser(email, userPool);
    try {
      return await cognitoUser.confirmRegistration(confirmationCode);
    } catch (e) {
      throw CognitoServiceException(e.toString());
    }
  }

  Future<User> signIn(String email, String password) async {
    final cognitoUser = CognitoUser(email, userPool);
    final authDetails =
        AuthenticationDetails(username: email, password: password);

    try {
      final session = await cognitoUser.authenticateUser(authDetails);
      if (session == null) {
        throw CognitoClientException("session not found");
      }
      var claims = <String, dynamic>{};
      claims.addAll(session.idToken.payload);
      claims.addAll(session.accessToken.payload);
      return User(email, true, session.isValid(),
          session.idToken.getSub() ?? "", claims);
    } catch (e) {
      throw CognitoServiceException(e.toString());
    }
  }
}
```  

## Step 4: Implementing UI
See `main.dart` for a sample UI to implement sign up or sign in functionality, e.g.,
```
import 'package:flutter/material.dart';
import 'cognito_manager.dart';

...

class SignUpView extends StatefulWidget {
  @override
  _SignUpViewState createState() => _SignUpViewState();
}

class _SignUpViewState extends State<SignUpView> {
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  late final CognitoManager _cognitoManager;

  @override
  void initState() {
    super.initState();
    _cognitoManager = CognitoManager();
    _initCognitoManager();
  }

  Future<void> _initCognitoManager() async {
    await _cognitoManager.init();
  }

  void _signUp() async {
    final email = _emailController.text;
    final password = _passwordController.text;

    try {
      await _cognitoManager.signUp(email, password);
      DefaultTabController.of(context).animateTo(1);
    } on CognitoServiceException catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(e.message)),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Sign Up')),
      body: Padding(
        padding: const EdgeInsets.all(8.0),
        child: Column(
          children: [
            TextField(
              controller: _emailController,
              decoration: const InputDecoration(labelText: 'Email'),
            ),
            TextField(
              controller: _passwordController,
              decoration: const InputDecoration(labelText: 'Password'),
              obscureText: true,
            ),
            ElevatedButton(
              onPressed: _signUp,
              child: const Text('Sign Up'),
            ),
          ],
        ),
      ),
    );
  }
}

...
```
## Step 5: Run Application
Launch the application using `flutter run`.

## Resources
- [AWS Cognito](https://aws.amazon.com/cognito/)
- [Flutter Getting Started](https://docs.flutter.dev/get-started/codelab)
- [Flutter Cookbook](https://docs.flutter.dev/cookbook)
- [Flutter Documentation](https://docs.flutter.dev/)
