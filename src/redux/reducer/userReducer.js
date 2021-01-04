const initialState = {
  main_id: '',
  main_rfid: '',
  driver_name: '',
  subDriver_name: '',
  active: 0,
  company: {},
  company_module: [],
};

const userReducer = (state = initialState, action) => {
  switch (action.type) {
    case 'SET USER DATA':
      return {...action.payload};
    default:
      return state;
  }
};

export default userReducer;
