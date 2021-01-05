import React, {useEffect} from 'react';
import {View, Text, NativeModules} from 'react-native';
import {useSelector} from 'react-redux';

export default function HomeScreen() {
  const {GPSModule} = NativeModules;

  useEffect(() => {
    GPSModule.createCalendarEvent('testName', 'testLocation');
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <View>
      <Text>home screen</Text>
    </View>
  );
}
