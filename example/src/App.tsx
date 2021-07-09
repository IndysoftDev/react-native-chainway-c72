import * as React from 'react';

import { StyleSheet, View, Text } from 'react-native';
import ChainwayC72 from 'react-native-chainway-c72';
import KeyEvent from 'react-native-keyevent';

export default function App() {
  const [result, setResult] = React.useState<boolean>();
  const [power, setPower] = React.useState<number>();

  const readTag = async () => {
    try {
      const tag = await ChainwayC72.readSingleTag();
      console.log(tag);
      return tag;
    } catch {
      console.log('missed');
      return;
    }
  };

  React.useEffect(() => {
    ChainwayC72.initReader().then((res) => {
      console.log(res);
      setResult(res);
    });
  }, []);

  React.useEffect(() => {
    ChainwayC72.readPower().then((res) => {
      setPower(res);
    });
  }, []);

  React.useEffect(() => {
    KeyEvent.onKeyUpListener(async () => {
      await readTag();
    });
  }, []);

  return (
    <View style={styles.container}>
      <Text>Result: {result}</Text>
      <Text>Power: {power}</Text>
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
