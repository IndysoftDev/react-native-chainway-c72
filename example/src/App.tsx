import * as React from 'react';

import { StyleSheet, View, Text } from 'react-native';
import ChainwayC72 from 'react-native-chainway-c72';
import KeyEvent from 'react-native-keyevent';

export default function App() {
  const [result, setResult] = React.useState<boolean>();
  const [power, setPower] = React.useState<number>();
  const scanRef = React.useRef<boolean>(false);

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

  // const writeToTag = async () => {
  //   try {
  //     await ChainwayC72.writeToEpc('ee');
  //     return 'success';
  //   } catch {
  //     console.log('missed');
  //     return;
  //   }
  // };

  const startScanning = () => ChainwayC72.findTag('000000000000000000403238');

  const stopScanning = () => ChainwayC72.stopScan();

  const getProximity = (tag: any) => {
    const rssi = parseFloat(tag.rssi);
    if (rssi > -52) {
      return 100;
    } else if (rssi > -54) {
      return 90;
    } else if (rssi > -56) {
      return 80;
    } else if (rssi > -58) {
      return 70;
    } else if (rssi > -60) {
      return 60;
    } else if (rssi > -62) {
      return 50;
    } else if (rssi > -64) {
      return 40;
    } else if (rssi > -66) {
      return 30;
    } else if (rssi > -68) {
      return 20;
    } else if (rssi > -70) {
      return 10;
    } else {
      return 0;
    }
  };

  // if (rssi > -50) {
  //    return 100
  // } else if rssi > -52 {
  //     self.readerDelegate?.proximity(proximityPercent: 90)
  // } else if rssi > -54 {
  //     self.readerDelegate?.proximity(proximityPercent: 80)
  // } else if rssi > -56 {
  //     self.readerDelegate?.proximity(proximityPercent: 70)
  // } else if rssi > -58 {
  //     self.readerDelegate?.proximity(proximityPercent: 60)
  // } else if rssi > -60 {
  //     self.readerDelegate?.proximity(proximityPercent: 50)
  // } else if rssi > -62 {
  //     self.readerDelegate?.proximity(proximityPercent: 40)
  // } else if rssi > -64 {
  //     self.readerDelegate?.proximity(proximityPercent: 30)
  // } else if rssi > -66 {
  //     self.readerDelegate?.proximity(proximityPercent: 20)
  // } else if rssi > -68 {
  //     self.readerDelegate?.proximity(proximityPercent: 10)
  // } else if rssi <= -68 {
  //     self.readerDelegate?.proximity(proximityPercent: 0)
  // }

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
    ChainwayC72.tagListener((tag) => console.log(getProximity(tag)));
  }, []);

  React.useEffect(() => {
    KeyEvent.onKeyDownListener(async () => {
      if (!scanRef.current) {
        scanRef.current = true;
        await startScanning();
      }
    });

    return () => KeyEvent.removeKeyDownListener();
  }, []);

  React.useEffect(() => {
    KeyEvent.onKeyUpListener(async () => {
      if (scanRef.current) {
        scanRef.current = false;
        await stopScanning();
      }
    });

    return () => KeyEvent.removeKeyUpListener();
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
