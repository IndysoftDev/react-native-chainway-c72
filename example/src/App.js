import * as React from 'react';

import { StyleSheet, View, Text } from 'react-native';
import ChainwayC72 from 'react-native-chainway-c72';
import KeyEvent from 'react-native-keyevent';
let lastPress = 0;

export default function App() {
  const timerRef = React.useRef(null);
  const [result, setResult] = React.useState();
  const [power, setPower] = React.useState();

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
    ChainwayC72.readPower().then((res) => {
      setPower(res);
    });
  }, []);

  React.useEffect(() => {
    KeyEvent.onKeyDownListener(() => {
      const time = new Date().getTime();
      const delta = time - lastPress;
      const DOUBLE_PRESS_DELAY = 300;
      lastPress = time;
      if (delta < DOUBLE_PRESS_DELAY) {
        if (timerRef.current) {
          clearTimeout(timerRef.current);
          console.log('double');
        }
        return;
      }
      timerRef.current = setTimeout(() => {
        console.log('single');
      }, 300);
      return;
    });

    return () => KeyEvent.removeKeyMultipleListener();
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
