import getRealm from '../../utils/Realm';
import ticketAPI from '../../api/ticketAPI';
import * as TicketModel from '../model/ticketModels';
import {RouteSchema} from '../model/vehicleModel';
import busAPI from '../../api/busAPI';

export const insertDenomination = async (init) => {
  try {
    const res = init
      ? await ticketAPI.getDenomination()
      : await ticketAPI.getDenomination_Goods();
    if (res) {
      const denomination = res.map((element) =>
        JSON.parse(element.subject_data),
      )[0];
      if (denomination.length > 0) {
        const realm = await getRealm([TicketModel.DenominationSchema]);
        realm.write(() => {
          let data = {
            price: denomination,
            type: init ? '' : 'goods',
          };
          realm.create('Denomination', data);
        });
        realm.close();
      }

      if (init) {
        await insertDenomination();
      }
    }
  } catch (error) {
    return Promise.reject(`init denomination failed: ${error}`);
  }
};

export const insertTickectType = async (init) => {
  try {
    const res = await ticketAPI.getTicketType(init ? false : true);
    if (res) {
      const typeList = res.map((type) => JSON.parse(type.subject_data));
      const realm = await getRealm([TicketModel.TicketTypeSchema]);
      realm.write(() => {
        typeList.forEach((element) => {
          let data = {
            id: element.id,
            company_id: element.company_id,
            name: element.name,
            description: element.description ? element.description : '',
            sign: element.sign,
            sign_form: element.sign_form ? element.sign_form : '',
            order_code: element.order_code,
            price: element.price,
            type: element.type,
            charge_limit: element.charge_limit ? element.charge_limit : 0,
            number_km: element.number_km ? element.number_km : 0,
          };
          init
            ? realm.create('Ticket_Type', data)
            : realm.create('Ticket_Type', data, 'modified');
        });
      });
      realm.close();

      if (init) {
        await insertTickectType();
      }
    }
  } catch (error) {
    return Promise.reject(`init routes failed: ${error}`);
  }
};

export const getAllTicketTypeID = async () => {
  try {
    const realm = await getRealm([TicketModel.TicketTypeSchema]);
    let allTicketType = realm.objects('Ticket_Type');
    if (allTicketType && allTicketType.length > 0) {
      let result = allTicketType.map((item) => item.id);
      realm.close();
      return result;
    } else {
      realm.close();
      throw new Error('empty ticket type');
    }
  } catch (error) {
    return Promise.reject(`get all ticket type id failed: ${error}`);
  }
};

export const insertTicketAllocation = async (ticketTypeArr) => {
  try {
    const ticketAllocationArr = await ticketAPI.updateAllocateTicket(
      ticketTypeArr,
    );
    const realm = await getRealm([TicketModel.AllocationSchema]);
    realm.write(() => {
      ticketAllocationArr.forEach((ticketType) => {
        let data = {
          company_id: ticketType.company_id,
          device_id: ticketType.device_id,
          ticket_type_id: ticketType.ticket_type_id,
          start_number: ticketType.start_number,
          end_number: ticketType.end_number,
        };
        realm.create('Allocation', data);
      });
    });
    realm.close();
  } catch (error) {
    return Promise.reject(`insert ticket allocation failed: ${error}`);
  }
};

export const getTicketForRoute = async (routeID) => {
  try {
    const realm = await getRealm([RouteSchema, TicketModel.TicketTypeSchema]);
    let allRoute = realm.objects('Routes');
    let route = allRoute.find((item) => item.id === routeID);
    let ticketArr = route.ticket_data;

    let conditions = `id=`;
    for (let i = 0; i < ticketArr.length; i++) {
      if (i === ticketArr.length - 1) {
        conditions += `${ticketArr[i]}`;
      } else {
        conditions += `${ticketArr[i]} OR id=`;
      }
    }
    let allTicketType = realm.objects('Ticket_Type');
    let ticketAvaiableArr = allTicketType.filtered(conditions);
    let result = JSON.parse(JSON.stringify(ticketAvaiableArr));
    realm.close();
    return result;
  } catch (error) {
    return Promise.reject(`get ticket for route failed: ${error}`);
  }
};

// Kiểm tra và cấp thêm vé
export const updateAllocation = async (ticketID) => {
  try {
    const realm = await getRealm([TicketModel.AllocationSchema]);
    const allAllocations = realm.objects('Allocation');
    const ticketAllocation = allAllocations.find(
      (item) => item.ticket_type_id === ticketID,
    );
    if (ticketAllocation) {
      realm.write(() => {
        ticketAllocation.start_number = ticketAllocation.start_number += 1;
        realm.create('Allocation', ticketAllocation, 'modified');
      });
      let result = JSON.parse(JSON.stringify(ticketAllocation));
      realm.close();
      return result.start_number;
    } else {
      realm.close();
      throw 'No Ticket Found';
    }
  } catch (error) {
    return Promise.reject(` update ticket allocation failed: ${error}`);
  }
};
