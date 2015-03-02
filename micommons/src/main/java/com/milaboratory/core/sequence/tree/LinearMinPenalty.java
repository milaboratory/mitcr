/*
 * MiTCR <http://milaboratory.com>
 *
 * Copyright (c) 2010-2013:
 *     Bolotin Dmitriy     <bolotin.dmitriy@gmail.com>
 *     Chudakov Dmitriy    <chudakovdm@mail.ru>
 *
 * MiTCR is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.milaboratory.core.sequence.tree;

public class LinearMinPenalty implements Penalty {
    //public static final LinearMinPenalty INSTANCE_V0 = new LinearMinPenalty(1.5f, 1.0f / 40, 0.0f);
    public static final LinearMinPenalty INSTANCE_V1 = new LinearMinPenalty(2.0f / 12, 1.0f / 40, 0.0f);
    public static final LinearMinPenalty INSTANCE_V2 = new LinearMinPenalty(2.0f / 12, 0.3f / 40, 0.7f);
    public static final LinearMinPenalty INSTANCE_V3 = new LinearMinPenalty(2.0f / 12, 0.8f / 40, 0.2f);
    public final float a, b, thresholdK;

    public LinearMinPenalty(float threshold, float a, float b) {
        this.a = a;
        this.b = b;
        this.thresholdK = threshold;
    }

    @Override
    public float penalty(int quality0, int quality1) {
        return Math.min(quality0, quality1) * a + b;
    }

    @Override
    public float threshold(int length) {
        return thresholdK * length;
    }
}
