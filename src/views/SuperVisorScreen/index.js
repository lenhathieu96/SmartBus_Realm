import React from 'react';
import {View, Text} from 'react-native';
import TextButton from '../../component/TextButton';

export default function SuperVisorScreen({navigation}) {
  return (
    <View>
      <Text>super visor screen</Text>
      <TextButton text="Đăng Xuất" onPress={() => navigation.goBack()} />
    </View>
  );
}
