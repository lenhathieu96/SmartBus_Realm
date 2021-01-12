import React, {useEffect, useState} from 'react';
import {Text, FlatList, NativeModules, Alert} from 'react-native';
import {useSelector} from 'react-redux';
import 'intl';
import 'intl/locale-data/jsonp/vi-VN';
import {getPreciseDistance} from 'geolib';
const apiKey = 'AIzaSyCMQcc8wB5qX0LjgzcpdFRoiNu5HU65n0I';
import {
  getTicketForRoute,
  updateAllocation,
} from '../../database/controller/ticketControllers';

import TextButton from '../../component/TextButton';

import styles from './styles';

import RootContainer from '../../component/RootContainer';

export default function BusTicketScreen() {
  const AVERAGE_STATION_DISTANCE = 500;
  const {PrintModule} = NativeModules;

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

  const getArriveStation = (stationIndex, ticketDistance) => {
    console.log(stationIndex);
    const distance = getPreciseDistance(
      {
        latitude: vehicleProfile.current_station.lat,
        longitude: vehicleProfile.current_station.lng,
      },
      {
        latitude: vehicleProfile.stationList[45].lat,
        longitude: vehicleProfile.stationList[45].lng,
      },
      0.1,
    );
    console.log(distance);
    console.log(vehicleProfile.stationList[45]);
    // return distance + 2500 > ticketDistance
    //   ? getArriveStation(stationIndex - 1, ticketDistance)
    //   : vehicleProfile.stationList[stationIndex];
  };

  const chargeNormalTicket = async (ticketData) => {
    try {
      let maxDistance = ticketData.number_km;
      const averageStationIndex = Math.floor(
        maxDistance / AVERAGE_STATION_DISTANCE,
      );
      const arriveStation = getArriveStation(averageStationIndex, maxDistance);
      console.log(arriveStation);
      // const result = await PrintModule.printFreeTicket(
      //   'QT-NT',
      //   'dia chi ne',
      //   'so dien thoai ne',
      //   'tax code ne',
      //   'number day',
      //   'ten tram',
      //   'fullname',
      //   'fullname_customer',
      //   'time',
      //   'ngay het han',
      // );
      // if (result) {
      //   console.log('print success');
      // }
    } catch (error) {
      console.log('Error on sell ticket: ', error);
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
