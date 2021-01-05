import {combineReducers} from 'redux';

import authReducer from './authReducer';
import userReducer from './userReducer';
import vehicleReducer from './vehicleReducer';

const rootReducer = combineReducers({
  auth: authReducer,
  user: userReducer,
  vehicle: vehicleReducer,
});

export default rootReducer;
