import React from 'react';
import {createBottomTabNavigator} from '@react-navigation/bottom-tabs';
import {useSelector} from 'react-redux';
import Icon from 'react-native-vector-icons/FontAwesome5';

import BusTicketScreen from '../../../../views/BusTicketScreen';
import GoodsTicketScreen from '../../../../views/GoodsTicketScreen';

import * as fontSize from '../../../../utils/Fontsize';

const TicketBottomStack = createBottomTabNavigator();

export default function TicketStack() {
  const modules = useSelector((state) => state.user.company_module);

  return (
    <TicketBottomStack.Navigator
      initialRouteName="ticket"
      tabBarOptions={{
        activeTintColor: '#69aa6b',
        labelStyle: {
          fontSize: fontSize.larger,
          fontWeight: 'bold',
        },
      }}>
      <TicketBottomStack.Screen
        name="ticket"
        component={BusTicketScreen}
        options={{
          tabBarLabel: 'Vé',
          tabBarIcon: ({focused}) => (
            <Icon
              name="ticket-alt"
              size={fontSize.bigger}
              color={focused ? '#69aa6b' : '#b8b8b8'}
            />
          ),
        }}
      />
      {modules.includes('module_vc_hang_hoa') ? (
        <TicketBottomStack.Screen
          name="goods"
          component={GoodsTicketScreen}
          options={{
            tabBarLabel: 'Hàng Hóa',
            tabBarIcon: ({focused}) => (
              <Icon
                name="box-open"
                size={fontSize.bigger}
                color={focused ? '#69aa6b' : '#b8b8b8'}
              />
            ),
          }}
        />
      ) : null}
    </TicketBottomStack.Navigator>
  );
}
