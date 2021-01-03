import getRealm from '../../utils/Realm';
import companyAPI from '../../api/companyAPI';
import * as CompanyModel from '../model/companyModels';

export const insertCompany = async (init) => {
  try {
    const res = await companyAPI.getCompany(init ? false : true);
    if (res) {
      const companyData = res.map((element) =>
        JSON.parse(element.subject_data),
      )[0];
      const realm = await getRealm(CompanyModel.CompanySchema);
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

      if (init) {
        await insertCompany();
      }
    }
  } catch (error) {
    return Promise.reject(`init company detail failed : ${error}`);
  }
};

export const insertUser = async (init) => {
  try {
    const res = await companyAPI.getUser(init ? false : true);
    if (res) {
      const User = res.map((type) => JSON.parse(type.subject_data));
      const realm = await getRealm(CompanyModel.UserSchema);
      realm.write(() => {
        User.forEach((element) => {
          let data = {
            id: element.id,
            company_id: element.company_id,
            role_id: element.role_id,
            disable: element.disable ? element.disable : 0,
            rfid: element.rfid ? element.rfid : '',
            barcode: element.barcode ? element.barcode : '',
            username: element.username,
            password: element.password ? element.password : '',
            email: element.email ? element.email : '',
            fullname: element.fullname ? element.fullname : '',
            pin_code: element.pin_code ? element.pin_code : '',
            phone: element.phone ? element.phone : '',
            role_name: element.role_name ? element.role_name : '',
          };
          init
            ? realm.create('User', data)
            : realm.create('User', data, 'modified');
        });
      });
      realm.close();

      if (init) {
        await insertUser();
      }
    }
  } catch (error) {
    return Promise.reject(`init user failed: ${error}`);
  }
};

export const insertCompanyModule = async () => {
  try {
    const res = await companyAPI.getModuleCompany();
    if (res) {
      const moduleList = res.map((element) =>
        JSON.parse(element.subject_data),
      )[0];
      const realm = await getRealm(CompanyModel.CompanyModuleSchema);

      realm.write(() => {
        moduleList.forEach((element) => {
          let data = {
            name: element,
          };
          realm.create('Module', data);
        });
      });
      realm.close();
    }
  } catch (error) {
    return Promise.reject(`init company module failed: ${error}`);
  }
};

export const insertSettingGlobal = async () => {
  try {
    const res = await companyAPI.getSettingGlobal();
    if (res) {
      const settingGlobalList = res.map((settingGlobal) =>
        JSON.parse(settingGlobal.subject_data),
      )[0];
      const realm = await getRealm(CompanyModel.SettingGlobalSchema);
      realm.write(() => {
        settingGlobalList.forEach((element) => {
          let data = {
            key: element.key,
            value: element.value,
            updated: element.updated ? element.updated : 0,
          };
          realm.create('Setting_Global', data);
        });
      });
      realm.close();
    }
  } catch (error) {
    return Promise.reject(`init setting global failed: ${error}`);
  }
};

export const getUserByRfid = async (inputRFID) => {
  try {
    const realm = await getRealm(CompanyModel.UserSchema);
    let allUsers = realm.objects('User');
    let userData = allUsers.find((user) => user.rfid === inputRFID);
    return userData;
  } catch (error) {
    return Promise.reject(`get user by rfid failed: ${error}`);
  }
};
