package com.github.hteph.repository.objects;


import com.github.hteph.utils.enums.LimbType;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
public class Limbs implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private LimbType limbType;
    private String autopodia; //limbendings
    private int dexterityBonus;
    private int strengthBonus;

}
