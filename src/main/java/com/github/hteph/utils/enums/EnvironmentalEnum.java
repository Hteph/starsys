package com.github.hteph.utils.enums;

/**
 */
public enum EnvironmentalEnum {

 //Classification
 //H demands hydrology
 //h benefits from hydro
 //d reduced by hydro
 // c reduced by high temp
 // t benefits from high temp
 //Z demands old civilisation
 //u unusual or inconvenient

 COASTAL("Coastal Ecosystems","H"),
 ALPINE ("Mountains","u"),
 TUNDRA("Polar Tundra","cu"),
 MIRES("Mires: Swamp, Bog, Fen and Moor", "h"),
 TEMPERATE_AND_SEMI_DESERTS("Temperate Deserts and Semi-Deserts","d"),
 CONIFEROUS_FORESTS("Coniferous Forests","c"),
 TEMPERATE_FORESTS("Temperate Deciduous Forests","h"),
 GRASSLANDS("Natural Grasslands","t"),
 HEATHLANDS("Heathlands and Related Shrublands",""),
 SHRUBLANDS("Mediterranean-Type Shrublands","t"),
 DESERT("Hot Deserts and Arid Shrublands","dtu"),
 TROPICAL_SAVANNAS("Tropical Savannas","th"),
 RAIN_FOREST("Tropical Rain Forest Ecosystems","Ht"),
 WETLAND_FORESTS("Wetland Forests","H"),
 //Managed or Manipulated or created ecosystems
 MANAGED_GRASSLANDS("Managed Grasslands","Z"),
 FIELD_CROP("Field Crop Ecosystems","Zh"),
 TREE_CROP("Tree Crop Ecosystems","Zh"),
 GREENHOUSE("Greenhouse Ecosystems","Z"),
 BIOINDUSTRIAL("Bioindustrial Ecosystems","Zu"),
 //Inland Aquatic systems
 RIVER_AND_STREAM("River and Stream Ecosystems","h"),
 LAKES("Lakes and Reservoirs","h"),
 //Marine Ecosystems
 INTERTIDAL_AND_LITTORAL("Intertidal and Littoral Ecosystems","H"),
 REEFS("Coral Reefs","Hht"),
 ESTUARIES("Estuaries and Enclosed Seas","H"),
 SHELVES("Ecosystems of the Continental Shelves","Hu"),
 DEEP_OCEAN("Ecosystems of the Deep Ocean","Hu"),
 //Managed Aquatic Ecosystems
 MANAGED_AQUATIC("Managed Aquatic Ecosystems","Zu"),
 //Underground Ecosystems
 CAVE("Cave Ecosystems","u"),
 //Exotics
 EXOTIC("Something unique and impossible to describe",""),
 //None (used for secondaries)
 NONE("","");

 private final String description;
 private final String classification;

 EnvironmentalEnum(String description, String classification) {
  this.classification =classification;
  this.description = description;
 }

 public String getDescription() {
  return description;
 }

 public String getClassification() {
  return classification;
 }
}
