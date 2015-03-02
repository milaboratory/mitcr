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
package com.milaboratory.util;

import cc.redberry.pipe.util.CountLimitingOutputPort;

public class ProgressReporterFactory {
    public static CanReportProgress create(Object object) {
        if (object == null)
            return null;
        if (object instanceof CanReportProgress)
            return (CanReportProgress) object;
        if (object instanceof CountLimitingOutputPort) {
            final CountLimitingOutputPort clop = (CountLimitingOutputPort) object;
            final long limit = clop.getLimit();
            return new CanReportProgress() {
                @Override
                public double getProgress() {
                    return 1.0 - 1.0 * clop.getElementsLeft() / limit;
                }

                @Override
                public boolean isFinished() {
                    return clop.isClosed();
                }
            };
        }
        return null;
    }
}
