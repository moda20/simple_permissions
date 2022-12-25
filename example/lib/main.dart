import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:simple_permissions/simple_permissions.dart';

void main() => runApp(new MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => new _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  Permission ?permission;

  @override
  initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await SimplePermissions.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      home: new Scaffold(
        appBar: new AppBar(
          title: new Text('Plugin example app'),
        ),
        body: new Center(
          child: new Column(children: <Widget>[
            new Text('Running on: $_platformVersion\n'),
            new DropdownButton(
                items: _getDropDownItems(),
                value: permission,
                onChanged: onDropDownChanged),
            new  ElevatedButton(
                onPressed: checkPermission,
                child: new Text("Check permission")),
            new  ElevatedButton(
                onPressed: requestPermission,
                child: new Text("Request permission")),
            new  ElevatedButton(
                onPressed: getPermissionStatus,
                child: new Text("Get permission status")),
            new  ElevatedButton(
                onPressed: SimplePermissions.openSettings,
                child: new Text("Open settings"))
          ]),
        ),
      ),
    );
  }

  onDropDownChanged(Permission ?permission) {
    setState(() => this.permission = permission);
    print(permission);
  }

  requestPermission() async {
    final res = await SimplePermissions.requestPermission(permission);
    print("permission request result is " + res.toString());
  }

  checkPermission() async {
    bool res = await SimplePermissions.checkPermission(permission);
    print("permission is " + res.toString());
  }

  getPermissionStatus() async {
    final res = await SimplePermissions.getPermissionStatus(permission);
    print("permission status is " + res.toString());
  }

  List<DropdownMenuItem<Permission>> _getDropDownItems() {
    List<DropdownMenuItem<Permission>> items = new List.empty(growable: true);
    Permission.values.forEach((permission) {
      var item = new DropdownMenuItem(
          child: new Text(getPermissionString(permission)), value: permission);
      items.add(item);
    });
    return items;
  }
}
