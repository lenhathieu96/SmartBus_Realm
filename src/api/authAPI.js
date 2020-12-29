import axiosclient from './axiosClient';

const authAPI = {
  checkImei: (imei) => {
    const url = '/machine/update/database?from=0&subject_type=ticket_type';
    return axiosclient.get(url, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
        'X-IMEI': `${imei}`,
      },
    });
  },
};

export default authAPI;
