import {NativeModules} from 'react-native';
const {PrintModule} = NativeModules;

export const PrintBusTicket = async (company, vehicle, ticket) => {
  try {
    switch (company.id) {
      //QT-NT
      case 19:
        await PrintModule.printDeductionTicketALL(
          JSON.stringify(company),
          JSON.stringify(vehicle),
          JSON.stringify(ticket),
        );
        break;
      default:
        break;
    }
  } catch (error) {
    return Promise.reject(error);
  }
};
