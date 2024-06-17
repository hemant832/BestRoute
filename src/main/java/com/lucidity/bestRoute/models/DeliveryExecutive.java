package com.lucidity.bestRoute.models;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity
public class DeliveryExecutive extends BaseModel{

    private String name;
    @Embedded
    private GeoLocation geoLocation;
}
