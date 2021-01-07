import React from 'react';
import {createStackNavigator} from '@react-navigation/stack';
import {useSelector} from 'react-redux';

import HomeScreen from '../../../views/HomeScreen';
import DirectionScreen from '../../../views/DirectionScreen';

import {huge} from '../../../utils/Fontsize';

const Dashboardstack = createStackNavigator();

export default function DashboardStack() {
  const company = useSelector((state) => state.user.company);
  return (
    <Dashboardstack.Navigator initialRouteName="direction">
      <Dashboardstack.Screen
        name="home"
        component={HomeScreen}
        options={{
          headerTitle: company.name,
          headerTitleStyle: {
            color: 'red',
            fontSize: huge,
            alignSelf: 'center',
          },
          headerStyle: {
            elevation: 0,
            shadowOpacity: 0,
            backgroundColor: 'transparent',
          },
          headerLeft: null,
        }}
      />
      <Dashboardstack.Screen
        name="direction"
        component={DirectionScreen}
        options={{headerShown: false}}
      />
    </Dashboardstack.Navigator>
  );
}
