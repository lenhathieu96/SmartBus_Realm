import React from 'react';
import PropTypes from 'prop-types';
import {TouchableOpacity, Text, ActivityIndicator} from 'react-native';

import {bigger} from '../../utils/Fontsize';
import styles from './styles';

TextButton.propTypes = {
  text: PropTypes.string.isRequired,
  onPress: PropTypes.func.isRequired,
  style: PropTypes.oneOfType([PropTypes.object, PropTypes.array]),
  textStyle: PropTypes.object,
  disabled: PropTypes.bool,
  isLoading: PropTypes.bool,
};

function TextButton(props) {
  const {style, textStyle, onPress, text, disabled, isLoading} = props;

  return (
    <TouchableOpacity
      onPress={onPress}
      disabled={disabled}
      style={[styles.TextButton, style]}>
      {isLoading ? (
        <ActivityIndicator color="white" size={bigger} />
      ) : (
        <Text style={[styles.text, textStyle]}>{text}</Text>
      )}
    </TouchableOpacity>
  );
}

export default TextButton;
