import * as React from 'react';

import { StyleSheet, View, Text } from 'react-native';
import ChainwayC72 from 'react-native-chainway-c72';

export default function App() {
  const [result, setResult] = React.useState<boolean>();

  React.useEffect(() => {
    ChainwayC72.isReaderInit().then((res) => {
      console.log(res);
      setResult(res);
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
