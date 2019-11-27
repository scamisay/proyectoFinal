package ar.edu.itba.pf.domain.drone;


import ar.edu.itba.pf.domain.environment.impl.Cell;
import ar.edu.itba.pf.domain.environment.objects.DroneObject;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class Drone implements DroneObject {

    private int id;
    private double mass;

    //recursos
    private double water;
    private double energy;

    //valores internos
    private double dt;
    private Vector2D position;
    private Vector2D velocity = new Vector2D(0,0);
    private Vector2D acceleration = new Vector2D(0,0);


    //private Cell cell;


    public Drone(int id, double mass, double water, double energy, double dt, double x, double y) {
        this.id = id;
        this.mass = mass;
        this.water = water;
        this.energy = energy;
        this.dt = dt;
        this.position = new Vector2D(x,y);
    }

    private static final double HEIGHT = 1;

    public double getZ0() {
        return -1;
    }

    @Override
    public double getX() {
        return position.getX();
    }

    @Override
    public double getY() {
        return position.getY();
    }

    @Override
    public double getZ() {
        return 0;
    }

    @Override
    public String getStringRepresentarion() {
        return "D";
    }

    @Override
    public void setCell(Cell cell) {
      /*  ValidatorHelper.checkNotNull(cell);
        this.cell = cell;*/
    }

    @Override
    public double evolve() {
        // x(t+dt) = x(t) + v(t+dt/2)*dt
        position = position.add( velocity.scalarMultiply(dt) );

        // a(t+dt) = F(v(t+dt/2), x(t+dt)) / m
        acceleration = calculateForce().scalarMultiply(1/mass);

        // v(t+3*dt/2) = v(t+dt/2) + a(t+dt)*dt
        velocity = velocity.add(acceleration.scalarMultiply(dt));
        return 0;
    }

    /**
     * Understanding Social-Force Model in Psychological Principles of Collective BehaviorPeng Wang
     */
    private Vector2D calculateForce() {
        Vector2D granularForce = new Vector2D(0,0);
        double A = 2000;
        double B = 0.08;
        Vector2D socialForce = calculateSocialForce( A, B);

        Vector2D target = findClosestTarget();
        double TAU = dt*1;
        double drivenVelocity = .25;

        Vector2D drivenForce = calculateDrivenForce(drivenVelocity, TAU, target);
        return granularForce.add(socialForce).add(drivenForce);
    }

    private Vector2D findClosestTarget() {
        return new Vector2D(2,4);
    }

    /**
     *
     * @param drivenVelocity
     * @param tau certain time interval
     * @param target
     * @return
     */
    private Vector2D calculateDrivenForce(double drivenVelocity, double tau, Vector2D target) {
        Vector2D e_target = target.subtract(position).normalize();
        return e_target.scalarMultiply(drivenVelocity)
                .subtract(velocity)
                .scalarMultiply(mass/tau);
    }

    private Vector2D calculateSocialForce(Double A, Double B) {
        /*Set<Particle> collisionsWithParticles, Double A, Double B
        return collisionsWithParticles.stream()
                .filter(p->overlap(p)>0)
                .filter(p->p.isWall==false)
                .map( p -> getNormalVersor(p).scalarMultiply(-A*Math.exp(-overlap(p)/B)))
                .reduce( (v1,v2) -> v1.add(v2)).orElse(new Vector2D(0,0));*/
        return new Vector2D(0,0);
    }

    @Override
    public double getRadio() {
        return 0;
    }

    public int getId() {
        return id;
    }

    public double getMass() {
        return mass;
    }

    public double getWater() {
        return water;
    }

    public double getEnergy() {
        return energy;
    }

    public Vector2D getPosition() {
        return position;
    }
}
