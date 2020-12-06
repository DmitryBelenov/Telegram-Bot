package net.bot;

import net.bot.scenario.Scenario;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StepByStepUserScenarios {
    public static Map<Long, Scenario> scMap = new ConcurrentHashMap<>();
}
