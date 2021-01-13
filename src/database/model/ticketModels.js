export const TicketTypeSchema = {
  name: 'Ticket_Type',
  primaryKey: 'id',
  properties: {
    id: 'int',
    company_id: 'int',
    name: 'string',
    description: 'string',
    sign: 'string',
    sign_form: 'string',
    order_code: 'string',
    price: 'int',
    type: 'int',
    charge_limit: 'int',
    number_km: 'int',
  },
};

export const DenominationSchema = {
  name: 'Denomination',
  properties: {
    price: 'int[]',
    type: 'string',
  },
};

export const AllocationSchema = {
  name: 'Allocation',
  primaryKey: 'ticket_type_id',
  properties: {
    company_id: 'int',
    device_id: 'int',
    ticket_type_id: 'int',
    start_number: 'int',
    end_number: 'int',
  },
};

export const TransactionSchema = {
  name: 'Transaction',
  primaryKey: 'timestamp',
  properties: {
    timestamp: 'int',
    action: 'string',
    subject_type: 'string',
    subject_data: 'string',
    user_id: 'int',
    is_upload: 'bool?', //optional property
  },
};
