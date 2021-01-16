import getRealm from '../../utils/Realm';
import ticketAPI from '../../api/ticketAPI';
import * as TicketModel from '../model/ticketModels';
import {RouteSchema} from '../model/vehicleModel';

//Denomination Schema ==================================================================================================
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
    // ko thể return lỗi
    // đối với các nhà xe không đăng ký vé hàng, api sẽ trả về lỗi gây kẹt app tầng ngoài (thông báo lấy dữ liêu lỗi)
    //   return Promise.reject(`init denomination failed: ${error}`);
  }
};

// Ticket Type Schema ==================================================================================================
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
            type: element.type ? element.type : 0,
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
    return Promise.reject(`init ticket types failed: ${error}`);
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

// Allocation Schema - Kiểm tra và cấp thêm vé ==========================================================================
export const insertTicketAllocation = async (ticketTypeArr) => {
  try {
    const ticketAllocationArr = await ticketAPI.updateAllocateTicket(
      ticketTypeArr,
    );
    console.log(ticketAllocationArr[0]);
    const realm = await getRealm([TicketModel.AllocationSchema]);
    realm.write(() => {
      ticketAllocationArr.forEach((ticketType) => {
        let data = {
          company_id: ticketType.company_id,
          device_id: ticketType.device_id,
          ticket_type_id: ticketType.ticket_type_id,
          start_number: ticketType.start_number,
          end_number: ticketType.end_number,
          haveQueue: false,
        };
        realm.create('Allocation', data);
      });
    });
    realm.close();
  } catch (error) {
    return Promise.reject(`insert ticket allocation failed: ${error}`);
  }
};

//Kiểm tra số lượng vé còn
export const getTicketAllocation = async (ticketID) => {
  try {
    const realm = await getRealm([TicketModel.AllocationSchema]);
    const allAllocations = realm.objects('Allocation');
    const ticketAllocation = allAllocations.find(
      (item) => item.ticket_type_id === ticketID,
    );
    if (ticketAllocation) {
      if (ticketAllocation.end_number - ticketAllocation.start_number >= 100) {
        //get new ticket allocation - Cấp thêm vé
        let allocation = JSON.parse(JSON.stringify(ticketAllocation));
        realm.close();
        await updateTicketAllocation(allocation);
      } else {
        ticketAllocation.start_number = ticketAllocation.start_number += 1;
        realm.write(() => {
          realm.create('Allocation', ticketAllocation, 'modified');
        });
        let result = JSON.parse(JSON.stringify(ticketAllocation));
        realm.close();
        return result.start_number;
      }
    } else {
      realm.close();
      throw 'Không tìm thấy vé';
    }
  } catch (error) {
    return Promise.reject(`get ticket allocation failed: ${error}`);
  }
};

//Cấp thêm vé
const updateTicketAllocation = async (ticketAllocation) => {
  try {
    console.log(ticketAllocation, 'ticket allocation');
    const realm = await getRealm([TicketModel.AllocationSchema]);
    if (ticketAllocation.haveQueue) {
      console.log('have queue');
    } else {
      //don't have queue
      const queueTicket = await ticketAPI.updateAllocateTicket([
        ticketAllocation.ticket_type_id,
      ]);
      console.log(queueTicket, 'queue ticket');
      // ticketAllocation.queue_start = queueTicket[0].start_number;
      // ticketAllocation.queue_end = queueTicket[0].end_number;
      // ticketAllocation.haveQueue = true;
      // realm.write(() => {
      //   realm.create('Allocation', ticketAllocation, 'modified');
      // });
      // realm.close();
    }
  } catch (error) {
    return Promise.reject(`update ticket allocation failed: ${error}`);
  }
};

//Transaction Schema ====================================================================================================
export const insertTransaction = async (ticketData) => {
  try {
    const realm = await getRealm([TicketModel.TransactionSchema]);
    realm.write(() => {
      let data = {
        timestamp: ticketData.timestamp,
        ticket_type_id: ticketData.ticket_type_id,
        type: ticketData.type,
        number: ticketData.ticket_number,
      };
      realm.create('Transaction', data);
    });
    realm.close();
  } catch (error) {
    return Promise.reject(`insert transaction failed: ${error}`);
  }
};
export const clearAllTransactions = async () => {
  try {
    const realm = await getRealm([TicketModel.TransactionSchema]);
    realm.write(() => {
      let allTransactions = realm.objects('Transaction');
      realm.delete(allTransactions);
    });
    realm.close();
  } catch (error) {
    return Promise.reject(`clear all transaction failed: ${error}`);
  }
};
