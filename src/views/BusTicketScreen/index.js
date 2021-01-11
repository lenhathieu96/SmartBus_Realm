import React, {useEffect, useState} from 'react';
import {Text, FlatList, NativeModules} from 'react-native';
import {useSelector} from 'react-redux';
import 'intl';
import 'intl/locale-data/jsonp/vi-VN';

import {getTicketForRoute} from '../../database/controller/ticketControllers';

import TextButton from '../../component/TextButton';

import styles from './styles';

import RootContainer from '../../component/RootContainer';

export default function BusTicketScreen() {
  const {PrintModule} = NativeModules;

  const [ticketList, setTicketList] = useState([]);

  const vehicleProfile = useSelector((state) => state.vehicle);
  const printAvailable = useSelector((state) => state.device.printAvailable);

  useEffect(() => {
    getTicketData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const getTicketData = async () => {
    try {
      const ticketArr = await getTicketForRoute(vehicleProfile.route_id);
      if (ticketArr) {
        setTicketList(ticketArr);
      }
    } catch (error) {
      console.log('Error on get ticket data: ', error);
    }
  };

  const sellTicket = (data) => {
    PrintModule.printTicketChargeFree(
      'QT-NT',
      'dia chi ne',
      'so dien thoai ne',
      'tax code ne',
      'number day',
      'ten tram',
      'fullname',
      'fullname_customer',
      'time',
      'ngay het han',
    );
  };

  return (
    <RootContainer>
      <Text style={styles.title}>Danh Sách Các Loại Vé</Text>
      <FlatList
        contentContainerStyle={styles.ticketList}
        numColumns={2}
        data={ticketList}
        keyExtractor={(item, index) => index.toString()}
        renderItem={({item}) => (
          <TextButton
            text={`${new Intl.NumberFormat('vi-VN').format(item.price)} VNĐ`}
            textStyle={styles.txtBtnTicket}
            onPress={() => sellTicket(item)}
            style={styles.btnTicket}
          />
        )}
      />
      <Text style={styles.title}>! Đối với vé tháng, vui lòng quẹt thẻ</Text>
    </RootContainer>
  );
}
