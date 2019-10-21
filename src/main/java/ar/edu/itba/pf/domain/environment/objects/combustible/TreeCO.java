package ar.edu.itba.pf.domain.environment.objects.combustible;

public class TreeCO extends CombustibleObject{

    private final static double COMBUSTION_PER_TIME = .025;

    public TreeCO(double z, double mass, double height) {
        super(z, mass, COMBUSTION_PER_TIME, height);
    }

    @Override
    public String getCharacterRepresentarion() {
        return super.getCharacterRepresentarion()+"t";
    }

}
