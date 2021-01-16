public class Main {

    public static void main(String[] args)
    {
        String basePath = "";
	    DataConverter sb = new DataConverter(basePath + "sourceData/us-states.csv",
                basePath + "convertedData/caseData.csv", basePath + "convertedData/deathData.csv",
                basePath + "convertedData/newCaseData.csv", basePath + "convertedData/newDeathData.csv",
                basePath + "convertedData/avgNewCaseData.csv", basePath + "convertedData/avgNewDeathData.csv",
               basePath +  "convertedData/totalUSData.csv", basePath + "convertedData/totalCAData.csv",
                basePath + "sourceData/state-fips.csv");

	    sb.readValues();
        sb.writeValues();
    }
}
