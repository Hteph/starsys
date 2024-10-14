package com.github.hteph.starsys.service.objects;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TempOrbitalObject {

        private double orbitDistance;
        private char orbitObject;

        public TempOrbitalObject(double orbitDistance) {
            this.orbitDistance = orbitDistance;
            this.orbitObject = '-';
        }

}
