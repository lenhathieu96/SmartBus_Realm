import React, {useEffect, useState} from 'react';
import {Text, FlatList} from 'react-native';
import {useSelector} from 'react-redux';
import 'intl';
import 'intl/locale-data/jsonp/vi-VN';

import {
  getTicketForRoute,
  updateAllocation,
} from '../../database/controller/ticketControllers';

import TextButton from '../../component/TextButton';

import styles from './styles';

import RootContainer from '../../component/RootContainer';

export default function BusTicketScreen() {
  const [ticketList, setTicketList] = useState([]);

  const vehicleProfile = useSelector((state) => state.vehicle);
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

  const sellTicket = async (ticket) => {
    await updateAllocation(ticket.id);
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
      <Text style={styles.txtInfo}>! Đối với vé tháng, vui lòng quẹt thẻ</Text>
    </RootContainer>
  );
}
