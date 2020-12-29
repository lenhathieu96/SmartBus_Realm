import {StyleSheet, Dimensions} from 'react-native';

const {width} = Dimensions.get('window');

const styles = StyleSheet.create({
  mainContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'white',
    padding: 55,
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
  textInput: {
    width: width / 2 - 5,
    height: 50,
    borderColor: 'black',
    borderWidth: 1,
    borderRadius: 4,
    alignSelf: 'center',
    margin: 20,
    fontSize: 20,
  },
  ButtonOk: {
    backgroundColor: 'gray',
    borderRadius: 4,
    height: 50,
    width: width / 2 - 5,
    alignSelf: 'center',
    justifyContent: 'center',
    alignItems: 'center',
    margin: 10,
  },
  viewUser: {
    flexDirection: 'row',
  },
});

export default styles;
