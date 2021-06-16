package com.github.hteph.repository.objects;

import com.github.hteph.utils.enums.BaseElementOfLife;
import com.github.hteph.utils.enums.Breathing;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Biosphere {

    //TODO chnage this to a wrapper object with just the information needed and not the whole world object
    //circular references!!!
    private StellarObject homeworld;
    private Breathing respiration;
    private BaseElementOfLife baseElement;

    private Creature creature;

}
