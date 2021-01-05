import React from 'react';
import {View, Text} from 'react-native';
import {useSelector} from 'react-redux';

export default function HomeScreen() {
  const vehicle = useSelector((state) => state.vehicle);
  console.log(vehicle);
  return (
    <View>
      <Text>home screen</Text>
    </View>
  );
}
