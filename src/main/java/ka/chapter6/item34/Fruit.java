package ka.chapter6.item34;

public class Fruit {
    private static void printApple(Apple apple) {
        switch (apple) {
            case FUJI -> System.out.println("FUJI");
            case PIPPIN -> System.out.println("PIPPIN");
            case GRANNY_SMITH -> System.out.println("GRANNY_SMITH");
        }
    }
    public static void main(String[] args) {

    }
}

enum Apple { FUJI, PIPPIN, GRANNY_SMITH }

enum Orange { NAVEL, TEMPLE, BLOOD }
