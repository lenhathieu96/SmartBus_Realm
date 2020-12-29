// import Intl from 'intl';
// import 'intl/locale-data/jsonp/en';
import Moment from 'moment';
// import NfcManager, {Ndef} from 'react-native-nfc-manager';
import AsyncStorage from '@react-native-async-storage/async-storage';
// import CryptoJS from 'crypto-js';
// import RNFS from 'react-native-fs';
import global from './Global';
// import Sound from 'react-native-sound';
import IMEI from 'react-native-imei';
// export function downloadSound({url_sound, callback = () => {}}) {
//   try {
//     var filePath = RNFS.DocumentDirectoryPath + '/' + url_sound;
//     var download = RNFS.downloadFile({
//       fromUrl: `${global.host}${'/audio/bus-stations/'}${url_sound}`,
//       toFile: filePath,
//       // eslint-disable-next-line no-unused-vars
//       progress: (res) => {
//         // ToastAndroid.show(
//         //   'Loading: ' +
//         //     url_sound +
//         //     ' ' +
//         //     ((res.bytesWritten / res.contentLength) * 100)
//         //       .toFixed(0)
//         //       .toString() +
//         //     '%',
//         //   ToastAndroid.SHORT
//         // );
//       },
//       progressDivider: 1,
//     });
//     download.promise.then((result) => {
//       if (result.statusCode == 200) {
//         callback({status: true});
//       } else {
//         callback({status: false});
//       }
//     });
//   } catch (error) {
//     callback({status: false});
//   }
// }

export function str_slug(str) {
  str = str.replace(/à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ/g, 'a');
  str = str.replace(/è|é|ẹ|ẻ|ẽ|ê|ề|ế|ệ|ể|ễ/g, 'e');
  str = str.replace(/ì|í|ị|ỉ|ĩ/g, 'i');
  str = str.replace(/ò|ó|ọ|ỏ|õ|ô|ồ|ố|ộ|ổ|ỗ|ơ|ờ|ớ|ợ|ở|ỡ/g, 'o');
  str = str.replace(/ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ/g, 'u');
  str = str.replace(/ỳ|ý|ỵ|ỷ|ỹ/g, 'y');
  str = str.replace(/đ/g, 'd');
  str = str.replace(/À|Á|Ạ|Ả|Ã|Â|Ầ|Ấ|Ậ|Ẩ|Ẫ|Ă|Ằ|Ắ|Ặ|Ẳ|Ẵ/g, 'A');
  str = str.replace(/È|É|Ẹ|Ẻ|Ẽ|Ê|Ề|Ế|Ệ|Ể|Ễ/g, 'E');
  str = str.replace(/Ì|Í|Ị|Ỉ|Ĩ/g, 'I');
  str = str.replace(/Ò|Ó|Ọ|Ỏ|Õ|Ô|Ồ|Ố|Ộ|Ổ|Ỗ|Ơ|Ờ|Ớ|Ợ|Ở|Ỡ/g, 'O');
  str = str.replace(/Ù|Ú|Ụ|Ủ|Ũ|Ư|Ừ|Ứ|Ự|Ử|Ữ/g, 'U');
  str = str.replace(/Ỳ|Ý|Ỵ|Ỷ|Ỹ/g, 'Y');
  str = str.replace(/Đ/g, 'D');
  str = str.replace(/\u0300|\u0301|\u0303|\u0309|\u0323/g, '');
  str = str.replace(/\u02C6|\u0306|\u031B/g, '');
  return str;
}

// export function deleteSound(url_sound) {
//   try {
//     var path = RNFS.DocumentDirectoryPath + '/' + url_sound;
//     return (
//       RNFS.unlink(path)
//         .then(() => {
//           // ToastAndroid.show(`${url_sound} DELETED`, ToastAndroid.TOP);
//         })
//         // eslint-disable-next-line no-unused-vars
//         .catch((err) => {
//           // alert(err.message);
//         })
//     );
//   } catch (e) {
//     //
//   }
// }

