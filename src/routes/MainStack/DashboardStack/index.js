import React from 'react';
import {View} from 'react-native';
import {createStackNavigator} from '@react-navigation/stack';
import {useSelector} from 'react-redux';

import HomeScreen from '../../../views/HomeScreen';
import TicketStack from './TicketStack';

import {biggest} from '../../../utils/Fontsize';

const Dashboardstack = createStackNavigator();

export default function DashboardStack() {
  const userProfile = useSelector((state) => state.user);

  return (
    <Dashboardstack.Navigator initialRouteName="home">
      <Dashboardstack.Screen
        name="home"
        component={HomeScreen}
        options={{
          headerTitle: `CÔNG TY ${userProfile.company.name}`,
          headerTitleStyle: {
            color: 'red',
            fontSize: biggest,
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
      {userProfile.company_module.includes('ve_luot') ? (
        <Dashboardstack.Screen
          name="ticket"
          component={TicketStack}
          options={{
            headerTitle: `CÔNG TY ${userProfile.company.name}`,
            headerTitleStyle: {
              color: 'red',
              fontSize: biggest,
              alignSelf: 'center',
            },
            headerStyle: {
              elevation: 0,
              shadowOpacity: 0,
              backgroundColor: 'transparent',
            },
            headerRight: () => <View />,
          }}
        />
      ) : null}
    </Dashboardstack.Navigator>
  );
}
