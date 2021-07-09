import { NativeModules } from 'react-native';

const { ChainwayC72 } = NativeModules;

type initReader = () => Promise<boolean>;

type deinitReader = () => Promise<boolean>;

type isReaderInit = () => Promise<boolean>;

type readSingleTag = () => Promise<any>;

type readPower = () => Promise<any>;

type setPower = (powerValue: number) => Promise<any>;

const initReader: initReader = () => ChainwayC72.initReader();

const deinitReader: deinitReader = () => ChainwayC72.deinitReader();

const isReaderInit: isReaderInit = () => ChainwayC72.isReaderInit();

const readSingleTag: readSingleTag = () => ChainwayC72.readSingleTag();

const readPower: readPower = () => ChainwayC72.readPower();

const setPower: setPower = () => ChainwayC72.setPower();

export default {
  initReader,
  deinitReader,
  isReaderInit,
  readSingleTag,
};
