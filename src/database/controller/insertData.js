import Realm from 'realm';

import {
  Bus_StationSchema,
  CompanyModuleSchema,
  VechicleSchema,
  CompanySchema,
} from '../model';
import {updateBus_Station, updateVehicle} from './updateData';
import companyAPI from '../../api/companyAPI';

export const insertBus_Station = async (init = null) => {
  try {
    const res = await companyAPI.getBusStation();
    if (res) {
      const stationList = res.map((element) =>
        JSON.parse(element.subject_data),
      );
      Realm.open({
        schema: [Bus_StationSchema],
      }).then((realm) => {
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
            realm.create('Bus_Staion', data);
          });
        });
        realm.close();
      });
      if (init !== null) {
        await updateBus_Station();
      }
    } else {
      throw Error('Failed On Get Bus Station API');
    }
  } catch (error) {
    return Promise.reject(`Error On Init Bus Station : ${error}`);
  }
};

export const insertCompanyModule = async () => {
  try {
    const res = await companyAPI.getModuleCompany();
    if (res) {
      const moduleList = res.map((element) => JSON.parse(element.subject_data));
      Realm.open({
        schema: [CompanyModuleSchema],
      }).then((realm) => {
        realm.write(() => {
          moduleList[0].forEach((element) => {
            let data = {
              name: element,
            };
            realm.create('Module', data);
          });
        });
        realm.close();
      });
    } else {
      throw Error('Failed On Get Company Module API');
    }
  } catch (error) {
    return Promise.reject(`Error On Init Company Module : ${error}`);
  }
};

export const insertVehicle = async (init = null) => {
  try {
    const res = await companyAPI.getVehicle();
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
              barcode: element.barcode ? element.barcode : '',
              license_plates: element.license_plates,
              blt_mac: element.blt_mac ? element.blt_mac : '',
              blt_pwd: element.blt_pwd ? element.blt_pwd : '',
              route_id: element.route_id ? element.route_id : 0,
            };
            realm.create('Vehicle', data);
          });
        });
        realm.close();
      });
      if (init !== null) {
        await updateVehicle();
      }
    } else {
      throw Error('Failed On Get Vehicle API');
    }
  } catch (error) {
    return Promise.reject(`Error On Init Company Module : ${error}`);
  }
};

export const insertCompany = async (init) => {
  try {
    const res = await companyAPI.getCompany(init ? false : true);
    if (res) {
      const companyData = res.map((element) =>
        JSON.parse(element.subject_data),
      )[0];

      Realm.open({
        schema: [CompanySchema],
      }).then((realm) => {
        realm.write(() => {
          let data = {
            id: companyData.id,
            name: companyData.name,
            fullname: companyData.fullname,
            address: companyData.address,
            phone: companyData.phone,
            tax_code: companyData.tax_code,
            print_at: companyData.print_at ? companyData.print_at : '',
            email: companyData.email,
          };
          init
            ? realm.create('Company', data)
            : realm.create('Company', data, 'modified');
        });
        realm.close();
      });

      if (init) {
        await insertCompany();
      }
    } else {
      throw Error('Failed On Get Company API');
    }
  } catch (error) {
    return Promise.reject(`Error On Init Company Module : ${error}`);
  }
};
