import 'package:flutter/material.dart';
import 'cognito_manager.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: DefaultTabController(
        length: 3,
        child: Scaffold(
          appBar: AppBar(
            title: const Text('Sample Cognito App'),
            bottom: const TabBar(
              tabs: [
                Tab(text: 'Sign-Up'),
                Tab(text: 'Confirm Sign-Up'),
                Tab(text: 'Sign-In'),
              ],
            ),
          ),
          body: TabBarView(
            children: [
              SignUpView(),
              ConfirmSignUpView(),
              SignInView(),
            ],
          ),
        ),
      ),
    );
  }
}

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

class ConfirmSignUpView extends StatefulWidget {
  @override
  _ConfirmSignUpViewState createState() => _ConfirmSignUpViewState();
}

class _ConfirmSignUpViewState extends State<ConfirmSignUpView> {
  final _emailController = TextEditingController();
  final _confirmationCodeController = TextEditingController();
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
    final confirmationCode = _confirmationCodeController.text;

    try {
      await _cognitoManager.confirmUser(email, confirmationCode);
      DefaultTabController.of(context).animateTo(2);
    } on CognitoServiceException catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(e.message)),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Sign-Up Confirmation')),
      body: Padding(
        padding: const EdgeInsets.all(8.0),
        child: Column(
          children: [
            TextField(
              controller: _emailController,
              decoration: const InputDecoration(labelText: 'Email'),
            ),
            TextField(
              controller: _confirmationCodeController,
              decoration: const InputDecoration(labelText: 'Confirmation Code'),
            ),
            ElevatedButton(
              onPressed: _signUp,
              child: const Text('Confirm Sign-Up'),
            ),
          ],
        ),
      ),
    );
  }
}

class SignInView extends StatefulWidget {
  @override
  _SignInViewState createState() => _SignInViewState();
}

class _SignInViewState extends State<SignInView> {
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

  void _signIn() async {
    final email = _emailController.text;
    final password = _passwordController.text;

    try {
      final user = await _cognitoManager.signIn(email, password);
      Navigator.push(
        context,
        MaterialPageRoute(builder: (context) => UserDetailsPage(user: user)),
      );
    } on CognitoServiceException catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(e.message)),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Sign In')),
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
              onPressed: _signIn,
              child: const Text('Sign In'),
            ),
          ],
        ),
      ),
    );
  }
}

class UserDetailsPage extends StatelessWidget {
  final User user;

  const UserDetailsPage({super.key, required this.user});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('User Details: ${user.username}')),
      body: Padding(
        padding: const EdgeInsets.all(8.0),
        child: ListView(
          children: [
            Text("Token Valid: ${user.sessionValid}"),
            ...user.claims.entries
                .map((entry) => Text('${entry.key}: ${entry.value}'))
                .toList(),
          ],
        ),
      ),
    );
  }
}
