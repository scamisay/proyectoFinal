package ar.edu.itba.pf.domain.environment.objects.combustible;

public class GrassCO extends CombustibleObject {

    private final static double COMBUSTION_PER_TIME = .1;
    private final static double HEIGHT = .7;//en metros

    public GrassCO(double z, double mass) {
        super(z, mass, COMBUSTION_PER_TIME, HEIGHT);
    }

    @Override
    public String getStringRepresentarion() {
        return "GRASS";
    }
}
