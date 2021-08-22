import * as React from 'react';

import { StyleSheet, View, Text } from 'react-native';
import ChainwayC72 from 'react-native-chainway-c72';
import KeyEvent from 'react-native-keyevent';
let lastPress = 0;

export default function App() {
  const [result, setResult] = React.useState();

  // const readTag = async () => {
  //   try {
  //     const tag = await ChainwayC72.readSingleTag();
  //     console.log(tag);
  //     return tag;
  //   } catch {
  //     console.log('missed');
  //     return;
  //   }
  // };

  React.useEffect(() => {
    ChainwayC72.initReader().then((res) => {
      console.log(res);
      setResult(res);
    });
  }, []);

  React.useEffect(() => {
    ChainwayC72.addTagListener((bc) => console.log(bc));

    return () => ChainwayC72.removeTagListener();
  }, []);

  React.useEffect(() => {
    KeyEvent.onKeyUpListener(async () => {
      await ChainwayC72.stopScan();
      return;
    });

    return () => KeyEvent.removeKeyUpListener();
  }, []);

  React.useEffect(() => {
    KeyEvent.onKeyDownListener(async () => {
      await ChainwayC72.startScan();
      return;
    });

    return () => KeyEvent.removeKeyDownListener();
  }, []);

  return (
    <View style={styles.container}>
      <Text>Result: {result}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
