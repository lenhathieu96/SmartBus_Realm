import React, {useEffect} from 'react';
import {View, Text, NativeModules, AppRegistry} from 'react-native';
import {useSelector} from 'react-redux';

export default function HomeScreen() {
  const {GPSModule} = NativeModules;

  useEffect(() => {
    GPSModule.startTracking();
    const MyHeadlessTask = async (taskData) => {
      console.log(taskData);
    };
    AppRegistry.registerHeadlessTask('GPSModule', () => MyHeadlessTask);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <View>
      <Text>home screen</Text>
    </View>
  );
}
