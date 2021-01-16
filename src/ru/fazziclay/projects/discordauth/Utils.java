package ru.fazziclay.projects.discordauth;

import java.util.Random;

public class Utils {
    public static int getRandom(int minimum, int maximum) {
        Random random = new Random();
        return random.nextInt(maximum - minimum + 1) + minimum;
    }

    public static boolean getChances (int chance) {
        return getRandom(0, 100) <= chance;
    }
}
