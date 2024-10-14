package com.github.hteph.starsys.service.objects;

import com.github.hteph.starsys.service.objects.wrappers.Homeworld;
import com.github.hteph.starsys.enums.BaseElementOfLife;
import com.github.hteph.starsys.enums.Breathing;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Biosphere {

    //TODO chnage this to a wrapper object with just the information needed and not the whole world object
    //circular references!!!
    private Homeworld homeworld;
    private Breathing respiration;
    private BaseElementOfLife baseElement;

    private Creature creature;

}
