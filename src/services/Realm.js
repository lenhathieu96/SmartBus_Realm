import Realm from 'realm';

export default function getRealm(schema) {
  return Realm.open({
    schema: [schema],
  });
}
