const initialState = {
  haveImei: false,
  isLogin: false,
};

const authReducer = (state = initialState, action) => {
  switch (action.type) {
    case 'SET HAVE IMEI': {
      return {...state, haveImei: true};
    }
    default: {
      return state;
    }
  }
};

export default authReducer;
