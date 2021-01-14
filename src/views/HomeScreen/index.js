import React, {useEffect, useState} from 'react';
import {View, Text, NativeModules, AppRegistry, Alert} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import {useSelector, useDispatch} from 'react-redux';
import {getPreciseDistance} from 'geolib';

import companyAPI from '../../api/companyAPI';
import {getCurrentTime} from '../../utils/Libs';
import {setLogout} from '../../redux/actionCreator/authActions';
import {updateCurrentStation} from '../../redux/actionCreator/vehicleActions';

import RootContainer from '../../component/RootContainer';
import TextButton from '../../component/TextButton';

import styles from './styles';

export default function HomeScreen({navigation}) {
  const {GPSModule} = NativeModules;
  const dispatch = useDispatch();

  const [imei, setImei] = useState();

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

      let currentStationData =
        vehicleProfile.stationList[vehicleProfile.current_station_index];
      let nextStationData =
        vehicleProfile.stationList[vehicleProfile.current_station_index + 1];

      // GPSModule.startTracking(
      //   JSON.stringify(vehicleData),
      //   global.url_node_server,
      // );
      // const handleNativeGPS = async (location) => {
      //   const currentStationDistance = getPreciseDistance(
      //     {latitude: location.latitude, longitude: location.longitude},
      //     {latitude: currentStationData.lat, longitude: currentStationData.lng},
      //     0.1,
      //   );
      //   const nextStationDistance = getPreciseDistance(
      //     {latitude: location.latitude, longitude: location.longitude},
      //     {latitude: nextStationData.lat, longitude: nextStationData.lng},
      //     0.1,
      //   );
      //   if (currentStationDistance + 100 > nextStationDistance - 100) {
      //     console.log('alo');
      //   } else {
      //     dispatch(updateCurrentStation());
      //   }
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

  const logOut = async () => {
    let userData = [
      {
        timestamp: getCurrentTime(),
        action: 'logout',
        subject_type: 'user',
        user_id: userProfile.main_id,
        subject_data: JSON.stringify({
          vehicle_id: vehicleProfile.id,
          total_amount: 100000,
        }),
      },
    ];
    const res = await companyAPI.updateActivity(JSON.stringify(userData));
    if (res && res.status) {
      dispatch(setLogout());
    }
  };

  return (
    <RootContainer>
      <View style={styles.btnContainer}>
        {userProfile.company_module.includes('ve_luot') ? (
          <TextButton
            style={styles.OptionBtn}
            textStyle={styles.txtOptionBtn}
            text="Bán Vé"
            onPress={() => navigation.navigate('ticket')}
          />
        ) : null}
        {userProfile.company_module.includes('the tra truoc') ||
        userProfile.company_module.includes('module_tt_sl_quet') ||
        userProfile.company_module.includes('module_tt_km') ? (
          <TextButton
            style={styles.OptionBtn}
            textStyle={styles.txtOptionBtn}
            text="Nạp Tiền Vào Thẻ"
            onPress={() => console.log('nap the')}
          />
        ) : null}
      </View>
      <View style={styles.btnContainer}>
        {userProfile.company_settingGlobal.includes(
          'glo_hidden_login_user',
        ) ? null : (
          <TextButton
            style={styles.OptionBtn}
            textStyle={styles.txtOptionBtn}
            text="Đăng Nhập Nhân Viên"
            onPress={() => console.log('Đăng Nhập')}
          />
        )}

        <TextButton
          style={styles.OptionBtn}
          textStyle={styles.txtOptionBtn}
          text="Tổng Kết & Đăng Xuất"
          onPress={() => logOut()}
        />
      </View>
      <View style={styles.profileContainer}>
        <View style={styles.txtContainer}>
          <Text>Tài xế: </Text>
          <Text>{userProfile.driver_name}</Text>
        </View>
        <View style={styles.txtContainer}>
          <Text>Phụ xe: </Text>
          <Text>{userProfile.subDriver_name}</Text>
        </View>
        <View style={styles.txtContainer}>
          <Text>Biển số xe: </Text>
          <Text>{vehicleProfile.license_plates}</Text>
        </View>
        <View style={styles.txtContainer}>
          <Text>Trạm hiện tại: </Text>
          <Text>{vehicleProfile.current_station.name}</Text>
        </View>
        <View style={styles.txtContainer}>
          <Text>Trạm tiếp theo: </Text>
          <Text>{vehicleProfile.next_station.name}</Text>
        </View>
      </View>
    </RootContainer>
  );
}
