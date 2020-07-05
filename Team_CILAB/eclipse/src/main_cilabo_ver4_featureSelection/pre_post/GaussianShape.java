package main_cilabo_ver4_featureSelection.pre_post;

import java.util.ArrayList;

import jfml.knowledgebase.variable.FuzzyVariableType;
import jfml.term.FuzzyTermType;
import main_cilabo_ver3_feature.Main;

public class GaussianShape {
	public static void main(String[] args) {

		String[] name = {"VerySmall", "Small", "Medium", "Large", "VeryLarge", "Don't Care"};

		FuzzyVariableType variable = new FuzzyVariableType("variable", 0f, 1f);
		FuzzyTermType verySmall = new FuzzyTermType("VerySmall",
													FuzzyTermType.TYPE_gaussianShape,
													new float[] {0f, 0.105f});
		FuzzyTermType Small = new FuzzyTermType("Small",
				FuzzyTermType.TYPE_gaussianShape,
				new float[] {0.25f, 0.105f});
		FuzzyTermType medium = new FuzzyTermType("Medium",
				FuzzyTermType.TYPE_gaussianShape,
				new float[] {0.5f, 0.105f});
		FuzzyTermType large = new FuzzyTermType("Large",
				FuzzyTermType.TYPE_gaussianShape,
				new float[] {0.75f, 0.105f});
		FuzzyTermType veryLarge = new FuzzyTermType("VeryLarge",
				FuzzyTermType.TYPE_gaussianShape,
				new float[] {1f, 0.105f});
		FuzzyTermType dc = new FuzzyTermType("Don't Care",
				FuzzyTermType.TYPE_rectangularShape,
				new float[] {0f, 1f});

		variable.addFuzzyTerm(verySmall);
		variable.addFuzzyTerm(Small);
		variable.addFuzzyTerm(medium);
		variable.addFuzzyTerm(large);
		variable.addFuzzyTerm(veryLarge);
		variable.addFuzzyTerm(dc);

		float[] x = new float[101];
		for(int i = 0; i < 100; i++) {
			x[i] = (float)i/100f;
		}
		x[100] = 1f;

		ArrayList<String> strs = new ArrayList<>();
		String str = "";

		str = "x";
		for(int i = 0; i < name.length; i++) {
			str += "," + name[i];
		}
		strs.add(str);

		for(int i = 0; i < x.length; i++) {
			str = String.valueOf(x[i]);
			for(int j = 0; j < name.length; j++) {
				float y = variable.getFuzzyTerm(j).getMembershipValue(x[i]);
				str += "," + String.valueOf(y);
			}
			strs.add(str);
		}

		String fileName = "GaussianShape.csv";
		String[] array = strs.toArray(new String[0]);
		Main.writeln(fileName, array);

	}
}
