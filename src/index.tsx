import { NativeModules } from 'react-native';

type ChainwayC72Type = {
  multiply(a: number, b: number): Promise<number>;
};

const { ChainwayC72 } = NativeModules;

export default ChainwayC72 as ChainwayC72Type;
