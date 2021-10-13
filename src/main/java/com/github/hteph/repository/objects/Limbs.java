package com.github.hteph.repository.objects;


import com.github.hteph.utils.enums.LimbType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Limbs {

    private LimbType limbType;
    private String autopodia; //limbendings
    private int dexterityBonus;
    private int strengthBonus;

}
