package main;

public class Item {
	// ************************************************************
	// Field
	int dimension = 12;
	float DBSN_pre;
	float DWSN_pre;
	float DBWR_pre;
	float DWWR_pre;
	float DBTMR_pre;
	float DWTMR_pre;

	float DBSN;
	float DWSN;
	float DBWR;
	float DWWR;
	float DBTMR;
	float DWTMR;

	float EBWR;
	float DBWR_next;

	// ************************************************************
	// Constructor
	public Item(float[] list) {
		this.DBSN_pre = list[0];
		this.DWSN_pre = list[1];
		this.DBWR_pre = list[2];
		this.DWWR_pre = list[3];
		this.DBTMR_pre = list[4];
		this.DWTMR_pre = list[5];

		this.DBSN = list[6];
		this.DWSN = list[7];
		this.DBWR = list[8];
		this.DWWR = list[9];
		this.DBTMR = list[10];
		this.DWTMR = list[11];

		this.EBWR = list[12];
		this.DBWR_next = list[13];
	}

	// ************************************************************
	// Method
	public float getValue(String attribute) {
		float value = 0;
		switch(attribute){
		case "DBSN_pre":
			value = DBSN_pre;
			break;
		case "DWSN_pre":
			value = DWSN_pre;
			break;
		case "DBWR_pre":
			value = DBWR_pre;
			break;
		case "DWWR_pre":
			value = DWWR_pre;
			break;
		case "DBTMR_pre":
			value = DBTMR_pre;
			break;
		case "DWTMR_pre":
			value = DWTMR_pre;
			break;

		case "DBSN":
			value = DBSN;
			break;
		case "DWSN":
			value = DWSN;
			break;
		case "DBWR":
			value = DBWR;
			break;
		case "DWWR":
			value = DWWR;
			break;
		case "DBTMR":
			value = DBTMR;
			break;
		case "DWTMR":
			value = DWTMR;
			break;

		case "EBWR":
			value = EBWR;
			break;
		case "DBWR_next":
			value = DBWR_next;
			break;
		}
		return value;
	}

	public float getConsequent(int i) {
		float value = 0;
		switch(i) {
		case 0:
			value = EBWR;
			break;
		case 1:
			value = DBWR_next;
			break;
		}
		return value;
	}

	public int getDimension() {
		return this.dimension;
	}

}
