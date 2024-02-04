package ka.chapter6.item37;

import ka.chapter6.item37.Plant.LifeCycle;

import java.util.*;
import java.util.stream.Collectors;

import static ka.chapter6.item37.Plant.LifeCycle.*;

public class PlantTest {
    static Plant[] garden = new Plant[4];

    public static void setGarden() {
        garden[0] = new Plant("나팔꽃", ANNUAL);
        garden[1] = new Plant("강낭콩", ANNUAL);
        garden[2] = new Plant("구일초", PERENNIAL);
        garden[3] = new Plant("나생이", BIENNIAL);
    }

    public static void main(String[] args) {
        setGarden();

        System.out.println(Arrays.stream(garden)
                .collect(Collectors.groupingBy(p -> p.lifeCycle)));

        System.out.println(Arrays.stream(garden)
                .collect(Collectors.groupingBy(p -> p.lifeCycle,
                        () -> new EnumMap<>(LifeCycle.class), Collectors.toSet())));

//        Map<LifeCycle, Set<Plant>> plantsByLifeCycle = new EnumMap<>(LifeCycle.class);

//        for (LifeCycle lc : values()) {
//            plantsByLifeCycle.put(lc, new HashSet<>());
//        }
//
//        for (Plant plant : garden) {
//            plantsByLifeCycle.get(plant.lifeCycle).add(plant);
//        }

//        System.out.println(plantsByLifeCycle);
    }

    public static void useOrdinal() {
        Set<Plant>[] plantsByLifeCycle = (Set<Plant>[]) new Set[LifeCycle.values().length];

        for (int i = 0; i < plantsByLifeCycle.length; i++) {
            plantsByLifeCycle[i] = new HashSet<>();
        }

        for (Plant p : garden) {
            plantsByLifeCycle[p.lifeCycle.ordinal()].add(p);
        }

        for (int i = 0; i < plantsByLifeCycle.length; i++) {
            System.out.printf("%s: %s\n", LifeCycle.values()[i], plantsByLifeCycle[i]);
        }
    }
}
