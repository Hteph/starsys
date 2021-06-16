package com.github.hteph.utils.enums;

/**
 */
public enum AttributeEnum implements baseEnum {

    AGILITY("Agility","The power of moving quickly and easily"),
    ALERTNESS("Alertness","How aware and attentive the lifeform is."),
    AUDIO("Hearing","How well do the lifeform utilise soundwaves."),
    CHAUVINISM("Chauvinism","The belief in the superiority or dominance of one's own group or people"),
    CONCENTRATION("Concentration","The ability to focus on a topic for an extended time."),
    CONSTITUTION("Constitution","A measure of how sturdy the lifeforms health is and how well it weathers adverse conditions."),
    CURIOSITY("Curiosity","How interested are the lifeform of exploring and investigate new things."),
    DEPENDENCY("Dependency","The reliance of a substance (or equivalent) of without the lifeform will suffer but not directly die."),
    EMPATHY("Empathy","The power of understanding and imaginatively entering into another person's feelings"),
    FRAME("Body Frame","How is the body of the lifeform distributed in comparison with a human."),
    GREGARIOUSNESS("Gregariousness","How fond of the company of others; sociable is the lifeform."),
    HEALTH("Health", "How resistant the lifeforms body is to invasions of pathogens and poisons"),
    IMAGINATION("Imagination","The faculty of producing ideas, mental images of what is not present or has not been experienced."),
    INTELLIGENCE("Intelligence","The capacity for understanding; ability to perceive and comprehend meaning"),
    LIFESPAN("Lifespan","How do the life length of the lifeforms scale to the average."),
    LIFESUPPORT("Lifesupport", "How well can the Lifeform  survive in a emergency or in a closed system with reduced needs."),
    MOVEMENT("Movement","How well is the lifeformsd body developed for the act of moving from place to place."),
    REFLEXES("Reflexes","Reactions speed to incoming threats and feeding opportunities, as well as other events"),
    SMELL("Smell", "How well developed are the lifeforms sense of smell (airborne chemistry sense)."),
    STEALTH("Stealth","The act or characteristic of moving with extreme care and quietness, as to avoid detection"),
    STRENGTH("Strength", "Physical prowess, muscular strength"),
    STAMINA("Stamina","How well do the lifeforms body work under extended duress"),
    SPEED("Speed", "How fast are the lifeform to react to new situations"),
    SUSPICION("Suspicion", "How difficult is the lifeform to fool and how wary of new situations."),
    SUSTENANCE("Sustenance", "How big is the relative food and nutrient intake."),
    TASTE("Taste", "How well developed are the lifeforms sense of taste (tactile chemistry sense)."),
    VISION("Vision","How well do the lifeform utilise visual senses (electromagnetic waves)"),
    VOICE("Voice", "The lifeform uses sound waves for communication");

    private final String name;
    private final String description;

    AttributeEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }
}
