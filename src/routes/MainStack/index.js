import React from 'react';
import {createStackNavigator} from '@react-navigation/stack';
import {useSelector} from 'react-redux';

const Mainstack = createStackNavigator();

import AuthStack from './AuthStack';
import DashboardStack from './DashboardStack';

export default function MainStack() {
  const isLogin = useSelector((state) => state.auth.isLogin);

  return (
    <Mainstack.Navigator>
      {isLogin ? (
        <Mainstack.Screen
          name="dashboard"
          component={DashboardStack}
          options={{headerShown: false}}
        />
      ) : (
        <Mainstack.Screen
          name="auth"
          component={AuthStack}
          options={{headerShown: false}}
        />
      )}
    </Mainstack.Navigator>
  );
}
