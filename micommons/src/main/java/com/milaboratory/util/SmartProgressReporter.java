package com.milaboratory.util;

import java.io.PrintStream;
import java.text.DecimalFormat;

public class SmartProgressReporter implements Runnable {
    private static final DecimalFormat percentFormat = new DecimalFormat("##.#'%'");
    private final PrintStream stream;
    private final CanReportProgressAndStage reporter;
    private double progressPeriod = 0.10, timePeriod = 120_000;
    private boolean detectStageChange = true;

    public SmartProgressReporter(CanReportProgressAndStage reporter, PrintStream stream) {
        this.stream = stream;
        this.reporter = reporter;
    }

    public SmartProgressReporter(CanReportProgressAndStage reporter) {
        this(reporter, System.out);
    }

    public SmartProgressReporter(final String prefix, final CanReportProgress reporter) {
        this(prefix, reporter, System.out);
    }

    public SmartProgressReporter(final String prefix, final CanReportProgress reporter, PrintStream stream) {
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

    public double getProgressPeriod() {
        return progressPeriod;
    }

    public void setProgressPeriod(double progressPeriod) {
        this.progressPeriod = progressPeriod;
    }

    public double getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(double timePeriod) {
        this.timePeriod = timePeriod;
    }

    public boolean isDetectStageChange() {
        return detectStageChange;
    }

    public void setDetectStageChange(boolean detectStageChange) {
        this.detectStageChange = detectStageChange;
    }

    @Override
    public void run() {
        long currentStamp, lastStamp = System.currentTimeMillis(), deltaTime, et;
        double currentProgress, lastProgress = Double.NaN, deltaValue;
        String currentStage, lastStage = null, etStr;
        boolean trigger;

        try {
            while (!reporter.isFinished()) {

                currentProgress = reporter.getProgress();
                currentStage = reporter.getStage();
                currentStamp = System.currentTimeMillis();

                deltaValue = currentProgress - lastProgress;
                deltaTime = currentStamp - lastStamp;

                trigger = false;

                if (detectStageChange && !currentStage.equals(lastStage))
                    trigger = true;

                if (Double.isNaN(currentProgress) ^ Double.isNaN(lastProgress))
                    trigger = true;

                if (deltaValue >= progressPeriod
                        || deltaTime >= timePeriod)
                    trigger = true;

                if (deltaValue < 0.0) {
                    deltaValue = Double.NaN;
                    trigger = true;
                }

                long hours, minutes, seconds;

                if (trigger) {

                    if (Double.isNaN(deltaValue) || deltaTime == 0 || deltaValue == 0.0)
                        etStr = "";
                    else {
                        et = (long) ((1.0 - currentProgress) * deltaTime / deltaValue);

                        et /= 1000;
                        hours = et / 3600;
                        et -= hours * 3600;
                        minutes = (et) / 60;
                        et -= minutes * 60;
                        seconds = et;

                        etStr = "  ETA: " + hours + ":" + minutes + ":" + seconds;
                    }

                    if (currentStage == null)
                        currentStage = "null";

                    String sProgress;
                    if (Double.isNaN(currentProgress))
                        sProgress = "progress unknown";
                    else
                        sProgress = percentFormat.format(currentProgress * 100.0);

                    stream.println(currentStage + ": " + sProgress + etStr);

                    lastProgress = currentProgress;
                    lastStamp = currentStamp;
                    lastStage = currentStage;
                }

                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
        }
    }
}
