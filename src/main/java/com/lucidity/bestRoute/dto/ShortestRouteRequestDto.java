package com.lucidity.bestRoute.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ShortestRouteRequestDto {
    private Long deliveryExecutiveId;
    private List<Long> orderIds;


}
