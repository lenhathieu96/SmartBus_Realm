import React, {useState, useEffect} from 'react';
import {View, Text, TextInput, Alert} from 'react-native';
import NfcManager, {NfcEvents} from 'react-native-nfc-manager';
import {useDispatch} from 'react-redux';

import RootContainer from '../../component/RootContainer';
import TextButton from '../../component/TextButton';

import {setLogin} from '../../redux/actionCreator/authActions';
import {setVehicleData} from '../../redux/actionCreator/vehicleActions';

import {getVehicleByRfid} from '../../database/controller/vehicleControllers';

import styles from './styles';

export default function VehicleLoginScreen() {
  const dispatch = useDispatch();

  const [pinCode, setPinCode] = useState('4726E63C');
  useEffect(() => {
    NfcManager.start().catch(
      () => console.log('NFC Not supported'),
      // Alert.alert('Thong bao', 'Thiet bi ko ho tro NFC'),
    );
    NfcManager.setEventListener(NfcEvents.DiscoverTag, (tag) => {
      setPinCode(tag.id);
      checkPinCode(tag.id);
    });
    NfcManager.registerTagEvent();
    return () => {
      NfcManager.setEventListener(NfcEvents.DiscoverTag, null);
      NfcManager.unregisterTagEvent();
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [pinCode]);

  const checkPinCode = async (rfid = pinCode) => {
    try {
      const vehicle = await getVehicleByRfid(rfid);
      if (vehicle) {
        let vehicleData = {
          id: vehicle.id,
          route_id: vehicle.route_id,
          license_plates: vehicle.license_plates,
          route_number: vehicle.route_number,
        };
        dispatch(setVehicleData(vehicleData));
        dispatch(setLogin());
      } else {
        Alert.alert('Phương tiện không tồn tại');
      }
    } catch (error) {
      console.log('Error on validate Vehicle:', error);
    }
  };

  return (
    <RootContainer>
      <Text style={styles.title}>Đăng Nhập Phương TIện</Text>
      <TextInput
        style={styles.input}
        autoFocus={true}
        value={pinCode}
        onChangeText={(text) => setPinCode(text)}
      />
      <TextButton
        text="Đăng Nhập"
        onPress={() => {
          checkPinCode();
        }}
      />
    </RootContainer>
  );
}
