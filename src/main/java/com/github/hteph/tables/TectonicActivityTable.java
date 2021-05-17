package com.github.hteph.tables;

import com.github.hteph.utils.Dice;

public class TectonicActivityTable {
    public static String findTectonicActivityGroup(double tectonicActivity) {
//TODO This should really be rewritten using Tablemaker
        String tempTectonicActivityGroup;

        if (tectonicActivity < 0.5) {
            tempTectonicActivityGroup = "Dead";
        } else if (tectonicActivity < 1) {

            switch (Dice.d6() + Dice.d6()) {
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                    tempTectonicActivityGroup = "Dead";
                    break;
                case 9:
                case 10:
                case 11:
                    tempTectonicActivityGroup = "Hot Spot";
                    break;
                case 12:
                    tempTectonicActivityGroup = "Plastic";
                    break;
                default:
                    tempTectonicActivityGroup = "Dead";
                    break;
            }
        } else if (tectonicActivity < 2) {
            switch (Dice.d6() + Dice.d6()) {
                case 2:
                case 3:
                case 4:
                    tempTectonicActivityGroup = "Dead";
                    break;
                case 5:
                case 6:
                case 7:
                    tempTectonicActivityGroup = "Hot Spot";
                    break;
                case 8:
                case 9:
                    tempTectonicActivityGroup = "Plastic";
                    break;
                case 10:
                case 11:
                case 12:
                    tempTectonicActivityGroup = "Plate Tectonics";
                    break;
                default:
                    tempTectonicActivityGroup = "Plastic";
                    break;
            }

        } else if (tectonicActivity < 3) {
            switch (Dice.d6() + Dice.d6()) {
                case 2:
                case 3:
                    tempTectonicActivityGroup = "Hot Spot";
                    break;
                case 4:
                case 5:
                case 6:
                case 7:
                    tempTectonicActivityGroup = "Plastic";
                    break;
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                    tempTectonicActivityGroup = "Plate Tectonics";
                    break;
                default:
                    tempTectonicActivityGroup = "Plate Tectonics";
                    break;
            }

        } else if (tectonicActivity < 4) {
            switch (Dice.d6() + Dice.d6()) {
                case 2:
                case 3:
                    tempTectonicActivityGroup = "Hot Spot";
                    break;
                case 5:
                case 6:
                    tempTectonicActivityGroup = "Plastic";
                    break;
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                    tempTectonicActivityGroup = "Plate Tectonics";
                    break;
                default:
                    tempTectonicActivityGroup = "Plate Tectonics";
                    break;
            }

        } else if (tectonicActivity < 5) {
            switch (Dice.d6() + Dice.d6()) {
                case 2:
                case 3:
                    tempTectonicActivityGroup = "Hot Spot";
                    break;
                case 4:
                case 5:
                    tempTectonicActivityGroup = "Plastic";
                    break;
                case 6:
                case 7:
                case 8:
                case 9:
                    tempTectonicActivityGroup = "Plate Tectonics";
                    break;
                case 10:
                case 11:
                case 12:
                    tempTectonicActivityGroup = "Platelet Tectonics";
                    break;
                default:
                    tempTectonicActivityGroup = "Plate Tectonics";
                    break;
            }
        } else {
            switch (Dice.d6() + Dice.d6()) {
                case 2:
                case 3:
                    tempTectonicActivityGroup = "Plastic";
                    break;
                case 4:
                case 5:
                    tempTectonicActivityGroup = "Plate Tectonics";
                    break;
                case 6:
                case 7:
                case 8:
                case 9:
                    tempTectonicActivityGroup = "Platelet Tectonic";
                    break;
                case 10:
                case 11:
                case 12:
                    tempTectonicActivityGroup = "Extreme";
                    break;
                default:
                    tempTectonicActivityGroup = "Platelet Tectonics";
                    break;
            }
        }
        return tempTectonicActivityGroup;
    }
}