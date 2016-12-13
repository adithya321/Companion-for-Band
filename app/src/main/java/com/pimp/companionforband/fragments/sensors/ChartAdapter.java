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

package com.pimp.companionforband.fragments.sensors;

import android.graphics.RectF;

import com.robinhood.spark.SparkAdapter;

public class ChartAdapter extends SparkAdapter {
    private final float[] yData;
    int i = 0;

    public ChartAdapter() {
        yData = new float[50];
    }

    public void add(Float y) {
        yData[i] = y;
        i = (i < 49) ? i + 1 : 0;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return yData.length;
    }

    @Override
    public Object getItem(int index) {
        return yData[index];
    }

    @Override
    public float getY(int index) {
        return yData[index];
    }

    @Override
    public float getX(int index) {
        return super.getX(index);
    }

    @Override
    public RectF getDataBounds() {
        return super.getDataBounds();
    }

    @Override
    public boolean hasBaseLine() {
        return false;
    }

    @Override
    public float getBaseLine() {
        return super.getBaseLine();
    }
}
