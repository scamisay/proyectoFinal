package ar.edu.itba.pf.web.domain;

import org.springframework.data.annotation.Id;

public class Simulation {

    @Id
    private Long id;

    private int width;
    private int height;
    private long endingTime;

    public Simulation(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Long getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getEndingTime() {
        return endingTime;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEndingTime(long endingTime) {
        this.endingTime = endingTime;
    }
}
