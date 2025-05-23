package util;

import grid.Grid;

import java.awt.*;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Cells {
    BODY(new Color(252, 172, 35),
            "Serves to provide a surface for more useful cells to connect to.<br>" +
            "Protects the brain."),
    MOUTH(new Color(69, 130, 209),
            "Allows a creature to eat food.<br>" +
            "Quite important."),
    LEG(new Color(123, 66, 245),
            "Capable of moving a handful of cells.<br>" +
            "A shortage of Legs causes movement to occur slower."),
    DAMAGER(new Color(209, 69, 69),
            "Hurts everything it touches.<br> " +
            "Also functions as a Mouth."),
    CHLOROPLAST(new Color(43, 194, 156),
            "Produces Glucose for the creature at regular intervals.<br>" +
            "Although useful for stationary creatures,<br>" +
            "its weight causes it to consume more energy<br>" +
            "than it produces while moving."),
    EYE(new Color(207, 31, 154),
            "Allows a creature to view its surroundings."),
    CHITIN(new Color(237, 131, 2),
            "Protects against Damagers.<br>" +
                    "Uses a bit of energy."),
    SHARER(new Color(230, 151, 145),
            "Gives extra energy to other Sharers."),
    SPINNER(new Color(133, 197, 214),
            "Leaves behind a paralyzing agent."),
    BOMBER(new Color(232, 86, 122),
            "Leaves behind an explosive agent."),
    BRAIN(new Color(252, 205, 35),
            "The most important cell.<br>" +
            "Controls behavior.<br>" +
            "There is exactly 1 per creature."),
    NULL(Grid.EMPTY_COLOR, Grid.EMPTY_TOOLTIP),

    PLANT(new Color(50, 168, 82), "Appears everywhere."),
    MEAT(new Color(168, 50, 50), "Yielded from a creature's death."),
    GLUCOSE(new Color(43, 194, 119), "Produced by Chloroplasts."),
    EGG(new Color(237, 198, 26), "Hatches into a new creature after some time.<br>Very tasty."),
    ROT(new Color(145, 82, 54), "Dead biomass.<br>More useful to Chloroplasts."),
    OFFERING(new Color(121, 112, 179), "An offering from one creature to another."),
    TRAP(new Color(190, 221, 230), "Slows creatures."),
    BOMB(new Color(255, 94, 0), "Explodes creatures."),
    ;

    public static final Map<Color, Cells> REVERSE =
            Arrays.stream(Cells.values()).collect(Collectors.toMap(
                Cells::get,
                Function.identity()
            ));
    private final Color color;
    private final String description;

    Cells(Color color, String description) {
        this.color = color;
        this.description = wrapHtml(description);
    }

    public static String wrapHtml(String toWrap) {
        return "<html>" + toWrap + "</html>";
    }

    public Color get() {
        return color;
    }

    public String getDescription() {
        return description;
    }
}
