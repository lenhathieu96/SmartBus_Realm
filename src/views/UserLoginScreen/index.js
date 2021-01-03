import React, {useState, useEffect} from 'react';
import {View, Text, TextInput, Alert} from 'react-native';
import NfcManager, {NfcEvents} from 'react-native-nfc-manager';

import RootContainer from '../../component/RootContainer';
import TextButton from '../../component/TextButton';

import {getUserByRfid} from '../../database/controller/companyControllers';

import styles from './styles';

export default function UserLoginScreen({navigation}) {
  const [pinCode, setPinCode] = useState('D786003D');
  useEffect(() => {
    NfcManager.start().catch(
      () => console.log('NFC Not supported'),
      // Alert.alert('Thong bao', 'Thiet bi ko ho tro NFC'),
    );
    NfcManager.setEventListener(NfcEvents.DiscoverTag, (tag) => {
      setPinCode(tag.id);
      checkPinCode();
    });
    NfcManager.registerTagEvent();
    return () => {
      NfcManager.setEventListener(NfcEvents.DiscoverTag, null);
      NfcManager.unregisterTagEvent();
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const checkPinCode = async () => {
    const user = await getUserByRfid(pinCode);
    if (user) {
      if (user.disable === 1) {
        Alert.alert('Tài khoản đã bị vô hiệu hoá');
      } else {
        navigation.navigate('vehicle');
      }
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
