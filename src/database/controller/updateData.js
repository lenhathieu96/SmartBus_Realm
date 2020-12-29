import Realm from 'realm';
import {Bus_StationSchema, VechicleSchema} from '../model';
import companyAPI from '../../api/companyAPI';

export const updateBus_Station = async () => {
  try {
    const res = await companyAPI.getBusStation(true);
    if (res) {
      // const realm = await getRealm()
      const dataList = res.map((element) => JSON.parse(element.subject_data));
      Realm.open({
        schema: [Bus_StationSchema],
      }).then((realm) => {
        realm.write(() => {
          dataList.forEach((element) => {
            let data = {
              id: element.id,
              name: element.name,
              address: element.address,
              lat: element.position.coordinates[1],
              lng: element.position.coordinates[0],
              station_order: +element.station_order,
              direction: element.direction,
              url_sound: element.url_sound ? element.url_sound : '',
              company_id: res[0].company_id,
              distance: element.distance,
            };
            realm.create('Bus_Staion', data, 'modified');
          });
        });
        realm.close();
      });
    }
  } catch (error) {
    return Promise.reject(`Error On Init Bus Station : ${error}`);
  }
};

export const updateVehicle = async () => {
  try {
    const res = await companyAPI.getVehicle(true);
    if (res) {
      const vehicleList = res.map((element) =>
        JSON.parse(element.subject_data),
      );
      Realm.open({
        schema: [VechicleSchema],
      }).then((realm) => {
        realm.write(() => {
          vehicleList.forEach((element) => {
            let data = {
              id: element.id,
              company_id: element.company_id,
              rfid: element.rfid,
              barcode: element.barcode,
              license_plates: element.license_plates,
              blt_mac: element.blt_mac ? element.blt_mac : '',
              blt_pwd: element.blt_pwd ? element.blt_pwd : '',
              route_id: element.route_id,
            };
            realm.create('Vehicle', data, 'modified');
          });
        });
        realm.close();
      });
    }
  } catch (error) {
    return Promise.reject(`Error On Init Bus Station : ${error}`);
  }
};
