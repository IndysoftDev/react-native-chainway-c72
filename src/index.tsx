import { NativeModules, NativeEventEmitter } from 'react-native';

const { ChainwayC72 } = NativeModules;

const evtEmitter = new NativeEventEmitter(ChainwayC72);

type initReader = () => Promise<boolean>;

type deinitReader = () => Promise<boolean>;

type isReaderInit = () => Promise<boolean>;

type readSingleTag = () => Promise<any>;

type readPower = () => Promise<any>;

type setPower = (powerVal: number) => Promise<any>;

type writeToEpc = (newStr: string) => Promise<any>;

type addListener = (cb: (args: any[]) => void) => void;

type removeListener = (cb: (args: any[]) => void) => void;

type startScan = () => Promise<any>;

type stopScan = () => Promise<any>;

type findTag = (tag: string) => Promise<any>;

const initReader: initReader = () => ChainwayC72.initReader();

const deinitReader: deinitReader = () => ChainwayC72.deinitReader();

const isReaderInit: isReaderInit = () => ChainwayC72.isReaderInit();

const readSingleTag: readSingleTag = () => ChainwayC72.readSingleTag();

const readPower: readPower = () => ChainwayC72.readPower();

const setPower: setPower = (powerVal: number) => ChainwayC72.setPower(powerVal);

const writeToEpc: writeToEpc = (newStr: string) =>
  ChainwayC72.writeToEpc(newStr);

const startScan: startScan = () => ChainwayC72.startScan();

const stopScan: stopScan = () => ChainwayC72.stopScan();

const findTag: findTag = (tag: string) => ChainwayC72.findTag(tag);

const addTagListener: addListener = (listener) =>
  evtEmitter.addListener('UHF_TAG', listener);

const removeTagListener: removeListener = (listener) =>
  evtEmitter.removeListener('UHF_TAG', listener);

export default {
  initReader,
  deinitReader,
  isReaderInit,
  readSingleTag,
  readPower,
  setPower,
  writeToEpc,
  startScan,
  stopScan,
  findTag,
  addTagListener,
  removeTagListener,
};
