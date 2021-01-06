import React, {useEffect, useState} from 'react';
import {View, Text, ActivityIndicator} from 'react-native';
import {useSelector, useDispatch} from 'react-redux';
import Icon from 'react-native-vector-icons/FontAwesome';
import Geolocation from '@react-native-community/geolocation';

import TextButton from '../../component/TextButton';
import RootContainer from '../../component/RootContainer';

import {getDepartStation} from '../../database/controller/vehicleControllers';
import {setVehicleDirection} from '../../redux/actionCreator/vehicleActions';

import * as fontSize from '../../utils/Fontsize';
import styles from './styles';

export default function DirectionScreen({navigation}) {
  const [isLoading, setLoading] = useState(true);
  const [departStation, setDepartStation] = useState('');
  const [returnStation, setReturnStation] = useState('');

  const dispatch = useDispatch();
  const vehicle = useSelector((state) => state.vehicle);

  useEffect(() => {
    getDirection();
    Geolocation.getCurrentPosition((position) => console.log(position));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const getDirection = async () => {
    try {
      const result = await getDepartStation(vehicle.route_id);
      setDepartStation(result.depart);
      setReturnStation(result.return);
      setLoading(false);
    } catch (error) {
      console.log('Error on get depart station', error);
      setLoading(false);
    }
  };

  const chooseDirection = async (direction) => {
    let data = {
      direction,
      departStation:
        direction === 0
          ? departStation[0].name
          : returnStation[returnStation.length - 1].name,
      stationList: direction === 0 ? departStation : returnStation,
    };
    dispatch(setVehicleDirection(data));
    navigation.navigate('home');
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
          onPress={() => {
            chooseDirection(1);
          }}
        />
      </View>
    </RootContainer>
  );
}
