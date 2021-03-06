package a2019.main;

import java.util.Date;

public class Main_new {

	public static void main(String[] args) {
		//読み込みファイル名
//		String traFileName = "DataSets/traData_raw_3.csv";	//evaをもともと抜いている

		//Ndim = 6
		String traFileName = "DataSets/traData_raw_Ndim6.csv";	//moveNo無し
		String tstFileName = "DataSets/tstData_raw_Ndim6.csv";	//moveNo無し
		//Ndim = 7
//		String traFileName = "DataSets/traData_raw_2.csv";	//evaを抜いていない
//		String tstFileName = "DataSets/tstData_raw_2.csv";

//		String evaFileName = "DataSets/evaData_raw_2.csv";
//		DataSetInfo eva = new DataSetInfo(evaFileName);

		//データセット読み込み
		DataSetInfo tra = new DataSetInfo(traFileName);
		DataSetInfo tst = new DataSetInfo(tstFileName);
		SettingForGA setting = new SettingForGA(tra);
		setting.outputSetting();

		FmlGaManager gaManager = new FmlGaManager();
		DataSetInfo eva = gaManager.pickEva(setting, tra);

		FmlManager fmlManager = new FmlManager(tra, tst, eva, setting);

		Date start = new Date();
		System.out.println(start);

		gaManager.gaFrame(setting, fmlManager, tra, tst, eva);


		Date end = new Date();
		System.out.println(end);
		System.out.println("");
	}

}
