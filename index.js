import React, { Component } from 'react';
import {
    Platform,
    NativeModules,
    CameraRoll
} from 'react-native';

export default saveImage = (url) => {
    if(Platform.OS === 'ios'){
        return CameraRoll.saveToCameraRoll(url);
    } else {
        return NativeModules.ImageUtil.save(url, url.split('/').pop());
    }
}