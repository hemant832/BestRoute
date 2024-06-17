package com.lucidity.bestRoute.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShortestRouteResponseDto {
    private double time;
    private String bestRoute;
    private ResponseStatus responseStatus;
}
