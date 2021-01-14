import axiosClient from './axiosClient';

const companyAPI = {
  getCompany: (isUpdate) => {
    const url = `/machine/update/database?from=0&subject_type=company&action=${
      isUpdate ? 'update' : 'create'
    }`;
    return axiosClient.get(url);
  },

  getSettingGlobal: (from) => {
    const url = `/machine/update/database?from=${from}&subject_type=setting_global&action=create`;
    return axiosClient.get(url);
  },

  getModuleCompany: () => {
    const url =
      '/machine/update/database?from=0&subject_type=module_company&action=create';
    return axiosClient.get(url);
  },

  getUser: (isUpdate) => {
    const url = `/machine/update/database?from=0&subject_type=user&action=${
      isUpdate ? 'update' : 'create'
    }`;
    return axiosClient.get(url);
  },

  updateActivity: (userData) => {
    const url = '/machine/update/activities';
    return axiosClient.post(url, userData, {
      headers: {
        'Content-Type': 'application/json',
      },
    });
  },
};

export default companyAPI;
