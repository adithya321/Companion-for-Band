/*
 * Companion for Band
 * Copyright (C) 2016  Adithya J
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.pimp.companionforband.utils.band;

import com.microsoft.band.InvalidBandVersionException;
import com.microsoft.band.sensors.BandAccelerometerEvent;
import com.microsoft.band.sensors.BandAltimeterEvent;
import com.microsoft.band.sensors.BandAmbientLightEvent;
import com.microsoft.band.sensors.BandBarometerEvent;
import com.microsoft.band.sensors.BandCaloriesEvent;
import com.microsoft.band.sensors.BandContactEvent;
import com.microsoft.band.sensors.BandContactState;
import com.microsoft.band.sensors.BandDistanceEvent;
import com.microsoft.band.sensors.BandGsrEvent;
import com.microsoft.band.sensors.BandGyroscopeEvent;
import com.microsoft.band.sensors.BandHeartRateEvent;
import com.microsoft.band.sensors.BandPedometerEvent;
import com.microsoft.band.sensors.BandRRIntervalEvent;
import com.microsoft.band.sensors.BandSkinTemperatureEvent;
import com.microsoft.band.sensors.BandUVEvent;
import com.microsoft.band.sensors.HeartRateQuality;
import com.microsoft.band.sensors.MotionType;
import com.microsoft.band.sensors.UVIndexLevel;

public class BandSensorData {
    private BandAccelerometerEvent accelerometerData;
    private BandAltimeterEvent altimeterData;
    private BandAmbientLightEvent ambientLightData;
    private BandBarometerEvent barometerData;
    private BandCaloriesEvent calorieData;
    private BandContactEvent contactData;
    private BandDistanceEvent distanceData;
    private BandGsrEvent gsrData;
    private BandGyroscopeEvent gyroscopeData;
    private BandHeartRateEvent heartRateData;
    private BandPedometerEvent pedometerData;
    private BandRRIntervalEvent rrIntervalData;
    private BandSkinTemperatureEvent skinTemperatureData;
    private BandUVEvent uvIndexData;

    public BandSensorData() {
        accelerometerData = new BandAccelerometerEvent() {
            @Override
            public float getAccelerationX() {
                return 0;
            }

            @Override
            public float getAccelerationY() {
                return 0;
            }

            @Override
            public float getAccelerationZ() {
                return 0;
            }

            @Override
            public long getTimestamp() {
                return 0;
            }
        };
        altimeterData = new BandAltimeterEvent() {
            @Override
            public long getTotalGain() {
                return 0;
            }

            @Override
            public long getTotalGainToday() throws InvalidBandVersionException {
                return 0;
            }

            @Override
            public long getTotalLoss() {
                return 0;
            }

            @Override
            public long getSteppingGain() {
                return 0;
            }

            @Override
            public long getSteppingLoss() {
                return 0;
            }

            @Override
            public long getStepsAscended() {
                return 0;
            }

            @Override
            public long getStepsDescended() {
                return 0;
            }

            @Override
            public float getRate() {
                return 0;
            }

            @Override
            public long getFlightsAscended() {
                return 0;
            }

            @Override
            public long getFlightsDescended() {
                return 0;
            }

            @Override
            public long getFlightsAscendedToday() throws InvalidBandVersionException {
                return 0;
            }

            @Override
            public long getTimestamp() {
                return 0;
            }
        };
        ambientLightData = new BandAmbientLightEvent() {
            @Override
            public int getBrightness() {
                return 0;
            }

            @Override
            public long getTimestamp() {
                return 0;
            }
        };
        barometerData = new BandBarometerEvent() {
            @Override
            public double getAirPressure() {
                return 0;
            }

            @Override
            public double getTemperature() {
                return 0;
            }

            @Override
            public long getTimestamp() {
                return 0;
            }
        };
        calorieData = new BandCaloriesEvent() {
            @Override
            public long getCalories() {
                return 0;
            }

            @Override
            public long getCaloriesToday() throws InvalidBandVersionException {
                return 0;
            }

            @Override
            public long getTimestamp() {
                return 0;
            }
        };
        contactData = new BandContactEvent() {
            @Override
            public BandContactState getContactState() {
                return null;
            }

            @Override
            public long getTimestamp() {
                return 0;
            }
        };
        distanceData = new BandDistanceEvent() {
            @Override
            public long getTotalDistance() {
                return 0;
            }

            @Override
            public long getDistanceToday() throws InvalidBandVersionException {
                return 0;
            }

            @Override
            public float getSpeed() {
                return 0;
            }

            @Override
            public float getPace() {
                return 0;
            }

            @Override
            public MotionType getMotionType() {
                return null;
            }

            @Override
            public long getTimestamp() {
                return 0;
            }
        };
        gsrData = new BandGsrEvent() {
            @Override
            public int getResistance() {
                return 0;
            }

            @Override
            public long getTimestamp() {
                return 0;
            }
        };
        gyroscopeData = new BandGyroscopeEvent() {
            @Override
            public float getAccelerationX() {
                return 0;
            }

            @Override
            public float getAccelerationY() {
                return 0;
            }

            @Override
            public float getAccelerationZ() {
                return 0;
            }

            @Override
            public float getAngularVelocityX() {
                return 0;
            }

            @Override
            public float getAngularVelocityY() {
                return 0;
            }

            @Override
            public float getAngularVelocityZ() {
                return 0;
            }

            @Override
            public long getTimestamp() {
                return 0;
            }
        };
        heartRateData = new BandHeartRateEvent() {
            @Override
            public int getHeartRate() {
                return 0;
            }

            @Override
            public HeartRateQuality getQuality() {
                return null;
            }

            @Override
            public long getTimestamp() {
                return 0;
            }
        };
        pedometerData = new BandPedometerEvent() {
            @Override
            public long getTotalSteps() {
                return 0;
            }

            @Override
            public long getStepsToday() throws InvalidBandVersionException {
                return 0;
            }

            @Override
            public long getTimestamp() {
                return 0;
            }
        };
        rrIntervalData = new BandRRIntervalEvent() {
            @Override
            public double getInterval() {
                return 0;
            }

            @Override
            public long getTimestamp() {
                return 0;
            }
        };
        skinTemperatureData = new BandSkinTemperatureEvent() {
            @Override
            public float getTemperature() {
                return 0;
            }

            @Override
            public long getTimestamp() {
                return 0;
            }
        };
        uvIndexData = new BandUVEvent() {
            @Override
            public UVIndexLevel getUVIndexLevel() {
                return null;
            }

            @Override
            public long getUVExposureToday() throws InvalidBandVersionException {
                return 0;
            }

            @Override
            public long getTimestamp() {
                return 0;
            }
        };
    }

    public BandAccelerometerEvent getAccelerometerData() {
        return accelerometerData;
    }

    public void setAccelerometerData(BandAccelerometerEvent accelerometerData) {
        this.accelerometerData = accelerometerData;
    }

    public BandAltimeterEvent getAltimeterData() {
        return altimeterData;
    }

    public void setAltimeterData(BandAltimeterEvent altimeterData) {
        this.altimeterData = altimeterData;
    }

    public BandAmbientLightEvent getAmbientLightData() {
        return ambientLightData;
    }

    public void setAmbientLightData(BandAmbientLightEvent ambientLightData) {
        this.ambientLightData = ambientLightData;
    }

    public BandBarometerEvent getBarometerData() {
        return barometerData;
    }

    public void setBarometerData(BandBarometerEvent barometerData) {
        this.barometerData = barometerData;
    }

    public BandCaloriesEvent getCalorieData() {
        return calorieData;
    }

    public void setCalorieData(BandCaloriesEvent calorieData) {
        this.calorieData = calorieData;
    }

    public BandContactEvent getContactData() {
        return contactData;
    }

    public void setContactData(BandContactEvent contactData) {
        this.contactData = contactData;
    }

    public BandDistanceEvent getDistanceData() {
        return distanceData;
    }

    public void setDistanceData(BandDistanceEvent distanceData) {
        this.distanceData = distanceData;
    }

    public BandGsrEvent getGsrData() {
        return gsrData;
    }

    public void setGsrData(BandGsrEvent gsrData) {
        this.gsrData = gsrData;
    }

    public BandGyroscopeEvent getGyroscopeData() {
        return gyroscopeData;
    }

    public void setGyroscopeData(BandGyroscopeEvent gyroscopeData) {
        this.gyroscopeData = gyroscopeData;
    }

    public BandHeartRateEvent getHeartRateData() {
        return heartRateData;
    }

    public void setHeartRateData(BandHeartRateEvent heartRateData) {
        this.heartRateData = heartRateData;
    }

    public BandPedometerEvent getPedometerData() {
        return pedometerData;
    }

    public void setPedometerData(BandPedometerEvent pedometerData) {
        this.pedometerData = pedometerData;
    }

    public BandRRIntervalEvent getRrIntervalData() {
        return rrIntervalData;
    }

    public void setRrIntervalData(BandRRIntervalEvent rrIntervalData) {
        this.rrIntervalData = rrIntervalData;
    }

    public BandSkinTemperatureEvent getSkinTemperatureData() {
        return skinTemperatureData;
    }

    public void setSkinTemperatureData(BandSkinTemperatureEvent skinTemperatureData) {
        this.skinTemperatureData = skinTemperatureData;
    }

    public BandUVEvent getUvIndexData() {
        return uvIndexData;
    }

    public void setUvIndexData(BandUVEvent uvIndexData) {
        this.uvIndexData = uvIndexData;
    }
}
