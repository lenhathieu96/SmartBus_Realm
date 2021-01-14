import React, {useState, useEffect} from 'react';
import {View, Text, TextInput, Alert} from 'react-native';
import NfcManager, {NfcEvents} from 'react-native-nfc-manager';
import {useDispatch} from 'react-redux';

import RootContainer from '../../component/RootContainer';
import TextButton from '../../component/TextButton';

import companyAPI from '../../api/companyAPI';
import {updateSettingGlobal} from '../../redux/actionCreator/userActions';

import {setVehicleData} from '../../redux/actionCreator/vehicleActions';

import {getVehicleByRfid} from '../../database/controller/vehicleControllers';

import styles from './styles';
import AsyncStorage from '@react-native-async-storage/async-storage';

export default function VehicleLoginScreen({navigation}) {
  const dispatch = useDispatch();

  const [pinCode, setPinCode] = useState('4726E63C');
  const [isLoading, setLoading] = useState(false);

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

  const getSettingGlobal = async () => {
    try {
      const res = await companyAPI.getSettingGlobal();
      if (res) {
        const settingGlobalList = res.map((settingGlobal) =>
          JSON.parse(settingGlobal.subject_data),
        )[0];
        //get all setting with value 1
        let availableSettings = settingGlobalList
          .filter((setting) => setting.value === '1')
          .map((item) => item.key);
        dispatch(updateSettingGlobal(availableSettings));
        await AsyncStorage.setItem(
          '@Setting_Global',
          JSON.stringify(availableSettings),
        );
      }
    } catch (error) {
      throw error;
    }
  };

  const checkPinCode = async (rfid = pinCode) => {
    setLoading(true);
    try {
      const vehicle = await getVehicleByRfid(rfid);
      if (vehicle) {
        let vehicleData = {
          id: vehicle.id,
          rfid: vehicle.rfid,
          route_id: vehicle.route_id,
          license_plates: vehicle.license_plates,
          route_number: vehicle.route_number,
        };
        await getSettingGlobal();
        dispatch(setVehicleData(vehicleData));
        await AsyncStorage.setItem('@Vehicle', JSON.stringify(vehicleData));
        navigation.navigate('direction');
      } else {
        Alert.alert('Phương tiện không tồn tại');
      }
    } catch (error) {
      setLoading(false);
      console.log('Error on validate vehicle:', error);
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
        isLoading={isLoading}
        onPress={() => {
          checkPinCode();
        }}
      />
    </RootContainer>
  );
}
