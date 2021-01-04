import React from 'react';
import {createStackNavigator} from '@react-navigation/stack';

import HomeScreen from '../../../views/HomeScreen';
import DirectionScreen from '../../../views/DirectionScreen';

const Dashboardstack = createStackNavigator();

export default function DashboardStack() {
  return (
    <Dashboardstack.Navigator initialRouteName="direction">
      <Dashboardstack.Screen
        name="home"
        component={HomeScreen}
        options={{headerShown: false}}
      />
      <Dashboardstack.Screen
        name="direction"
        component={DirectionScreen}
        options={{headerShown: false}}
      />
    </Dashboardstack.Navigator>
  );
}
