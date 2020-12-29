import axiosClient from './axiosClient';

const companyAPI = {
  getVehicle: (from, isUpdate = false) => {
    const url = `/machine/update/database?from=${from}&subject_type=vehicle&action=${
      isUpdate ? 'update' : 'create'
    }`;
    return axiosClient.get(url);
  },

  getCompany: (from, isUpdate = false) => {
    const url = `/machine/update/database?from=${from}&subject_type=company&action=${
      isUpdate ? 'update' : 'create'
    }`;
    return axiosClient.get(url);
  },

  getTicketType: (from) => {
    const url = `/machine/update/database?from=${from}&subject_type=ticket_type&action=create`;
    return axiosClient.get(url);
  },

  getRoutes: (from, isUpdate = false) => {
    const url = `/machine/update/database?from=${from}&subject_type=route&action=${
      isUpdate ? 'update' : 'create'
    }`;
    return axiosClient.get(url);
  },

  getBusStation: (isUpdate = false) => {
    const url = `/machine/update/database?from=0&subject_type=bus_station&action=${
      isUpdate ? 'update' : 'create'
    }`;
    return axiosClient.get(url);
  },

  getDenomination: (from) => {
    const url = `/machine/update/database?from=${from}&subject_type=denomination&action=create`;
    return axiosClient.get(url);
  },

  getDenomination_Goods: (from) => {
    const url = `/machine/update/database?from=${from}&subject_type=denomination_goods&action=create`;
    return axiosClient.get(url);
  },

  getSettingGlobal: (from) => {
    const url = `/machine/update/database?from=${from}&subject_type=setting_global&action=create`;
    return axiosClient.get(url);
  },

  getModuleCompany: () => {
    const url = `/machine/update/database?from=0&subject_type=module_company&action=create`;
    return axiosClient.get(url);
  },

  getUser: (from, isUpdate = false) => {
    const url = `/machine/update/database?from=${from}&subject_type=user&action=${
      isUpdate ? 'update' : 'create'
    }`;
    return axiosClient.get(url);
  },

  updateTicketType: (from) => {
    const url = `/machine/update/database?from='}${from}&subject_type=ticket_type&action=update`;
    return axiosClient.get(url);
  },

  updateTicket: (ticketArray) => {
    const url = `/machine/ticketAllocates/update`;
    return axiosClient.post(url, JSON.stringify(ticketArray));
  },
};

export default companyAPI;
