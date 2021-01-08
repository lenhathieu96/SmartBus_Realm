import {StyleSheet} from 'react-native';

import * as fontSize from '../../utils/Fontsize';

const styles = StyleSheet.create({
  btnContainer: {
    marginVertical: 5,
    paddingVertical: 5,
    flexDirection: 'row',
    flex: 0.3,
    justifyContent: 'space-between',
  },

  OptionBtn: {
    borderRadius: 10,
    flex: 1,
  },
  txtOptionBtn: {
    textAlign: 'center',
    fontSize: fontSize.bigger,
  },
  profileContainer: {
    flex: 0.2,
  },

  txtContainer: {
    flexDirection: 'row',
    marginVertical: 5,
  },
});

export default styles;
