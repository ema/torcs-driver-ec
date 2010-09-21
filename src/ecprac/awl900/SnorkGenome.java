package ecprac.awl900;

import java.util.Arrays;
import java.util.Random;

import ecprac.torcs.genome.IGenome;

public class SnorkGenome implements IGenome {

	/**
	 * auto generated
	 */
	private static final long serialVersionUID = -6573761157371835493L;
	
	private Random r = new Random(this.hashCode() + System.currentTimeMillis());

	public double _steerAreaDiff;
	public double[] _steerFactors;
	public double[] _speedFactors;
	
	public SnorkGenome() {
		_steerAreaDiff = 0.00;
		_steerFactors = new double[4];
		_speedFactors = new double[3];
		
		_steerFactors[0] = 0.5;
		_steerFactors[1] = 0.7;
		_steerFactors[2] = 1.0;
		_steerFactors[3] = 0.5;
		_speedFactors[0] = 8; 
		_speedFactors[1] = 0.1;
		_speedFactors[2] = 0.30;
	}

	public SnorkGenome(double steerAreaDiff, double[] steerFactors,
			double[] speedFactors) {
		_steerFactors = Arrays.copyOf(steerFactors, steerFactors.length);
		_speedFactors = Arrays.copyOf(speedFactors, speedFactors.length);
		_steerAreaDiff = steerAreaDiff;
	}

	public SnorkGenome(SnorkGenome orig) {
		this(orig._steerAreaDiff, orig._steerFactors, orig._speedFactors);
	}
	
	public SnorkGenome[] recombine(SnorkGenome other) {
		return null;
	}

	
	public void mutate() {
		
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("steerFactors: ");
		for (double d : _steerFactors) {
			sb.append(String.format("%.3f ", d));
		}
		
		sb.append("speedFactors: ");
		for (double d : _speedFactors) {
			sb.append(String.format("%.3f ", d));
		}
		sb.append("steerAreaDiff: ");
		sb.append(String.format("%.3f ", _steerAreaDiff));
		
		return sb.toString();
	}
}
