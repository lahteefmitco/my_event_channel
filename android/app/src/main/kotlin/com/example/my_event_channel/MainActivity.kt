package com.example.my_event_channel

import androidx.lifecycle.lifecycleScope
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : FlutterActivity() {
    private val EVENT_CHANNEL = "com.example/events"
    private val METHOD_CHANNEL = "com.example/methods"
    private var eventSink: EventChannel.EventSink? = null

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        // Set up EventChannel for streaming events
        EventChannel(flutterEngine.dartExecutor.binaryMessenger, EVENT_CHANNEL).setStreamHandler(
            object : EventChannel.StreamHandler {
                override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                    eventSink = events
                    eventSink?.success("Initial Data")
                }

                override fun onCancel(arguments: Any?) {
                    eventSink = null
                }
            }
        )

        // Set up MethodChannel for receiving data from Flutter
        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            METHOD_CHANNEL
        ).setMethodCallHandler { call, result ->
            if (call.method == "sendData") {
                val data = (call.arguments as? Map<*, *>)?.get("data") as? String
                if (data != null) {
                    handleFlutterData(data)
                    result.success("Data received successfully")
                } else {
                    result.error("INVALID_ARGUMENT", "Data not provided", null)
                }
            } else {
                result.notImplemented()
            }
        }
    }

    private   fun handleFlutterData(data: String) {
        println("Data received from Flutter: $data")
        val count = data.toInt()
            lifecycleScope.launch   {
                for ( i in 1..count){
                    delay(1000)
                    eventSink?.success("$i")

                }
            }
    }
}

