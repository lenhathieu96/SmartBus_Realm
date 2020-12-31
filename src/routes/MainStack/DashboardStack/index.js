import React from 'react';
import {createStackNavigator} from '@react-navigation/stack';

import HomeScreen from '../../../views/HomeScreen';

const Dashboardstack = createStackNavigator();

export default function DashboardStack() {
  return (
    <Dashboardstack.Navigator>
      <Dashboardstack.Screen
        name="home"
        component={HomeScreen}
        options={{headerShown: false}}
      />
    </Dashboardstack.Navigator>
  );
}
