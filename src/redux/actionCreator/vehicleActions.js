export const setVehicleData = (vehicleData) => {
  return {
    type: 'SET VEHICLE DATA',
    payload: vehicleData,
  };
};

export const setVehicleDirection = (vehicleDirection) => {
  return {
    type: 'SET VEHICLE DIRECTION',
    payload: vehicleDirection,
  };
};

export const updateVehicleLocation = (location) => {
  return {
    type: 'UPDATE VEHICLE LOCATION',
    payload: location,
  };
};
