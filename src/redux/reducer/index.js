import {combineReducers} from 'redux';

import authReducer from './authReducer';
import userReducer from './userReducer';
import vehicleReducer from './vehicleReducer';
import deviceReducer from './deviceReducer';

const rootReducer = combineReducers({
  auth: authReducer,
  user: userReducer,
  vehicle: vehicleReducer,
  device: deviceReducer,
});

export default rootReducer;
