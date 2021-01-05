import React, {useEffect, useState} from 'react';
import {
  View,
  TextInput,
  ActivityIndicator,
  PermissionsAndroid,
  Alert,
  Text,
} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
// import IMEI from 'react-native-imei';
import {useDispatch} from 'react-redux';

import RootContainer from '../../component/RootContainer';

import authAPI from '../../api/authAPI';
import {setHaveImei} from '../../redux/actionCreator/authActions';

import * as CompanyController from '../../database/controller/companyControllers';
import * as vehicleController from '../../database/controller/vehicleControllers';
import * as TicketController from '../../database/controller/ticketControllers';

import styles from './styles';

export default function ImeiScreen() {
  const [isLoading, setLoading] = useState();
  const [imei, setImei] = useState('');

  const dispatch = useDispatch();

  useEffect(() => {
    checkPermissions();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const checkPermissions = async () => {
    const permisson = await PermissionsAndroid.check(
      PermissionsAndroid.PERMISSIONS.READ_PHONE_STATE,
    );
    if (!permisson) {
      const granted = await PermissionsAndroid.request(
        PermissionsAndroid.PERMISSIONS.READ_PHONE_STATE,
      );
      if (granted) {
        getImeiDevice();
      }
    } else {
      getImeiDevice();
    }
  };

  const getImeiDevice = async () => {
    try {
      const storageImei = await AsyncStorage.getItem('@imei');
      if (!storageImei) {
        // const deviceImei = await IMEI.getImei();
        // if (deviceImei && deviceImei.length > 0) {
        //   checkImei(deviceImei[0]);
        checkImei('359261051233786');

        // }
      }
    } catch (error) {
      console.log('Error On Authenticate Imei Device', error);
    }
  };

  const checkImei = async (deviceImei) => {
    setLoading(true);
    try {
      const response = await authAPI.checkImei(deviceImei);
      if (response && response.code !== 404) {
        await AsyncStorage.setItem('@imei', deviceImei);
        initData();
      } else {
        await AsyncStorage.removeItem('@imei');
      }
    } catch (error) {
      setLoading(false);
      console.log('Error On Checking Imei : ', error);
      Alert.alert('Thông báo', 'Xảy ra lỗi trong quá trình xác minh IMEI');
    }
  };

  const getDataAllTicket = async () => {
    try {
      const ticketTypeIDArr = await TicketController.getAllTicketTypeID();
      await TicketController.insertTicketAllocation(ticketTypeIDArr);
    } catch (error) {
      console.log(`Error On Get All Ticket: ${error}`);
      throw new Error(error);
    }
  };

  const initData = async () => {
    try {
      await vehicleController.insertVehicle('init');
      await vehicleController.insertRoutes('init');
      await vehicleController.insertBus_Station('init');
      await vehicleController.insertRouteBusStation();

      await CompanyController.insertCompany('init');
      await CompanyController.insertCompanyModule();
      await CompanyController.insertSettingGlobal();
      await CompanyController.insertUser('init');

      await TicketController.insertTickectType('init');
      await TicketController.insertDenomination('init');

      await getDataAllTicket();
      dispatch(setHaveImei());
    } catch (error) {
      setLoading(false);
      console.log('Error On Init Data: ', error);
      Alert.alert('Xảy ra lỗi trong quá trình khới tạo dữ liệu');
    }
  };

  return isLoading ? (
    <RootContainer style={styles.loadingContainer}>
      <ActivityIndicator size="large" color="blue" />
      <Text style={styles.txtLoading}>
        Đang khởi tạo dữ liệu,{'\n'} vui lòng đợi trong giây lát...!
      </Text>
    </RootContainer>
  ) : (
    <View style={styles.mainContainer}>
      <View style={{justifyContent: 'space-between'}}>
        <TextInput
          style={styles.textInput}
          placeholder="IMEI"
          onChangeText={(text) => setImei(text)}
          value={imei}
          underlineColorAndroid="transparent"
        />
      </View>
    </View>
  );
}
