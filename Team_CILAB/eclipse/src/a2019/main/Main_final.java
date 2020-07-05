package a2019.main;

import java.util.Date;

public class Main_final {

	public static void main(String[] args) {
		//読み込みファイル名

		//Ndim = 6
		String traFileName = "dataset/C2020_TrainData.csv";	//moveNo無し
		String tstFileName = "dataset/C2020_TestData.csv";	//moveNo無し

		//データセット読み込み
		DataSetInfo tra = new DataSetInfo(traFileName);
		DataSetInfo tst = new DataSetInfo(tstFileName);
		SettingForGA setting = new SettingForGA(tra);
		setting.setArgument(args);
		setting.outputSetting();


		FmlGaManager gaManager = new FmlGaManager();
		DataSetInfo eva;
		if(setting.useEVA) {
			eva = gaManager.pickEva(setting, tra);
		} else {
			eva = new DataSetInfo();
		}


		FmlManager fmlManager = new FmlManager(tra, tst, eva, setting);

		Date start = new Date();
		System.out.println(start);

		FMLpopulation fmlPopulation = new FMLpopulation(setting);
		gaManager.gaFrame2(setting, fmlPopulation, tra, eva, tst);

		Date end = new Date();
		System.out.println(end);
		System.out.println("");

	}

}
