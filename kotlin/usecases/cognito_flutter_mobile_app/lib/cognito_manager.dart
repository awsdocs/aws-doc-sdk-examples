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
