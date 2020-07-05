package main_cilabo_ver3_feature.ga;

import java.util.Arrays;

import jfml.FuzzyInferenceSystem;
import main_cilabo_ver3_feature.Main;

public class Individual_KB {
	// ************************************************************
	// Fields
	int id;

	int objectiveNum;

	float[][][] controlPoints = new float[Main.dimension][Main.H][];
	double[] fitness;

	FuzzyInferenceSystem fs;

	// ************************************************************
	// Constructor
	public Individual_KB() {}

	public Individual_KB(int objectiveNum) {
		this.objectiveNum = objectiveNum;

		this.fitness = new double[objectiveNum];
	}

	public Individual_KB(Individual_KB individual) {
		deepCopy(individual);
	}

	// ************************************************************
	// Methods
	public void deepCopy(Individual_KB individual) {
		this.id = individual.getID();
		this.objectiveNum = individual.getObjectiveNum();

		setControlPoints(individual.getControlPoints());
		setFitness(individual.getFitness());
	}


	// ************************************************************
	// GETter
	public int getID() {
		return this.id;
	}

	public int getObjectiveNum() {
		return this.objectiveNum;
	}

	public float[][][] getControlPoints() {
		return this.controlPoints;
	}

	public float[][] getControlPoints(int dimension) {
		return this.controlPoints[dimension];
	}

	public float[] getControlPoints(int dimension, int h) {
		return this.controlPoints[dimension][h];
	}


	public double getFitness(int i) {
		return this.fitness[i];
	}

	public double[] getFitness() {
		return this.fitness;
	}

	public FuzzyInferenceSystem getFS() {
		return this.fs;
	}

	// ************************************************************
	// SETter
	public void setID(int id) {
		this.id = id;
	}

	public void setObjectiveNum(int objectiveNum) {
		this.objectiveNum = objectiveNum;
	}

	/** Deep Copy */
	public void setControlPoints(int n, float[][] controlPoints) {
		this.controlPoints = new float[Main.dimension][][];
		for(int h = 0; h < Main.H; h++) {
			this.controlPoints[n][h] = Arrays.copyOf(controlPoints[n], controlPoints[n].length);
		}
	}

	/** Deep Copy */
	public void setControlPoints(float[][][] controlPoints) {
		this.controlPoints = new float[Main.dimension][Main.H][];
		for(int n = 0; n < Main.dimension; n++) {
			for(int h = 0; h < Main.H; h++) {
				this.controlPoints[n][h] = Arrays.copyOf(controlPoints[n][h], controlPoints[n][h].length);
			}
		}
	}

	public void setFitness(int i, double value) {
		this.fitness[i] = value;
	}

	/** Deep Copy */
	public void setFitness(double[] fitness) {
		this.fitness = Arrays.copyOf(fitness, fitness.length);
	}

	public void setFS(FuzzyInferenceSystem fs) {
		this.fs = fs;
	}
}
