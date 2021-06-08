package com.github.hteph.repository.objects;

import com.github.hteph.utils.enums.BaseElementOfLife;
import com.github.hteph.utils.enums.Breathing;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Biosphere {

    private String homeworld;
    private Breathing respiration;
    private BaseElementOfLife baseElement;

}
