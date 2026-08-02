import 'package:flutter/material.dart';

void main() => runApp(const MpsApp());

class MpsApp extends StatelessWidget {
  const MpsApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'My Publisher Scheduler',
      theme: ThemeData(colorSchemeSeed: const Color(0xFF17365D), useMaterial3: true),
      home: const Scaffold(
        body: SafeArea(
          child: Center(
            child: Padding(
              padding: EdgeInsets.all(24),
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Text('My Publisher Scheduler', style: TextStyle(fontSize: 28, fontWeight: FontWeight.bold)),
                  SizedBox(height: 12),
                  Text('Sprint 1 mobile foundation is ready.'),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}