// export function playSoundLocal({url_sound}) {
//   try {
//     const sound = new Sound(url_sound, null, (error) => {
//       if (error !== null) {
//         sound.release();
//       } else {
//         sound.setVolume(1);
//         sound.play((success) => {
//           if (success) {
//             sound.release();
//           }
//         });
//       }
//     });
//   } catch (e) {
//     //
//   }
// }

// export function playSound({url_sound, callback = () => {}}) {
//   try {
//     var filePath = RNFS.DocumentDirectoryPath + '/' + url_sound;
//     const sound = new Sound(filePath, null, (error) => {
//       if (error !== null) {
//         sound.release();
//         callback({status: false});
//       } else {
//         sound.setVolume(1);
//         sound.play((success) => {
//           if (success) {
//             sound.release();
//           }
//         });
//         callback({status: true});
//       }
//     });
//   } catch (e) {
//     //
//   }
// }

// export function cleanUp(a = null) {
//   // NfcManager.closeTechnology();
//   NfcManager.unregisterTagEvent();
// }

export function calDedution(price, deduction) {
  return price - (price * deduction) / 100;
}

export function convertStringToNum(str) {
  let tmp = str.split(',');
  return parseInt(tmp.join(''));
}

export async function checkModuleApp(key) {
  const s = await AsyncStorage.getItem('@shift');
  const shift = JSON.parse(s);
  const index = shift.findIndex((el) => el.active == 1);
  return shift[index].module_company.includes(key);
}

export function format_ticket(num) {
  num = num.toString();
  switch (num.length) {
    case 6:
      return `0${num}`;

    case 5:
      return `00${num}`;

    case 4:
      return `000${num}`;

    case 3:
      return `0000${num}`;

    case 2:
      return `00000${num}`;

    case 1:
      return `000000${num}`;

    default:
      return `${num}`;
  }
}

// export async function hadleStationCurrent(bus_station_data) {
//   try {
//     var e = await AsyncStorage.getItem('@COORDINATES');
//     if (e !== null) {
//       let {lat, lng} = JSON.parse(e);
//       station = await getStation(bus_station_data, {lat, lng});
//     }
//     return station;
//   } catch (e) {
//     return station;
//   }
// }

export async function getStation(data, cur = null, s_station = null) {
  try {
    if (cur === null) {
      const p = await AsyncStorage.getItem('@position');
      cur = JSON.parse(p);
    }
    let arrayM = [];
    for (let i = 0; i < data.length; i++) {
      // if(s_station!==null && data[i].id === s_station.id) continue;
      arrayM.push(distance(cur.lat, cur.lng, data[i].lat, data[i].lng, 1000));
    }
    const min = Math.min(...arrayM);
    const index_arr =
      s_station !== null
        ? s_station === 1
          ? arrayM.indexOf(min) - 1
          : arrayM.indexOf(min) + 1
        : arrayM.indexOf(min);
    return data[index_arr];
  } catch (e) {
    //
  }
}

// export async function getCurrentPosition() {
//   try {
//     const full_path = RNFS.DocumentDirectoryPath + '/Location/gps';
//     const p = await RNFS.readFile(full_path, 'utf8');
//     return JSON.parse(p);
//   } catch (e) {
//     return null;
//   }
// }

export function findUri(txt) {
  return txt.indexOf('/') === -1;
}

// export function buildUrlPayload(valueToWrite) {
//   return Ndef.encodeMessage([Ndef.uriRecord(valueToWrite)]);
// }

// export function buildTextPayload(valueToWrite) {
//   return Ndef.encodeMessage([Ndef.textRecord(valueToWrite)]);
// }

// export function parseUriNFC(tag) {
//   try {
//     if (Ndef.isType(tag.ndefMessage[0], Ndef.TNF_WELL_KNOWN, Ndef.RTD_URI)) {
//       return Ndef.uri.decodePayload(tag.ndefMessage[0].payload);
//     }
//   } catch (e) {
//     //
//   }
//   return null;
// }

