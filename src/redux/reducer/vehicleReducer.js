const initialState = {
  id: 0,
  route_id: 0,
  rfid: 0,
  license_plates: '',
  direction: 0,
  departStation: '',
  route_number: 0,
  location: {},
  stationList: [],
};

const vehicleReducer = (state = initialState, action) => {
  switch (action.type) {
    case 'SET VEHICLE DATA':
      const {id, route_id, license_plates, route_number, rfid} = action.payload;
      return {...state, id, route_id, license_plates, route_number, rfid};

    case 'SET VEHICLE DIRECTION':
      const {direction, departStation, stationList} = action.payload;
      return {...state, direction, departStation, stationList};
    case 'UPDATE VEHICLE LOCATION':
      return {...state, location: action.payload};

    default:
      return state;
  }
};

export default vehicleReducer;
