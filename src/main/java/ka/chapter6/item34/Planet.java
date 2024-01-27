package ka.chapter6.item34;

public enum Planet {
    MERCURY(1.111e+11, 2.111e1),
    VENUS(1.222e+22, 2.222e2),
    EARTH(1.333e+33, 2.333e3),
    MARS(1.444e44, 2.444e4),
    JUPITER(1.555e55, 2.555e5),
    SATURN(1.666e66, 2.666e6),
    URANUS(1.777e77, 2.777e7),
    NEPTUNE(1.888e88, 2.888e8);

    private final double mass;
    private final double radius;
    private final double surfaceGravity;

    private static final double G = 6.67300E-11;

    Planet(double mass, double radius) {
        this.mass = mass;
        this.radius = radius;
        surfaceGravity = G * mass / (radius * radius);
    }

    public double mass() {return mass;}
    public double radius() {return radius;}
    public double surfaceGravity() {return surfaceGravity;}

    public double surfaceWeight(double mass) {
        return mass * surfaceGravity;
    }
}
