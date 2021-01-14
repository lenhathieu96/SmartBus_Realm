import React, {useEffect, useState} from 'react';
import {View, Text, ActivityIndicator} from 'react-native';
import {useSelector, useDispatch} from 'react-redux';
import Icon from 'react-native-vector-icons/FontAwesome';

import TextButton from '../../component/TextButton';
import RootContainer from '../../component/RootContainer';

import busAPI from '../../api/busAPI';
import {getDepartStation} from '../../database/controller/vehicleControllers';
import {setVehicleDirection} from '../../redux/actionCreator/vehicleActions';
import {setLogin} from '../../redux/actionCreator/authActions';

import {getCurrentTime, getTimestamp} from '../../utils/Libs';

import * as fontSize from '../../utils/Fontsize';
import styles from './styles';
import companyAPI from '../../api/companyAPI';
import AsyncStorage from '@react-native-async-storage/async-storage';

export default function DirectionScreen({navigation}) {
  const [isLoading, setLoading] = useState(true);
  const [isAuthen, setAuthen] = useState(false);

  const [chosenBtn, chooseBtn] = useState();
  const [departStation, setDepartStation] = useState('');
  const [returnStation, setReturnStation] = useState('');

  const dispatch = useDispatch();
  const vehicle = useSelector((state) => state.vehicle);
  const user = useSelector((state) => state.user);

  useEffect(() => {
    getDirection();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const getDirection = async () => {
    try {
      const result = await getDepartStation(vehicle.route_id);
      setDepartStation(result.depart);
      setReturnStation(result.return);
    } catch (error) {
      console.log('Error on get depart station', error);
    }
    setLoading(false);
  };

  const chooseDirection = async (direction) => {
    setAuthen(true);
    chooseBtn(direction);
    try {
      let vehicleData = {
        direction,
        departStation:
          direction === 0
            ? departStation[0].name
            : returnStation[returnStation.length - 1].name,
        stationList: direction === 0 ? departStation : returnStation.reverse(),
      };
      dispatch(setVehicleDirection(vehicleData));

      let userData = [
        {
          timestamp: getCurrentTime(),
          action: 'login',
          subject_type: 'user',
          user_id: user.main_id,
          subject_data: JSON.stringify({
            vehicle_id: vehicle.id,
            rfid_vehicle: vehicle.rfid,
            rfid_user: user.main_rfid,
            station_id:
              direction === 0
                ? departStation[0].id
                : returnStation[returnStation.length - 1].id,
          }),
        },
      ];
      const res = await companyAPI.updateActivity(JSON.stringify(userData));
      if (res && res.status) {
        dispatch(setLogin());
        await AsyncStorage.setItem('@Working', 'true');
      }
    } catch (error) {
      console.log('Error on login : ', error);
      chooseBtn(undefined);
    }
    setAuthen(false);
  };

  return isLoading ? (
    <RootContainer style={styles.loadingContainer}>
      <ActivityIndicator size="large" color="blue" />
      <Text style={styles.txtLoading}>
        Đang thiết lập dữ liệu các trạm dừng{'\n'} vui lòng đợi trong giây
        lát...!
      </Text>
    </RootContainer>
  ) : (
    <RootContainer>
      <Text>Vui Lòng Chọn Bến Xuất Phát</Text>
      <View style={styles.buttonContainer}>
        <Icon name="bus" size={1.5 * fontSize.biggest} />
        <TextButton
          style={styles.btn}
          disabled={chosenBtn === 1}
          isLoading={isAuthen && chosenBtn === 0}
          text={departStation ? departStation[0].name : ''}
          onPress={() => {
            chooseDirection(0);
          }}
        />
      </View>
      <View style={styles.buttonContainer}>
        <Icon name="bus" size={1.5 * fontSize.biggest} />
        <TextButton
          style={styles.btn}
          text={
            returnStation ? returnStation[returnStation.length - 1].name : ''
          }
          disabled={chosenBtn === 0}
          isLoading={isAuthen && chosenBtn === 1}
          onPress={() => {
            chooseDirection(1);
          }}
        />
      </View>
    </RootContainer>
  );
}
