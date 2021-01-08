import React from 'react';
import {createStackNavigator} from '@react-navigation/stack';

import UserLoginScreen from '../../../views/UserLoginScreen';
import VehicleLoginScreen from '../../../views/VehicleLoginScreen';
import SuperVisorScreen from '../../../views/SuperVisorScreen';
import DirectionScreen from '../../../views/DirectionScreen';

const Authstack = createStackNavigator();

export default function DashboardStack() {
  return (
    <Authstack.Navigator>
      <Authstack.Screen
        name="user"
        component={UserLoginScreen}
        options={{headerShown: false}}
      />
      <Authstack.Screen
        name="vehicle"
        component={VehicleLoginScreen}
        options={{headerShown: false}}
      />
      <Authstack.Screen
        name="supervisor"
        component={SuperVisorScreen}
        options={{headerShown: false}}
      />
      <Authstack.Screen
        name="direction"
        component={DirectionScreen}
        options={{headerShown: false}}
      />
    </Authstack.Navigator>
  );
}
