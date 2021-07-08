import * as React from 'react';

import { StyleSheet, View, Text } from 'react-native';
import ChainwayC72 from 'react-native-chainway-c72';
import KeyEvent from 'react-native-keyevent';

export default function App() {
  const [result, setResult] = React.useState<boolean>();

  React.useEffect(() => {
    ChainwayC72.initReader().then((res) => {
      console.log(res);
      setResult(res);
    });
  }, []);

  React.useEffect(() => {
    KeyEvent.onKeyDownListener((keyEvent: any) => {
      console.log(keyEvent);
    });
    // if you want to react to keyUp
    KeyEvent.onKeyUpListener(async () => {
      const tag = await ChainwayC72.readSingleTag();
      console.log(tag.rssi);
    });
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
