package ar.edu.itba.pf.domain.environment.objects.combustible;

public class TreeCO extends CombustibleObject{

    private final static double COMBUSTION_PER_TIME = .025;

    public TreeCO(int z, double mass, double height) {
        super(z, mass, COMBUSTION_PER_TIME, height);
    }

    @Override
    public String getStringRepresentarion() {
        return "TREE";
    }

}
