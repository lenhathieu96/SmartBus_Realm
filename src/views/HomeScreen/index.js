import React, {useEffect, useState} from 'react';
import {View, Text, NativeModules, AppRegistry, Alert} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import {useSelector, useDispatch} from 'react-redux';

import {updateVehicleLocation} from '../../redux/actionCreator/vehicleActions';

import RootContainer from '../../component/RootContainer';

export default function HomeScreen() {
  const {GPSModule} = NativeModules;
  const dispatch = useDispatch();

  const [imei, setImei] = useState();
  const [features, setFeatures] = useState([]);

  const vehicleProfile = useSelector((state) => state.vehicle);
  const userProfile = useSelector((state) => state.user);

  useEffect(() => {
    if (imei) {
      let vehicleData = {
        company_id: userProfile.company.id,
        company_name: userProfile.company.name,

        imei,
        vehicle_id: vehicleProfile.id,
        license_plates: vehicleProfile.license_plates,
        direction_name: vehicleProfile.departStation,
        direction: vehicleProfile.direction,
        route_number: vehicleProfile.route_number,
        is_running: 1,

        user: userProfile.driver_name,
        phone_user: userProfile.driver_phone,
        sub_user: userProfile.subDriver_name,
        phone_sub_user: userProfile.subDriver_phone,
      };
      // GPSModule.startTracking(JSON.stringify(vehicleData));
      // const handleNativeGPS = async (location) => {
      //   dispatch(updateVehicleLocation(location));
      // };
      // AppRegistry.registerHeadlessTask('GPSModule', () => handleNativeGPS);
      // eslint-disable-next-line react-hooks/exhaustive-deps
    } else {
      //On init component
      getStorageImei();
      getAvailableFeatures();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [imei]);

  const getStorageImei = async () => {
    try {
      const storageImei = await AsyncStorage.getItem('@imei');
      if (storageImei) {
        setImei(storageImei);
      }
    } catch (error) {
      console.log('Get storage imei failed: ', error);
      Alert.alert('Thông Báo', 'Lấy IMEI của máy thất bại');
    }
  };

  const getAvailableFeatures = () => {
    let modules = userProfile.company_module;
    console.log(modules);
    let availableFeatures = [];

    if (modules.includes('ve_luot')) {
      availableFeatures.push('Bán Vé');
    }

    if (
      modules.includes('the tra truoc') ||
      modules.includes('module_tt_sl_quet') ||
      modules.includes('module_tt_km')
    ) {
      availableFeatures.push('Nạp tiền vào thẻ');
    }
  };

  return (
    <RootContainer>
      <Text>ayyyo</Text>
    </RootContainer>
  );
}
