import React, {useState, useEffect} from 'react';
import {View, Text, TextInput, Alert} from 'react-native';
import NfcManager, {NfcEvents} from 'react-native-nfc-manager';
import {useDispatch} from 'react-redux';

import RootContainer from '../../component/RootContainer';
import TextButton from '../../component/TextButton';

import {setUserData} from '../../redux/actionCreator/userActions';
import {
  getUserByRfid,
  getCompanyData,
  getCompanyModuleData,
} from '../../database/controller/companyControllers';

import styles from './styles';

export default function UserLoginScreen({navigation}) {
  const dispatch = useDispatch();

  const [pinCode, setPinCode] = useState('D786003D');
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
      const user = await getUserByRfid(rfid);
      if (user) {
        if (user.disable === 1) {
          Alert.alert('Tài khoản đã bị vô hiệu hoá');
        } else {
          const company = await getCompanyData();
          const company_module = await getCompanyModuleData();
          if (company && company_module) {
            let userData = {
              main_id: user.id,
              main_rfid: user.rfid,
              driver_name: '',
              subDriver_name: '',
              active: 1,
              company,
              company_module,
            };
            switch (user.role_id) {
              case 6:
                navigation.navigate('supervisor');
                setPinCode('');
                break;
              case 4:
                userData.driver_name = user.fullname;
                userData.driver_phone = user.phone;
                break;
              case 5:
                userData.subDriver_name = user.fullname;
                userData.subDriver_phone = user.phone;
                break;
              default:
                break;
            }
            dispatch(setUserData(userData));
            navigation.navigate('vehicle');
          } else {
            throw 'company or module is empty';
          }
        }
      } else {
        Alert.alert('Người dùng không tồn tại');
      }
    } catch (error) {
      console.log('Error on validate user:', error);
    }
  };

  return (
    <RootContainer>
      <Text style={styles.title}>Đăng Nhập Người Dùng</Text>
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
