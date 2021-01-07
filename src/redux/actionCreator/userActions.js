export const setUserData = (user) => {
  return {
    type: 'SET USER DATA',
    payload: user,
  };
};

export const updateSettingGlobal = (settingArr) => {
  return {
    type: 'UPDATE SETTING GLOBAL',
    payload: settingArr,
  };
};
