const initialState = {
  printAvailable: true,
};

const deviceReducer = (state = initialState, action) => {
  switch (action.type) {
    case 'SET PRINT AVAILABLE':
      console.log(action.payload);
      return {...state, printAvailable: action.payload};
    default:
      return state;
  }
};

export default deviceReducer;
