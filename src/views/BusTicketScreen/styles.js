import {StyleSheet, Dimensions} from 'react-native';
const {height} = Dimensions.get('window');
import * as fontSize from '../../utils/Fontsize';

const styles = StyleSheet.create({
  title: {
    textAlign: 'center',
    fontWeight: 'bold',
    fontSize: fontSize.bigger,
  },
  ticketList: {
    flex: 1,
    marginTop: 5,
  },
  btnTicket: {
    flex: 1,
    height: 0.13 * height,
    borderRadius: 10,
  },
  txtBtnTicket: {
    fontSize: fontSize.huge,
  },
});

export default styles;
