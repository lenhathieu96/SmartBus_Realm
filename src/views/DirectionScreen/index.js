import React, {useEffect, useState} from 'react';
import {View, Text} from 'react-native';
import {useSelector, useDispatch} from 'react-redux';
import Icon from 'react-native-vector-icons/FontAwesome';

import TextButton from '../../component/TextButton';
import RootContainer from '../../component/RootContainer';

import {getDepartStation} from '../../database/controller/vehicleControllers';
import {setVehicleDirection} from '../../redux/actionCreator/vehicleActions';

import * as fontSize from '../../utils/Fontsize';
import styles from './styles';

export default function DirectionScreen({navigation}) {
  const [departStation, setDepartStation] = useState('');
  const [returnStation, setReturnStation] = useState('');

  const dispatch = useDispatch();
  const vehicle = useSelector((state) => state.vehicle);

  useEffect(() => {
    getDirection();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const getDirection = async () => {
    const result = await getDepartStation(vehicle.route_id);
    setDepartStation(result.depart);
    setReturnStation(result.return);
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

  return (
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
