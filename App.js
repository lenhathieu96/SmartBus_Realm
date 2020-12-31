import 'react-native-gesture-handler';
import React from 'react';
import {NavigationContainer} from '@react-navigation/native';
import {Provider} from 'react-redux';

import store from './src/redux/store';
import Route from './src/routes';

export default function App() {
  return (
    <Provider store={store}>
      <NavigationContainer>
        <Route />
      </NavigationContainer>
    </Provider>
  );
}
