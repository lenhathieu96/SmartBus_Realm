import React, {useEffect} from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';
import {createStackNavigator} from '@react-navigation/stack';
import {useSelector, useDispatch} from 'react-redux';

import ImeiScreen from '../views/ImeiScreen';
import MainStack from './MainStack';

import {setHaveImei} from '../redux/actionCreator/authActions';

const Route = createStackNavigator();

export default function MainRoute() {
  const dispatch = useDispatch();
  const haveImei = useSelector((state) => state.auth.haveImei);

  useEffect(() => {
    checkStorageImei();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const checkStorageImei = async () => {
    try {
      const storageImei = await AsyncStorage.getItem('@imei');
      if (storageImei) {
        dispatch(setHaveImei());
      }
    } catch (error) {
      console.log('Error On Get Storage Imei: ', error);
    }
  };

  return (
    <Route.Navigator>
      {haveImei ? (
        <Route.Screen
          name="MainStack"
          component={MainStack}
          options={{headerShown: false}}
        />
      ) : (
        <Route.Screen
          name="Imei"
          component={ImeiScreen}
          options={{headerShown: false}}
        />
      )}
    </Route.Navigator>
  );
}
