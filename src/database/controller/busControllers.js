import getRealm from '../../utils/Realm';
import busAPI from '../../api/busAPI';
import * as BusModel from '../model/busModels';

export const insertBus_Station = async (init) => {
  try {
    const res = await busAPI.getBusStation(init ? false : true);
    if (res) {
      const stationList = res.map((element) =>
        JSON.parse(element.subject_data),
      );
      const realm = await getRealm(BusModel.Bus_StationSchema);
      realm.write(() => {
        stationList.forEach((element) => {
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
          init
            ? realm.create('Bus_Staion', data)
            : realm.create('Bus_Staion', data, 'modified');
        });
      });
      realm.close();
      if (init) {
        await insertBus_Station();
      }
    }
  } catch (error) {
    return Promise.reject(`Init bus station failed : ${error}`);
  }
};

export const insertVehicle = async (init) => {
  try {
    const res = await busAPI.getVehicle(init ? false : true);
    if (res) {
      const vehicleList = res.map((element) =>
        JSON.parse(element.subject_data),
      );
      const realm = await getRealm(BusModel.VechicleSchema);
      realm.write(() => {
        vehicleList.forEach((element) => {
          let data = {
            id: element.id,
            company_id: element.company_id,
            rfid: element.rfid,
            barcode: element.barcode ? element.barcode : '',
            license_plates: element.license_plates,
            blt_mac: element.bluetooth_mac_add ? element.bluetooth_mac_add : '',
            blt_pwd: element.bluetooth_pass ? element.bluetooth_pass : '',
            route_id: element.route_id ? element.route_id : 0,
          };
          init
            ? realm.create('Vehicle', data)
            : realm.create('Vehicle', data, 'modified');
        });
      });
      realm.close();
      if (init) {
        await insertVehicle();
      }
    }
  } catch (error) {
    return Promise.reject(`init vehicle failed : ${error}`);
  }
};

export const insertRoutes = async (init) => {
  try {
    const res = await busAPI.getRoutes(init ? false : true);
    if (res) {
      const routeList = res.map((element) => JSON.parse(element.subject_data));
      const realm = await getRealm(BusModel.RouteSchema);
      realm.write(() => {
        routeList.forEach((element) => {
          let data = {
            id: element.id,
            company_id: element.company_id,
            name: element.name,
            start_time: element.start_time,
            end_time: element.end_time,
            ticket_data: JSON.parse(element.ticket_data),
            distance_scan: element.distance_scan ? element.distance_scan : 0,
            timeout_sound: element.timeout_sound ? element.timeout_sound : 0,
            number: element.number,
          };
          init
            ? realm.create('Routes', data)
            : realm.create('Routes', data, 'modified');
        });
      });
      realm.close();

      if (init) {
        await insertRoutes();
      }
    }
  } catch (error) {
    return Promise.reject(`init routes failed: ${error}`);
  }
};

export const getVehicleByRfid = async (inputRFID) => {
  try {
    const realm = await getRealm(BusModel.VechicleSchema);
    let allVehicles = realm.objects('Vehicle');
    let vehicleData = allVehicles.find((vehicle) => vehicle.rfid === inputRFID);
    const result = JSON.parse(JSON.stringify(vehicleData));
    realm.close();
    return result;
  } catch (error) {
    return Promise.reject(`get vehicle by rfid failed: ${error}`);
  }
};
