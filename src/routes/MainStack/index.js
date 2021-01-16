import React, {useEffect} from 'react';
import {createStackNavigator} from '@react-navigation/stack';
import {useSelector, useDispatch} from 'react-redux';
import AsyncStorage from '@react-native-async-storage/async-storage';

import {setLogin} from '../../redux/actionCreator/authActions';
import AuthStack from './AuthStack';
import DashboardStack from './DashboardStack';

const Mainstack = createStackNavigator();

export default function MainStack() {
  const dispatch = useDispatch();
  const isLogin = useSelector((state) => state.auth.isLogin);

  useEffect(() => {
    getStorageStatus();
  }, []);

  const getStorageStatus = async () => {
    try {
      const statusStorage = await AsyncStorage.getItem('@Working');
      if (statusStorage) {
        const storageData = await AsyncStorage.multiGet([
          '@User',
          '@Vehicle',
          '@Setting_Global',
        ]);
        if (storageData) {
          console.log(storageData);
        } else {
          throw 'Storage data not failed';
        }
        console.log(storageData);
        // dispatch(setLogin());
      }
    } catch (error) {
      console.log('Error On Get Storage Data: ', error);
    }
  };
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