// export function parseTextNFC(tag) {
//   try {
//     if (Ndef.isType(tag.ndefMessage[0], Ndef.TNF_WELL_KNOWN, Ndef.RTD_TEXT)) {
//       return Ndef.text.decodePayload(tag.ndefMessage[0].payload);
//     }
//   } catch (e) {
//     //
//   }
//   return null;
// }

export function distance(lat1, lng1, lat2, lng2, value = null) {
  // console.log('distance', lat1, lng1, lat2, lng2);
  var unit = 'K';
  if (
    (lat1 === lat2 && lng1 === lng2) ||
    (lat1 || lng1 || lat2 || lng2) === undefined
  ) {
    return 0;
  }
  var radlat1 = (Math.PI * lat1) / 180;
  var radlat2 = (Math.PI * lat2) / 180;
  var theta = lng1 - lng2;
  var radtheta = (Math.PI * theta) / 180;
  var dist =
    Math.sin(radlat1) * Math.sin(radlat2) +
    Math.cos(radlat1) * Math.cos(radlat2) * Math.cos(radtheta);
  if (dist > 1) {
    dist = 1;
  }
  dist = Math.acos(dist);
  dist = (dist * 180) / Math.PI;
  dist = dist * 60 * 1.1515;
  if (unit == 'K') {
    dist = dist * 1.609344;
  }
  if (unit == 'N') {
    dist = dist * 0.8684;
  }
  if (value === null) {
    return dist;
  } else {
    return dist * value;
  }
}

export function checkCountMonth(str) {
  try {
    str = str.toString();
    if (str.length === 1) {
      str = `0${str}`;
    }
    const regex = /[0-9]{2}/;
    var rs = str.match(regex);
    const num = parseInt(rs[0]);
    if (num > 12) {
      return 12;
    } else {
      return num;
    }
  } catch (e) {
    return null;
  }
}

export async function genNumberGoods() {
  try {
    var IM = IMEI.getImei();
    var num = await AsyncStorage.getItem('@qtyNumGoods');
    if (num === '-1') {
      return num;
    } else {
      var stt = '01';
      var time = Moment().format('DDMMYY');
      if (num !== null) {
        num = JSON.parse(num);
        if (Moment().format('YYYY-MM-DD') === num.day) {
          if (num.stt < 9) {
            stt = `0${num.stt + 1}`;
          } else {
            stt = `${num.stt + 1}`;
          }
        } else if (Moment().format('YYYY-MM-DD') < num.day) {
          time = Moment(num.day).format('DDMMYY');
          if (num.stt < 9) {
            stt = `0${num.stt + 1}`;
          } else {
            stt = `${num.stt + 1}`;
          }
        }
      }
      return `${IM.substring(IM.length - 6)}${time}-${stt}`;
    }
  } catch (e) {
    //
  }
}
// export function encrypt(data, key) {
//   return CryptoJS.AES.encrypt(JSON.stringify(data), key).toString();
// }

// export function decrypt(data, key) {
//   return JSON.parse(
//     CryptoJS.AES.decrypt(data, key).toString(CryptoJS.enc.Utf8),
//   );
// }

export function getTimeLogin(timestamp) {
  return Moment.unix(timestamp).format('HH:mm:ss DD-MM-YYYY');
}

export function getNowDB() {
  return Moment().format('YYYY-MM-DD HH:mm:ss');
}

export function getNow(str = null) {
  let time = str;
  if (str === null) {
    time = Moment().format('YYYY-MM-DD');
  }
  return time.split(' ')[0].split('-').map(Number);
}
// export function getNumDayToTimeTamp(str) {
//   return str
//     .split(' ')[0]
//     .split('-')
//     .map(Number);
// }
// export function getNumYearMonthNow() {
//   return Moment()
//     .format('YYYY-MM-DD')
//     .split('-')
//     .map(Number);
// }

export function getHours(timestamp) {
  return Moment.unix(timestamp).format('HH:mm:ss');
}

