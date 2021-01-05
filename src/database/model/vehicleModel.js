export const RouteSchema = {
  name: 'Routes',
  primaryKey: 'id',
  properties: {
    id: 'int',
    company_id: 'int',
    name: 'string',
    start_time: 'string',
    end_time: 'string',
    ticket_data: 'int[]',
    distance_scan: 'int',
    timeout_sound: 'int',
    number: 'string',
  },
};

export const VechicleSchema = {
  name: 'Vehicle',
  primaryKey: 'id',
  properties: {
    id: 'int',
    company_id: 'int',
    rfid: 'string',
    barcode: 'string',
    license_plates: 'string',
    blt_mac: 'string',
    blt_pwd: 'string',
    route_id: 'int',
  },
};

export const Bus_StationSchema = {
  name: 'Bus_Station',
  primaryKey: 'id',
  properties: {
    id: 'int',
    name: 'string',
    address: 'string',
    lat: 'double',
    lng: 'double',
    station_order: 'int',
    direction: 'int',
    url_sound: 'string',
    company_id: 'int',
    distance: 'int',
  },
};

export const Route_Bus_StationSchema = {
  name: 'Route_Bus_Station',
  properties: {
    id: 'int',
    route_id: 'int',
    bus_station_id: 'int',
  },
};
