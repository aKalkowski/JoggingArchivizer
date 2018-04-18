package com.andrzejkalkowski.joggingarchivizer.Model;

/**
 * Created by komputerek on 17.04.18.
 */

public final class Training {

    private String activityType;
    private double averageSpeed;
    private double distance;
    private int burntCalories;
    private int seconds;

    public Training() {
    }

    public Training(final TrainingBuilder builder) {
        this.activityType = builder.activityType;
        this.distance = builder.distance;
        this.burntCalories = builder.burntCalories;
        this.seconds = builder.seconds;
        this.averageSpeed = distance/seconds * 3600;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setBurntCalories(int burntCalories) {
        this.burntCalories = burntCalories;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public String getActivityType() {
        return activityType;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public double getDistance() {
        return distance;
    }

    public int getBurntCalories() {
        return burntCalories;
    }

    public int getSeconds() {
        return seconds;
    }

    public static class TrainingBuilder {
        private final String activityType;
        private double distance;
        private int burntCalories;
        private int seconds;

        public TrainingBuilder(final String activityType) {
            this.activityType = activityType;
        }

        public TrainingBuilder distance(final double distance) {
            this.distance = distance;
            return this;
        }

        public TrainingBuilder burntCalories(final int burntCalories) {
            this.burntCalories = burntCalories;
            return this;
        }

        public TrainingBuilder seconds(final int seconds) {
            this.seconds = seconds;
            return this;
        }

        public Training build() {
            return new Training(this);
        }
    }

    @Override
    public String toString() {
        return "Training{" +
                "activityType='" + activityType + '\'' +
                ", averageSpeed=" + averageSpeed +
                ", distance=" + distance +
                ", burntCalories=" + burntCalories +
                ", seconds=" + seconds +
                '}';
    }
}
