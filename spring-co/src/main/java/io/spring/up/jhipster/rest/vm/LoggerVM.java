package io.spring.up.jhipster.rest.vm;

import ch.qos.logback.classic.Logger;

/**
 * View Model object for storing a Logback logger.
 */
public class LoggerVM {

    private String name;

    private String level;

    public LoggerVM(final Logger logger) {
        this.name = logger.getName();
        this.level = logger.getEffectiveLevel().toString();
    }

    public LoggerVM() {
        // Empty public constructor used by Jackson.
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getLevel() {
        return this.level;
    }

    public void setLevel(final String level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "LoggerVM{" +
                "name='" + this.name + '\'' +
                ", level='" + this.level + '\'' +
                '}';
    }
}
