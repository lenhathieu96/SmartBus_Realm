import axiosClient from './axiosClient';

const busAPI = {
  getVehicle: (isUpdate) => {
    const url = `/machine/update/database?from=0&subject_type=vehicle&action=${
      isUpdate ? 'update' : 'create'
    }`;
    return axiosClient.get(url);
  },

  getRoutes: (isUpdate) => {
    const url = `/machine/update/database?from=0&subject_type=route&action=${
      isUpdate ? 'update' : 'create'
    }`;
    return axiosClient.get(url);
  },

  getBusStation: (isUpdate) => {
    const url = `/machine/update/database?from=0&subject_type=bus_station&action=${
      isUpdate ? 'update' : 'create'
    }`;
    return axiosClient.get(url);
  },
};

export default busAPI;
