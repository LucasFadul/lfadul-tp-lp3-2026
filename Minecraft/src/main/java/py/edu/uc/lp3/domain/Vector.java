package py.edu.uc.lp3.domain;

public class Vector {
	private double x;
	private double y;
	private double z;

	public Vector() {
		this(0, 0, 0);
	}

	public Vector(double x, double y, double z) {
		setX(x);
		setY(y);
		setZ(z);
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		if (!Double.isFinite(x)) throw new IllegalArgumentException("La coordenada debe ser finita");
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		if (!Double.isFinite(y)) throw new IllegalArgumentException("La coordenada debe ser finita");
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		if (!Double.isFinite(z)) throw new IllegalArgumentException("La coordenada debe ser finita");
		this.z = z;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}
}
