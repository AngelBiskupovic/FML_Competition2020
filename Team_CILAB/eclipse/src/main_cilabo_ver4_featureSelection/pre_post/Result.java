package main_cilabo_ver4_featureSelection.pre_post;

import java.io.File;
import java.util.ArrayList;

import jfml.FuzzyInferenceSystem;
import jfml.JFML;
import main.ItemSet;
import main_cilabo_ver4_featureSelection.Main;

public class Result {


	public static void main(String[] args) {
		String DPath = "./dataset/C2020_TrainData.csv";
		String DtestPath = "./dataset/C2020_TestData.csv";
		String DtraPath = "./dataset/Dtra.csv";
		String DvalidPath = "./dataset/Dvalid.csv";

		ItemSet D = Main.loadCSV(DPath);
		ItemSet Dtest = Main.loadCSV(DtestPath);
		ItemSet Dtra = Main.loadCSV(DtraPath);
		ItemSet Dvalid = Main.loadCSV(DvalidPath);

		D = Main.normalize(D);
		Dtest = Main.normalize(Dtest);
		Dtra = Main.normalize(Dtra);
		Dvalid = Main.normalize(Dvalid);

		String dirPath = "./result/DBWR_next_select_20200628-0534_withDeva";
		String sep = File.separator;


		//FML file名 取得
		File dir = new File(dirPath);
		File[] list = dir.listFiles();
		ArrayList<String> fmlList = new ArrayList<>();
		for(int i = 0; i < list.length; i++) {
			if(list[i].getName().contains(".xml")) {
				System.out.println(list[i].getName());
				fmlList.add(list[i].getName());
			}
		}

		//Result
		ArrayList<String> strs = new ArrayList<>();
		String str = "feature,D,Dtest,Dtra,Dvalid";
		strs.add(str);

		//再推論開始
		for(int i = 0; i < fmlList.size(); i++) {
			// Input FML
			String fmlPath = dirPath + sep + fmlList.get(i);
			File fml = new File(fmlPath);
			FuzzyInferenceSystem fs = JFML.load(fml);

			//Feature取得
			String binary = fmlList.get(i);
			binary = binary.replaceAll("[^01]", "");

			ArrayList<String> selectedFeatures = new ArrayList<>();
			for(int j = 0; j < binary.length(); j++) {
				if(binary.charAt(j) == '1') {
					selectedFeatures.add(Main.nameCandidate[j]);
				}
			}
			Main.name = selectedFeatures.toArray(new String[0]);
			Main.dimension = Main.name.length;

			String fileName = dirPath + sep + "a_" + binary + "_";
			double DMSE = Main.inference(fs, D, fileName + "D.csv");
			double DtestMSE = Main.inference(fs, Dtest, fileName + "Dtest.csv");
			double DtraMSE = Main.inference(fs, Dtra, fileName + "Dtra.csv");
			double DvalidMSE = Main.inference(fs, Dvalid, fileName + "Dvalid.csv");

			str = "";
			str += binary;
			str += "," + String.valueOf(DMSE);
			str += "," + String.valueOf(DtestMSE);
			str += "," + String.valueOf(DtraMSE);
			str += "," + String.valueOf(DvalidMSE);

			strs.add(str);
		}

		//output
		String fileName = dirPath + sep + "a_result.csv";
		String[] array = strs.toArray(new String[0]);
		Main.writeln(fileName, array);



	}
}
