import 'dart:convert';
import 'package:flutter/services.dart' show rootBundle;

class Config {
  String userPoolID;
  String clientID;

  Config(this.userPoolID, this.clientID);
}

Future<Config> loadConfig() async {
  final configString = await rootBundle.loadString('assets/config.json');
  final Map<String, dynamic> config = json.decode(configString);

  return Config(config['UserPoolID'], config['ClientID']);
}
