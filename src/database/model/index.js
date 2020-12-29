export const Bus_StationSchema = {
  name: 'Bus_Staion',
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

export const CompanyModuleSchema = {
  name: 'Module',
  properties: {
    name: 'string',
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
