export const CompanyModuleSchema = {
  name: 'Module',
  properties: {
    name: 'string',
  },
};

export const CompanySchema = {
  name: 'Company',
  primaryKey: 'id',
  properties: {
    id: 'int',
    name: 'string',
    fullname: 'string',
    address: 'string',
    phone: 'string',
    tax_code: 'string',
    print_at: 'string',
    email: 'string',
  },
};

export const SettingGlobalSchema = {
  name: 'Setting_Global',
  properties: {
    key: 'string',
    value: 'string',
    updated: 'int',
  },
};

export const UserSchema = {
  name: 'User',
  primaryKey: 'id',
  properties: {
    id: 'int',
    company_id: 'int',
    role_id: 'int',
    disable: 'int',
    rfid: 'string',
    barcode: 'string',
    username: 'string',
    password: 'string',
    email: 'string',
    fullname: 'string',
    pin_code: 'string',
    phone: 'string',
    role_name: 'string',
  },
};
