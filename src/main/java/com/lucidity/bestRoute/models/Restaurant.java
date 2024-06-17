package com.lucidity.bestRoute.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Restaurant extends BaseModel{

    private String name;
    @Embedded
    private GeoLocation geoLocation;
    private double preparationTime;
}
