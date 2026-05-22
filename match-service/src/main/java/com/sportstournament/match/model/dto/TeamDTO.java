package com.sportstournament.match.model.dto;

public class TeamDTO {
    private Long id;
    private String name;
    private String city;
    private String coachName;
    private int points;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCoachName() { return coachName; }
    public void setCoachName(String coachName) { this.coachName = coachName; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
}