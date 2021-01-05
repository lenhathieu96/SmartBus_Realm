import {StyleSheet} from 'react-native';
const styles = StyleSheet.create({
  buttonContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-evenly',
  },
  btn: {
    flex: 1,
  },
  loadingContainer: {
    flex: 1,
    padding: 20,
    justifyContent: 'center',
    alignItems: 'center',
  },
  txtLoading: {
    marginVertical: 10,
    textAlign: 'center',
  },
  textTitle: {
    color: 'black',
    fontSize: 15,
    margin: 10,
  },
});

export default styles;
