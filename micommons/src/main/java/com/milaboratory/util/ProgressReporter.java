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

import java.io.PrintStream;
import java.text.DecimalFormat;

public class ProgressReporter implements Runnable {
    private static final DecimalFormat percentFormat = new DecimalFormat("##.#'%'");
    private final PrintStream stream;
    private final CanReportProgressAndStage reporter;

    public ProgressReporter(CanReportProgressAndStage reporter, PrintStream stream) {
        this.stream = stream;
        this.reporter = reporter;
    }

    public ProgressReporter(CanReportProgressAndStage reporter) {
        this(reporter, System.out);
    }

    public ProgressReporter(final String prefix, final CanReportProgress reporter) {
        this(prefix, reporter, System.out);
    }

    public ProgressReporter(final String prefix, final CanReportProgress reporter, PrintStream stream) {
        this.stream = stream;
        this.reporter = new CanReportProgressAndStage() {
            @Override
            public String getStage() {
                return prefix;
            }

            @Override
            public double getProgress() {
                return reporter.getProgress();
            }

            @Override
            public boolean isFinished() {
                return reporter.isFinished();
            }
        };
    }

    @Override
    public void run() {
        long currentStamp, lastStamp = System.currentTimeMillis();
        double currentProgress, lastProgress = Double.NaN;
        String currentStage, lastStage = null;
        try {
            while (!reporter.isFinished()) {
                Thread.sleep(1000);

                currentProgress = reporter.getProgress();
                currentStage = reporter.getStage();

                if (currentStage == null)
                    currentStage = "null";

                if (currentStage.equals(lastStage) && currentProgress - lastProgress < .2)
                    continue;

                currentStamp = System.currentTimeMillis();

                String sProgress;
                if (Double.isNaN(currentProgress))
                    sProgress = "progress unknown";
                else
                    sProgress = percentFormat.format(currentProgress * 100.0);

                double performance = 1.0 * (currentProgress - lastProgress) / (currentStamp - lastStamp);

                long eta = currentStamp - lastStamp;

                System.out.println(currentStage + ": " + sProgress);

                lastProgress = currentProgress;
                lastStage = currentStage;
                lastStamp = currentStamp;

            }
        } catch (InterruptedException e) {
        }
    }
}
