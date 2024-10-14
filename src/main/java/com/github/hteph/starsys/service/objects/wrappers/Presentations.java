package com.github.hteph.starsys.service.objects.wrappers;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
@Builder
public class Presentations {

    HashMap<String,String> facts;
    List<Presentations> MoonPresentations;
    HashMap<String,String> BioSpherePresentations;

}
