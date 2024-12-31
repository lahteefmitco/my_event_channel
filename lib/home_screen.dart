import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  static const EventChannel _eventChannel = EventChannel('com.example/events');
  static const MethodChannel _methodChannel =
      MethodChannel('com.example/methods');

  String _receivedEvent = "No events yet";

  @override
  void initState() {
    super.initState();
    _startListeningToEvents();
  }

  void _startListeningToEvents() {
    _eventChannel.receiveBroadcastStream().listen((dynamic event) {
      setState(() {
        _receivedEvent = "Event from Android: $event";
      });
    }, onError: (dynamic error) {
      setState(() {
        _receivedEvent = "Error: ${error.message}";
      });
    });
  }

  Future<void> _sendDataToAndroid(String data) async {
    try {
      await _methodChannel.invokeMethod('sendData', {'data': data});
    } catch (e) {
      print("Error sending data: $e");
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Event Channel"),
        centerTitle: true,
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(_receivedEvent),
            ElevatedButton(
              onPressed: () {
                _sendDataToAndroid("10");
              },
              child: const Text("START EVENT CHANNEL"),
            )
          ],
        ),
      ),
    );
  }
}
