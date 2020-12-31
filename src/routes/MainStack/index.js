import React from 'react';
import {createStackNavigator} from '@react-navigation/stack';

const Mainstack = createStackNavigator();

import AuthStack from './AuthStack';
import DashboardStack from './DashboardStack';

export default function MainStack() {
  return (
    <Mainstack.Navigator>
      <Mainstack.Screen
        name="auth"
        component={AuthStack}
        options={{headerShown: false}}
      />
      <Mainstack.Screen
        name="dashboard"
        component={DashboardStack}
        options={{headerShown: false}}
      />
    </Mainstack.Navigator>
  );
}
