const initialState = {
  id: 0,
  route_id: 0,
  rfid: 0,
  license_plates: '',
  direction: 0,
  departStation: '',
  route_number: 0,

  current_station_index: 0,
  stationList: [],
  current_station: '',
  next_station: '',
};

const vehicleReducer = (state = initialState, action) => {
  switch (action.type) {
    case 'SET VEHICLE DATA':
      const {id, route_id, license_plates, route_number, rfid} = action.payload;
      return {...state, id, route_id, license_plates, route_number, rfid};

    case 'SET VEHICLE DIRECTION':
      const {direction, departStation, stationList} = action.payload;
      return {
        ...state,
        direction,
        departStation,
        stationList,
        current_station: stationList[0],
        next_station: stationList[1],
      };

    case 'UPDATE CURRENT STATION':
      let stationIndex =
        state.current_station_index < state.stationList.length - 1
          ? (state.current_station_index += 1)
          : state.current_station_index;
      return {
        ...state,
        current_station_index: stationIndex,
        current_station: state.stationList[stationIndex],
        next_station:
          stationIndex < state.stationList.length - 1
            ? state.stationList[(stationIndex += 1)]
            : state.stationList[stationIndex],
      };

    default:
      return state;
  }
};

export default vehicleReducer;
