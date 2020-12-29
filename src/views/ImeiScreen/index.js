import React, {useEffect, useState} from 'react';
import {
  View,
  TextInput,
  ActivityIndicator,
  PermissionsAndroid,
  Alert,
  Text,
} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import IMEI from 'react-native-imei';
import Moment from 'moment';

// import selectDb from '../../Database/selectDb';
// import {insertDB} from '../../Database/insertDb';
// import {createDB} from '../../Database/createDb';
// import deleteDb from '../../Database/deleteDb';
import authAPI from '../../api/authAPI';
import companyAPI from '../../api/companyAPI';

import {
  insertBus_Station,
  insertCompanyModule,
  insertVehicle,
} from '../../database/controller/insertData';

import styles from './styles/index.css';

export default function ImeiScreen() {
  const [isLoading, setLoading] = useState();
  const [imei, setImei] = useState('');

  useEffect(() => {
    checkPermissions();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const checkPermissions = async () => {
    const permisson = await PermissionsAndroid.check(
      PermissionsAndroid.PERMISSIONS.READ_PHONE_STATE,
    );
    if (!permisson) {
      const granted = await PermissionsAndroid.request(
        PermissionsAndroid.PERMISSIONS.READ_PHONE_STATE,
      );
      if (granted) {
        getImeiDevice();
      }
    } else {
      getImeiDevice();
    }
  };

  //   const initDB = () => {
  //     createDB.createDbAllocations();
  //     createDB.createDbBus_stations();
  //     createDB.createDbCompanies();
  //     createDB.createDbRoutes();
  //     createDB.createDbTicket_types();
  //     createDB.createDbUsers();
  //     createDB.createDbVehicles();
  //     createDB.createDbRoute_bus_stations();
  //     createDB.createDbSellTicket();
  //     createDB.createDbTemp();
  //     createDB.createDbSellTiketTemp();
  //     createDB.createDbSupervisor();
  //     createDB.createDbRoute_bus_stations();
  //     createDB.createDbModuleCompany();
  //     createDB.createDbDenominations();
  //     createDB.createDbCharge_Shifts();
  //     createDB.createDbSetting_global();
  //   };

  //   const createDBByDrop = () => {
  //     createDB.createDbCompanies();
  //     createDB.createDbUsers();
  //     createDB.createDbVehicles();
  //     createDB.createDbRoute_bus_stations();
  //     createDB.createDbRoutes();
  //     createDB.createDbAllocations();
  //     createDB.createDbTicket_types();
  //     // createDb.createDbBus_stations();
  //     createDB.createDbCharge_Shifts();
  //     setTimeout(function () {
  //       checkImei(imei);
  //     }, 1500);
  //   };

  //   const dropDB = () => {
  //     //TBL, key_insert = null, key_update = null, callback
  //     deleteDb.dropTBL({TBL: 'charge_shifts'});
  //     deleteDb.dropTBL({TBL: 'allocations'});
  //     deleteDb.dropTBL({
  //       TBL: 'ticket_types',
  //       key_insert: '@from_insert_ticket',
  //       key_update: '@from_update_ticket',
  //     });
  //     deleteDb.dropTBL({
  //       TBL: 'route_bus_stations',
  //       key_insert: '@from_insert_route_station',
  //       key_update: '@from_update_route_station',
  //     });
  //     deleteDb.dropTBL({
  //       TBL: 'routes',
  //       key_insert: '@from_insert_route',
  //       key_update: '@from_update_route',
  //     });
  //     deleteDb.dropTBL({
  //       TBL: 'companies',
  //       key_insert: '@from_insert_company',
  //       key_update: '@from_update_company',
  //     });
  //     deleteDb.dropTBL({
  //       TBL: 'users',
  //       key_insert: '@from_insert_user',
  //       key_update: '@from_update_user',
  //     });
  //     deleteDb.dropTBL({
  //       TBL: 'vehicles',
  //       key_insert: '@from_insert_vehicle',
  //       key_update: '@from_update_vehicle',
  //     });
  //     // createDrop = setTimeout(function () {
  //     //   createDBByDrop();
  //     // }, 1500);
  //   };

  const getImeiDevice = async () => {
    try {
      const initRoute = await AsyncStorage.getItem('@initRoute');
      //   !initRoute ? initDB() : dropDB();
      const deviceImei = await IMEI.getImei();
      if (deviceImei && deviceImei.length > 0) {
        checkImei(deviceImei[0]);
      }
    } catch (error) {
      console.log('Error On Authenticate Imei Device', error);
    }
  };

  const initData = async () => {
    try {
      await insertVehicle('init'),
        // await insertCompany('init'),
        // await insertTickectType('init'),
        // await insertRoute('init'),
        await insertBus_Station('init');
      await insertCompanyModule();
      // await insertDenomination('init');
      // await insertDenomination_Goods('init');
      // await insertSettingGlobal('init');
      // await insertCompanyModule('init');
      // await insertUser('init');
    } catch (error) {
      console.log('Error On Init Data: ', error);
    }

    // ])
    //   .then(() => {
    //     console.log('alo');
    //     // getDataAllTicket();
    //   })
    //   .catch((error) => {
    //     console.log('Error On Init Data: ', error);
    //   });
  };

  const checkImei = async (deviceImei) => {
    setLoading(true);
    try {
      const response = await authAPI.checkImei('359261051233786');
      if (response && response.code !== 404) {
        await AsyncStorage.setItem('imei', '359261051233786');
        initData();
      } else {
        await AsyncStorage.removeItem('imei');
      }
    } catch (error) {
      setLoading(false);
      console.log('Error On Checking Imei : ', error);
      Alert.alert('Thông báo', 'Xảy ra lỗi trong quá trình xác minh IMEI');
    }
  };

  const getDataAllTicket = () => {
    console.log('get data all ticket');
    // selectDb.selectIdTicket({
    //   callback: (data) => {
    //     if (data.len > 0) {
    //       selectIdAllocation(data.ticket_type_id);
    //     } else {
    //       console.log('no ticket data');
    //     }
    //   },
    // });
  };

  const selectIdAllocation = (ticketTypeID) => {
    console.log('select id allocation');
    // let s = ticketTypeID.map(() => '?');
    // selectDb.selectIdAllocation({
    //   ticket_type_id: ticketTypeID,
    //   s,
    //   callback: PostApiTicketId,
    // });
  };

  //   const PostApiTicketId = async (data) => {
  //     if (data.array.length === 0) {
  //       Alert.alert(
  //         `${global.titleNoti}`,
  //         'Tất cả vé đang còn!',
  //         'Cập nhật lúc khác',
  //       );
  //     } else {
  //       try {
  //         const result = await companyAPI.updateTicket(data.array);
  //         if (result) {
  //           selectDb.selectAllTicket({
  //             callback: (dta) => {
  //               if (dta.len === 0) {
  //                 result.forEach((e, i) => {
  //                   insertDB.insertDataAllocations(e, null, 1);
  //                 });
  //               }
  //             },
  //           });
  //         }
  //       } catch (error) {
  //         console.log(error);
  //       }

  // postApi(
  //   `${global.url}${'/machine/ticketAllocates/update'}`,
  //   data.array,
  //   1,
  // )
  //   .then((d) => console.log(d))
  //   .catch((error) => console.log(error, 'error'));
  // .then((d) => {
  //       if (d.connection !== undefined && d.code !== undefined) {
  //         updateAllocate = undefined;
  //         ToastAndroid.show('Cập nhật vé lỗi!', ToastAndroid.SHORT);
  //       } else {
  //         clearTimeout(timeoutGoLogin);
  //         updateAllocate = undefined;
  //         selectDb.selectAllTicket({
  //           callback: (dta) => {
  //             if (dta.len === 0) {
  //               ToastAndroid.show('Đã cập serial vé', ToastAndroid.SHORT);
  //               d.forEach((e, i) => {
  //                 insertDb.insertDataAllocations(e, null, 1);
  //                 // eslint-disable-next-line eqeqeq
  //                 if (i + 1 == d.length) {
  //                   _this.goLoginUser();
  //                 }
  //               });
  //             }
  //           },
  //         });
  //         _this.setState({isLoading: false, disabled_login: false});

  //         timeoutGoLogin = setTimeout(function () {
  //           _this.goLoginUser();
  //         }, 3000);
  //       }
  //     });
  //   }
  // }); // setState
  // }
  //   };

  const getApiTicketGoods = () => {
    AsyncStorage.setItem('@qtyNumGoods', '-1');
    // getApi(`${global.url}${'/machine/get/transaction/check/goods'}`).then(
    //   (data) => {
    //     if (data.code === undefined && data.connection === undefined) {
    //       let num = {
    //         stt: Number(data),
    //         day: Moment().format('YYYY-MM-DD'),
    //       };
    //       AsyncStorage.setItem('@qtyNumGoods', JSON.stringify(num));
    //     }
    //   },
    // );
  };

  return isLoading ? (
    <View style={styles.loadingContainer}>
      <ActivityIndicator size="large" color="blue" />
      <Text style={styles.txtLoading}>
        Đang khởi tạo dữ liệu,{'\n'} vui lòng đợi trong giây lát...!
      </Text>
    </View>
  ) : (
    <View style={styles.mainContainer}>
      <View style={{justifyContent: 'space-between'}}>
        <TextInput
          style={styles.textInput}
          placeholder="IMEI"
          onChangeText={(imei) => setImei(imei)}
          value={imei}
          underlineColorAndroid="transparent"
        />
      </View>
    </View>
  );
}
