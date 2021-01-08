import {StyleSheet} from 'react-native';
import * as fontSize from '../../utils/Fontsize';
import Color from '../../utils/Color';
const styles = StyleSheet.create({
  TextButton: {
    borderRadius: 50,
    padding: 10,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: Color.primary,
    marginHorizontal: 5,
    marginBottom: 10,
  },
  DisableButton: {
    borderRadius: 50,
    padding: 10,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: Color.unactive,
    marginHorizontal: 5,
    marginBottom: 10,
  },
  text: {
    fontWeight: 'bold',
    fontSize: fontSize.larger,
    color: 'white',
  },
});
export default styles;
