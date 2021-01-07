const initialState = {
  main_id: '',
  main_rfid: '',
  driver_name: '',
  subDriver_name: '',
  driver_phone: '',
  subDriver_phone: '',
  active: 0,
  company: {},
  company_settingGlobal: [],
  company_module: [],
};

const userReducer = (state = initialState, action) => {
  switch (action.type) {
    case 'SET USER DATA':
      return Object.assign(state, action.payload);
    case 'UPDATE SETTING GLOBAL':
      return {...state, company_settingGlobal: action.payload};
    default:
      return state;
  }
};

export default userReducer;
