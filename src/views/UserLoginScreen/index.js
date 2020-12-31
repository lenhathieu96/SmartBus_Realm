import React, {useState, useEffect} from 'react';
import {View, Text} from 'react-native';
import NfcManager, {NfcEvents} from 'react-native-nfc-manager';

import {getUserByRfid} from '../../database/controller/companyControllers';

export default function UserLoginScreen() {
  const [pinCode, setPinCode] = useState('D786003D');
  useEffect(() => {
    NfcManager.start();
    NfcManager.setEventListener(NfcEvents.DiscoverTag, (tag) => {
      setPinCode(tag.id);
      hanldeTagDisCover();
    });
    NfcManager.registerTagEvent();
    return () => {
      NfcManager.setEventListener(NfcEvents.DiscoverTag, null);
      NfcManager.unregisterTagEvent();
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const hanldeTagDisCover = async () => {
    const user = await getUserByRfid(pinCode);
    console.log(user, 'user');
  };
  return (
    <View>
      <Text>Đăng Nhập Người Dùng</Text>
      <Text>{pinCode}</Text>
    </View>
  );
}
