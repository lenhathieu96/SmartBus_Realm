import React, {useEffect} from 'react';
import {View, NativeEventEmitter, NativeModules} from 'react-native';
import {createStackNavigator} from '@react-navigation/stack';
import {useSelector, useDispatch} from 'react-redux';

import HomeScreen from '../../../views/HomeScreen';
import TicketStack from './TicketStack';

import {setPrintAvailble} from '../../../redux/actionCreator/deviceActions';

import {biggest} from '../../../utils/Fontsize';

const Dashboardstack = createStackNavigator();

export default function DashboardStack() {
  const {PrintModule} = NativeModules;

  const dispatch = useDispatch();
  const userProfile = useSelector((state) => state.user);
  const deviceState = useSelector((state) => state.device);

  const eventEmitter = new NativeEventEmitter(PrintModule);

  useEffect(() => {
    PrintModule.init();
    let PaperListener;
    if (deviceState.printAvailable) {
      PaperListener = eventEmitter.addListener('NO_PAPER', () => {
        dispatch(setPrintAvailble(false));
      });
    } else {
      PaperListener = eventEmitter.addListener('HAVE_PAPER', () => {
        console.log('have paper');
        dispatch(setPrintAvailble(true));
      });
    }
    return () => {
      PaperListener.remove();
      PrintModule.UnregisterReceiver();
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [deviceState.printAvailable]);

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
