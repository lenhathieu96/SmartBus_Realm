import getRealm from '../../utils/Realm';
import busAPI from '../../api/busAPI';
import * as VehicleModel from '../model/vehicleModel';

export const insertBus_Station = async (init) => {
  try {
    const res = await busAPI.getBusStation(init ? false : true);
    if (res) {
      const stationList = res.map((element) =>
        JSON.parse(element.subject_data),
      );
      const realm = await getRealm([VehicleModel.Bus_StationSchema]);
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
            ? realm.create('Bus_Station', data)
            : realm.create('Bus_Station', data, 'modified');
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
      const realm = await getRealm([VehicleModel.VechicleSchema]);
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
      const realm = await getRealm([VehicleModel.RouteSchema]);
      realm.write(() => {
        routeList.forEach((route) => {
          let data = {
            id: route.id,
            company_id: route.company_id,
            name: route.name,
            start_time: route.start_time,
            end_time: route.end_time,
            ticket_data: route.ticket_data ? JSON.parse(route.ticket_data) : [], //>??????????????????
            distance_scan: route.distance_scan ? route.distance_scan : 0,
            timeout_sound: route.timeout_sound ? route.timeout_sound : 0,
            number: route.number,
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

export const insertRouteBusStation = async () => {
  try {
    const res = await busAPI.getRouteBusStation();
    if (res) {
      const stationList = res.map((element) =>
        JSON.parse(element.subject_data),
      );
      const realm = await getRealm([VehicleModel.Route_Bus_StationSchema]);
      realm.write(() => {
        stationList.forEach((station) => {
          let data = {
            id: station.id,
            route_id: station.route_id,
            bus_station_id: station.bus_station_id,
          };
          realm.create('Route_Bus_Station', data);
        });
      });
      realm.close();
    }
  } catch (error) {
    return Promise.reject(`init routes bus station failed: ${error}`);
  }
};

export const getVehicleByRfid = async (inputRFID) => {
  try {
    const realm = await getRealm([
      VehicleModel.VechicleSchema,
      VehicleModel.RouteSchema,
    ]);
    let allVehicles = realm.objects('Vehicle');
    let vehicleData = allVehicles.find((vehicle) => vehicle.rfid === inputRFID);
    if (vehicleData) {
      // Tìm tuyến của xe
      let allRoute = realm.objects('Routes');
      let vehicleRoute = allRoute.find(
        (route) => route.id === vehicleData.route_id,
      );
      if (vehicleRoute) {
        vehicleData.route_number = vehicleRoute.number;
        const result = JSON.parse(JSON.stringify(vehicleData));
        realm.close();
        return result;
      } else {
        realm.close();
        return null;
      }
    } else {
      realm.close();
      return null;
    }
  } catch (error) {
    return Promise.reject(`get vehicle by rfid failed: ${error}`);
  }
};

export const getDepartStation = async (routeID) => {
  try {
    const realm = await getRealm([
      VehicleModel.Route_Bus_StationSchema,
      VehicleModel.Bus_StationSchema,
    ]);
    let allStationID = realm.objects('Route_Bus_Station');
    //Danh sách toàn bộ các trạm theo tuyến
    let stationWithRouteID = allStationID.filtered(`route_id = ${routeID}`);

    let conditions = `id=`;
    for (let i = 0; i < stationWithRouteID.length; i++) {
      if (i === stationWithRouteID.length - 1) {
        conditions += `${stationWithRouteID[i].bus_station_id} `;
      } else {
        conditions += `${stationWithRouteID[i].bus_station_id} OR id=`;
      }
    }
    let allStation = realm.objects('Bus_Station');
    let stationData = allStation.filtered(conditions);
    //Danh sách thông tin các bến chiều đi
    let departStations = stationData
      .filtered('direction = 1')
      .sorted('station_order');
    //Danh sách thông tin các bến chiều về
    let returnStations = stationData
      .filtered('direction = 0')
      .sorted('station_order');
    let result = {
      depart: JSON.parse(JSON.stringify(departStations)),
      return: JSON.parse(JSON.stringify(returnStations)),
    };
    realm.close();
    return result;
  } catch (error) {
    return Promise.reject(`get depart station failed: ${error}`);
  }
};
