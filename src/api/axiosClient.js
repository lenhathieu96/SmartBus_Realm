import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';
import queryString from 'query-string';

import {getTimestamp} from '../utils/Libs';
import global from '../utils/Global';

const axiosClient = axios.create({
  baseURL: global.url,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
  paramsSerializer: (params) => queryString.stringify(params),
});

axiosClient.interceptors.request.use(async (config) => {
  const imei = await AsyncStorage.getItem('@imei');
  const timestamp = getTimestamp();
  if (imei) {
    config.headers.common.Accept = 'application/json; charset=utf-8';
    config.headers['X-IMEI'] = '359261051233786';
    config.headers.timestamp = timestamp;
  }
  return config;
});

axiosClient.interceptors.response.use(
  (response) => {
    if (response && response.status === 200) {
      return response.data;
    } else {
      console.log(response, 'response');
      return null;
    }
  },
  (error) => {
    return Promise.reject(error);
  },
);

export default axiosClient;
