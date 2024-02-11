package com.antonov.vlmart.render;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ExpandWrap {
    private List<PlaceExpandWrap> shopPlaceExpands;
    private List<PlaceExpandWrap> storePlaceExpands;
}
