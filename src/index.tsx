import { NativeModules } from 'react-native';

const { ChainwayC72 } = NativeModules;

type initReader = () => Promise<boolean>;

type deinitReader = () => Promise<boolean>;

type isReaderInit = () => Promise<boolean>;

type readSingleTag = () => Promise<any>;

type readPower = () => Promise<any>;

type setPower = (powerVal: number) => Promise<any>;

type writeToEpc = (newStr: string) => Promise<any>;

const initReader: initReader = () => ChainwayC72.initReader();

const deinitReader: deinitReader = () => ChainwayC72.deinitReader();

const isReaderInit: isReaderInit = () => ChainwayC72.isReaderInit();

const readSingleTag: readSingleTag = () => ChainwayC72.readSingleTag();

const readPower: readPower = () => ChainwayC72.readPower();

const setPower: setPower = (powerVal: number) => ChainwayC72.setPower(powerVal);

// eslint-disable-next-line prettier/prettier
const writeToEpc: writeToEpc = (newStr: string) => ChainwayC72.writeToEpc(newStr);

export default {
  initReader,
  deinitReader,
  isReaderInit,
  readSingleTag,
  readPower,
  setPower,
  writeToEpc,
};
