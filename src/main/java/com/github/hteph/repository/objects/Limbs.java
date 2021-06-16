package com.github.hteph.repository.objects;


import com.github.hteph.utils.enums.LimbType;
import lombok.Data;

@Data
public class Limbs {

    private LimbType limbType;
    private String autopodia; //limbendings
    private int dexterityBonus;
    private int strengthBonus;

}
