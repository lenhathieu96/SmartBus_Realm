import axiosClient from './axiosClient';

const ticketAPI = {
  getDenomination: () => {
    const url =
      '/machine/update/database?from=0&subject_type=denomination&action=create';
    return axiosClient.get(url);
  },

  getDenomination_Goods: () => {
    const url =
      '/machine/update/database?from=0&subject_type=denomination_goods&action=create';
    return axiosClient.get(url);
  },

  getTicketType: (isUpdate) => {
    const url = `/machine/update/database?from=0&subject_type=ticket_type&action=${
      isUpdate ? 'update' : 'create'
    }`;
    return axiosClient.get(url);
  },

  updateAllocateTicket: (ticketArray) => {
    const url = '/machine/ticketAllocates/update';
    return axiosClient.post(url, ticketArray);
  },
};

export default ticketAPI;
