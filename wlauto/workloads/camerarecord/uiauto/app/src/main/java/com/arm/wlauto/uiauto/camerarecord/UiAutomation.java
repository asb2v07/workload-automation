/*    Copyright 2013-2015 ARM Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/


package com.arm.wlauto.uiauto.camerarecord;

import android.os.Bundle;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiSelector;

import com.arm.wlauto.uiauto.BaseUiAutomation;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class UiAutomation extends BaseUiAutomation {

    public static String TAG = "camerarecord";
    int timeToRecord = 0;
    int timeout = 4;
    int sleepTime = 2;
    int recordingTime = 0;
    String recordingMode = "normal";
    int api = 0;
    Integer[] version = {0,0,0};

@Test
public void runUiAutomation() throws Exception {
        initialize_instrumentation();
        Bundle parameters = getParams();
        if (parameters.size() > 0) {
           recordingTime = parameters.getInt("recording_time");
           recordingMode = parameters.getString("recording_mode");
           api = parameters.getInt("api_level");
           String versionString = parameters.getString("version");
           version = splitVersion(versionString);
        }

        //Pre Android M UI
        if (api < 23)
            recordVideoAosp();
        else
        {
            if(compareVersions(version, new Integer[]{3,2,0}) >= 0)
                recordVideoGoogleV3_2();
            else
               recordVideoGoogle();
        }
    }

    void recordVideoAosp() throws Exception {
        // switch to camera capture mode
        UiObject clickModes = mDevice.findObject(new UiSelector().descriptionMatches("Camera, video or panorama selector"));
        clickModes.click();
        sleep(sleepTime);

        UiObject changeModeToCapture = mDevice.findObject(new UiSelector().descriptionMatches("Switch to video"));
        changeModeToCapture.click();
        sleep(sleepTime);

        UiObject clickRecordingButton = mDevice.findObject(new UiSelector().descriptionMatches("Shutter button"));
        clickRecordingButton.longClick();
        sleep(recordingTime);

        // Stop video recording
        clickRecordingButton.longClick();
        mDevice.pressBack();
    }

    void recordVideoGoogleV3_2() throws Exception {
        // clear tutorial if needed
        UiObject tutorialText = mDevice.findObject(new UiSelector().resourceId("com.android.camera2:id/photoVideoSwipeTutorialText"));
        if (tutorialText.waitForExists(TimeUnit.SECONDS.toMillis(5))) {
            tutorialText.swipeLeft(5);
            sleep(sleepTime);
            tutorialText.swipeRight(5);
        }

        // ensure we are in video mode
        UiObject viewFinder = mDevice.findObject(new UiSelector().resourceId("com.android.camera2:id/viewfinder_frame"));
        viewFinder.swipeLeft(5);

        String captureButtonId = "com.android.camera2:id/photo_video_button";
        // Enable slow motion if requested
        if (recordingMode.equals("slow_motion")) {
            mDevice.pressMenu();
            UiObject slowMoButton = mDevice.findObject(new UiSelector().text("Slow Motion").className("android.widget.TextView"));
            slowMoButton.waitForExists(TimeUnit.SECONDS.toMillis(5));
            slowMoButton.click();
            captureButtonId = "com.android.camera2:id/video_hfr_shutter_button";
        }

        // click to capture photos
        UiObject clickCaptureButton = mDevice.findObject(new UiSelector().resourceId(captureButtonId));
        clickCaptureButton.longClick();
        sleep(recordingTime);

        // stop video recording
        clickCaptureButton.longClick();
    }

    void recordVideoGoogle() throws Exception {
        // Open mode select menu
        UiObject swipeScreen = mDevice.findObject(new UiSelector().resourceId("com.android.camera2:id/mode_options_overlay"));
        swipeScreen.swipeRight(5);

        // Switch to video mode
        UiObject changeModeToCapture = mDevice.findObject(new UiSelector().descriptionMatches("Switch to Video Camera"));
        changeModeToCapture.click();
        sleep(sleepTime);

        UiObject clickRecordingButton = mDevice.findObject(new UiSelector().descriptionMatches("Shutter"));
        clickRecordingButton.longClick();
        sleep(recordingTime);

        // Stop video recording
        clickRecordingButton.longClick();
    }
}

