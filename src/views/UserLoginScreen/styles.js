import {StyleSheet} from 'react-native';
import * as fontSize from '../../utils/Fontsize';

const styles = StyleSheet.create({
  title: {
    fontSize: fontSize.huge,
    fontWeight: 'bold',
    textAlign: 'center',
  },
  input: {
    fontSize: fontSize.large,
    padding: 10,
    justifyContent: 'center',
    textAlign: 'center',
    letterSpacing: 2,
    borderRadius: 10,
    marginVertical: 10,
    backgroundColor: 'white',
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 3,
    },
    shadowOpacity: 0.27,
    shadowRadius: 4.65,
    elevation: 6,
    fontWeight: 'bold',
  },
});

export default styles;
