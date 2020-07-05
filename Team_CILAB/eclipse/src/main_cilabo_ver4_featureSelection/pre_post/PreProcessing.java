package main_cilabo_ver4_featureSelection.pre_post;

import java.util.ArrayList;

import main.Item;
import main.ItemSet;
import main_cilabo_ver4_featureSelection.Main;
import main_cilabo_ver4_featureSelection.rnd.MersenneTwisterFast;

public class PreProcessing {

	public static void main(String[] args) {
		String pathD = "./dataset/C2020_TrainData.csv";
		String pathDtra = "./dataset/Dtra.csv";
		String pathDeva = "./dataset/Deva.csv";

		ItemSet D = Main.loadCSV(pathD);
		int numD = D.getItemSize();
		int numDeva = numD / 3;
		int numDtra = numD - numDeva;

		ItemSet[] divided = samplingWithout(D, numDeva);
		ItemSet Dtra = divided[0];
		ItemSet Deva = divided[1];

		String header = ",DBSN(t-1),DWSN(t-1),DBWR(t-1),DWWR(t-1),DBTMR(t-1),DWTMR(t-1)," +
		"DBSN(t),DWSN(t),DBWR(t),DWWR(t),DBTMR(t),DWTMR(t),EBWR(t),DBWR(t+1)";

		outputDataset(pathDtra, Dtra, header);
		outputDataset(pathDeva, Deva, header);




	}

	public static void outputDataset(String fileName, ItemSet dataset, String header) {
		ArrayList<String> strs = new ArrayList<>();
		String str = "";

		//header
		strs.add(header);

		//dataset
		for(int p = 0; p < dataset.getItemSize(); p++) {
			Item item = dataset.getItem(p);

			str = String.valueOf(p);
			for(int n = 0; n < item.getDimension(); n++) {
				str += "," + String.valueOf(item.getValue(Main.nameCandidate[n]));
			}
			str += "," + String.valueOf(item.getValue("EBWR"));
			str += "," + String.valueOf(item.getValue("DBWR_next"));
			strs.add(str);
		}

		String[] array = strs.toArray(new String[0]);
		Main.writeln(fileName, array);
	}


	public static ItemSet[] samplingWithout(ItemSet train, int numPick) {
		ItemSet[] divided = new ItemSet[2];
		int seed = 0;
		MersenneTwisterFast rnd = new MersenneTwisterFast(seed);

		ArrayList<Integer> list = new ArrayList<>();
		for(int p = 0; p < train.getItemSize(); p++) {
			list.add(p);
		}

		//Deva
		divided[1] = new ItemSet();
		for(int p = 0; p < numPick; p++) {
			int index = list.remove(rnd.nextInt(list.size()));
			divided[1].addItem(train.getItem(index));
		}

		//Dtra
		divided[0] = new ItemSet();
		for(int p = 0; p < list.size(); p++) {
			divided[0].addItem(train.getItem(list.get(p)));
		}

		return divided;
	}
}
