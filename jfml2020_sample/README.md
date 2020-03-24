# JFML2020 Sample Codes

This is an Eclipse project for a sample for JFML competition.
 _[FML-based Machine Learning Competition for Human and Smart Machine Co-Learning on Game of Go @ WCCI2020](http://oase.nutn.edu.tw/wcci2020-fmlcompetition/overview.php)_

The sample package is placed at "src/main".
The "jfml" package is original package of the _[JFML library](https://www.uco.es/JFML/)_.

You can execute a sample with the sample package and the original JFML library.

The simplest way is to download or clone "[FML_Competition2020](https://github.com/CI-labo-OPU/FML_Competition2020.git)" and open the folder "jfml2020_sample" from Eclipse.

Eclipse can be downloaded from [here](https://www.eclipse.org).

## About the sample package
This package includes three java source codes as follows:
 + Item.java
 + ItemSet.java
 + Main.java

"Item.java" and "ItemSet.java" are the dataset implementations for this competition.
"Main.java" is a sample main procedure of designing the fuzzy inference system based on JFML library.
In the sample system, only two triangular membership functions "small" and "large" are used for each attribute.

You can execute the "Main.java" with no arguments on Eclipse,
or you can export the main function in "Main.java" to JAR file and execute the JAR file.

Once your execute the "Main.java", the mean square errors for the training data and test data are shown in the console like:

    Training: MSE = 0.05370033
        Test: MSE = 0.09745144

This result is for Category 1. By refreshing the package, you will find the result files named "EBWR\_H2\_ite100\_train.csv" and "EBWR\_H2\_ite100\_test.csv" together with the xml file named "FML_EBWR.xml".

To obtain the result for Category 2, you have to comment out the 69th line of "Main.java" and uncomment the 70th line.

    // static String outputName = "EBWR";   // Category 1
    static String outputName = "DBWR_next";    // Category 2

Enjoy!

Updated: 2020/03/24
