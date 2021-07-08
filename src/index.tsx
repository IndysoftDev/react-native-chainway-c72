import { NativeModules } from 'react-native';

const { ChainwayC72 } = NativeModules;

type initReader = () => Promise<boolean>;

type deinitReader = () => Promise<boolean>;

type isReaderInit = () => Promise<boolean>;

type readSingleTag = () => Promise<any>;

const initReader: initReader = () => ChainwayC72.initReader();

const deinitReader: deinitReader = () => ChainwayC72.deinitReader();

const isReaderInit: isReaderInit = () => ChainwayC72.isReaderInit();

const readSingleTag: readSingleTag = () => ChainwayC72.readSingleTag();

export default {
  initReader,
  deinitReader,
  isReaderInit,
  readSingleTag,
};
