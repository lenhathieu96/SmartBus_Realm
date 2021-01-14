import React, {useEffect, useState} from 'react';
import {Text, FlatList, Alert} from 'react-native';

import {useSelector} from 'react-redux';
import 'intl';
import 'intl/locale-data/jsonp/vi-VN';

import {
  getTicketForRoute,
  updateAllocation,
  insertTransaction,
} from '../../database/controller/ticketControllers';
import {getCurrentTime, format_ticket} from '../../utils/Libs';

import TextButton from '../../component/TextButton';

import styles from './styles';

import RootContainer from '../../component/RootContainer';
import {PrintBusTicket} from '../../utils/Print';

export default function BusTicketScreen() {
  const [ticketList, setTicketList] = useState([]);

  const vehicleProfile = useSelector((state) => state.vehicle);
  const user = useSelector((state) => state.user);

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

  const getArriveStation = (ticketDistance) => {
    const stationList = vehicleProfile.stationList;
    const distance =
      vehicleProfile.direction === 0
        ? vehicleProfile.current_station.distance + ticketDistance //tuyến đi
        : vehicleProfile.current_station.distance - ticketDistance; // tuyến về
    const index =
      vehicleProfile.direction === 0
        ? stationList.findIndex((station) => station.distance > distance) //tuyến đi
        : stationList.findIndex((station) => station.distance < distance); // tuyến về
    if (index >= 0) {
      return stationList[index - 1];
    } else {
      return stationList[stationList.length - 1];
    }
  };

  const chargeNormalTicket = async (ticketData) => {
    try {
      let maxDistance = ticketData.number_km;
      const allocation = await updateAllocation(ticketData.id);
      const arriveStation = getArriveStation(maxDistance);

      ticketData.start_station = vehicleProfile.current_station.address;
      ticketData.arrive_station = arriveStation.address;
      ticketData.time = getCurrentTime('format');
      ticketData.allocation = format_ticket(allocation);

      let transactionData = {
        ticket_type_id: ticketData.id,
        ticket_number: allocation,
        station_id: vehicleProfile.current_station.id,
        amount: ticketData.price,
        station_down: ticketData.arrive_station,
        type: 'pos',
        sign: ticketData.sign,
      };
      await insertTransaction(
        getCurrentTime(),
        'insert_ticket',
        'ticket',
        JSON.stringify(transactionData),
        user.main_id,
      );

      //Format ticket price for print ticket
      ticketData.price = new Intl.NumberFormat('vi-VN').format(
        ticketData.price,
      );
      // await PrintBusTicket(company, vehicleProfile, ticketData);
    } catch (error) {
      console.log('Error on purchase ticket: ', error);
      if (error.code === 'Out of Paper') {
        Alert.alert('Thông Báo!', 'Hết Giấy');
      }
    }
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
            onPress={() => chargeNormalTicket(item)}
            style={styles.btnTicket}
          />
        )}
      />
      <Text style={styles.txtInfo}>! Đối với vé tháng, vui lòng quẹt thẻ</Text>
    </RootContainer>
  );
}
