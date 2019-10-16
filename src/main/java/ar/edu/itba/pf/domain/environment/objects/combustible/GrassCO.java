package ar.edu.itba.pf.domain.environment.objects.combustible;

public class GrassCO extends CombustibleObject {

    private final static double COMBUSTION_PER_TIME = .1;

    public GrassCO(double z, double mass) {
        super(z, mass, COMBUSTION_PER_TIME);
    }

    @Override
    public String getCharacterRepresentarion() {
        return super.getCharacterRepresentarion()+"g";
    }
}