export function get_month_year(str = null) {
  str = strTimeDB(str);
  if (str === null) {
    return Moment().format('MM-YYYY');
  }
  return Moment(str).format('MM-YYYY');
}
export function get_day_month_year(str) {
  let ex = str.split(' ')[0];

  return `${ex.split('-')[2]}-${ex.split('-')[1]}-${ex.split('-')[0]}`;
}

export function strTimeDB(str) {
  if (str == null) {
    return null;
  }
  let tmp = str.split('-');
  let month = parseInt(tmp[0]);
  let year = parseInt(tmp[1]);

  if (month > year) {
    if (year < 10) {
      year = `0${year}`;
    }
    str = `${month}-${year}`;
  } else {
    if (month < 10) {
      month = `0${month}`;
    }
    str = `${year}-${month}`;
  }
  return str;
}

export function checkExpWithNow(str) {
  try {
    str = strTimeDB(str);
    if (str == null) {
      return false;
    }

    const now = Moment().format('YYYY-MM');
    const exp = Moment(str).format('YYYY-MM');
    if (exp < now) {
      return true;
    } else {
      return false;
    }
  } catch (e) {
    return false;
  }
}
export function addOneMonth(str, count_month = 1) {
  try {
    str = strTimeDB(str);
    if (str == null) {
      return Moment().format('MM-YYYY');
    }
    return Moment(str).add(count_month, 'months').format('MM-YYYY');
  } catch (e) {
    //
  }
}

export function convertStartMonth(str, count_month = null) {
  try {
    str = strTimeDB(str);
    if (str == null) {
      return false;
    }

    if (count_month === null) {
      return Moment(str).format('YYYY-MM-DD 00:00:00');
    } else {
      return Moment(str)
        .add(count_month, 'months')
        .format('YYYY-MM-DD 00:00:00');
    }
  } catch (e) {
    //
  }
}

export function convertEndMonth(str, count_month = null) {
  try {
    str = strTimeDB(str);
    if (str == null) {
      return false;
    }

    if (count_month === null) {
      return Moment(str).format('YYYY-MM');
    } else {
      return Moment(str).add(count_month, 'months').format('YYYY-MM');
    }
  } catch (e) {
    //
  }
}

export function format_month_exp(str) {
  str = strTimeDB(str);
  if (str === null) {
    return Moment().format('MM-YYYY');
  }

  const now = Moment().format('YYYY-MM');
  const exp = Moment(str).format('YYYY-MM');
  if (exp < now) {
    return Moment().format('MM-YYYY');
  } else {
    return Moment(str).add(1, 'months').format('MM-YYYY');
  }
}

export function get_year_month() {
  return Moment().format('YYYY-MM');
}

// export function get_next_year_month(str) {
//   time = str.split('-').map(Number);
//   if (time[1] === 12) {
//     return `${time[0] + 1}-1`;
//   } else {
//     return `${time[0]}-${time[1] + 1}`;
//   }
// }
// export function get_next_month_year(str) {
//   time = str.split('-').map(Number);
//   if (time[1] === 12) {
//     return `1-${time[0] + 1}`;
//   } else {
//     return `${time[1] + 1}-${time[0]}`;
//   }
// }

export function getDay(timestamp) {
  return Moment.unix(timestamp).format('DD-MM-YYYY');
}

export function getDateTime(timestamp = null) {
  if (timestamp === null) {
    return '';
  }
  return Moment.unix(timestamp).format('DD-MM-YYYY HH:mm:ss');
}

export function getIndexShiftById(arr, shift_id) {
  return arr.findIndex((el) => el.shift_id == shift_id);
}

export function getIndexShiftByActive(arr) {
  return arr.findIndex((el) => el.active == 1);
}

export function getTimestamp() {
  let timestamp = new Date().getTime();
  return Math.floor(timestamp / 1000);
}

// export function format_number_milion(number) {
//   if (number !== null) {
//     let Nber = number.toLocaleString().split(',').join('');
//     const fm = new Intl.NumberFormat();
//     result = fm.format(parseInt(Nber));
//     return result;
//   }
// }

export function format_number(value, dv = null) {
  return Number(value).toLocaleString().split(',').join('.');
}
