const initialState = {
  id: 0,
  route_id: 0,
  license_plates: '',
  direction: 0,
  departStation: '',
  stationList: [],
};

const vehicleReducer = (state = initialState, action) => {
  switch (action.type) {
    case 'SET VEHICLE DATA':
      const {id, route_id, license_plates} = action.payload;
      return {...state, id, route_id, license_plates};

    case 'SET VEHICLE DIRECTION':
      const {direction, departStation, stationList} = action.payload;
      return {...state, direction, departStation, stationList};

    default:
      return state;
  }
};

export default vehicleReducer;
