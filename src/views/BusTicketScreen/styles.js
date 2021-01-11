import {StyleSheet, Dimensions} from 'react-native';
const {height} = Dimensions.get('window');
import * as fontSize from '../../utils/Fontsize';
import Color from '../../utils/Color';
const styles = StyleSheet.create({
  title: {
    textAlign: 'center',
    fontWeight: 'bold',
    fontSize: fontSize.bigger,
  },
  txtInfo: {
    textAlign: 'center',
    fontSize: fontSize.huge,
    color: Color.red,
  },
  ticketList: {
    flex: 1,
    marginTop: 10,
  },
  btnTicket: {
    flex: 1,
    height: 0.13 * height,
    borderRadius: 10,
    borderWidth: 1,
    backgroundColor: 'white',
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.25,
    shadowRadius: 3.84,

    elevation: 5,
  },
  txtBtnTicket: {
    color: Color.blue,
    fontSize: fontSize.huge,
  },
});

export default styles;
