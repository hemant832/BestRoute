package com.lucidity.bestRoute.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Customer extends BaseModel{

    private String name;
    @Embedded
    private GeoLocation geoLocation;
}

