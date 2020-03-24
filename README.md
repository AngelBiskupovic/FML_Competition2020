# Overview of FML Competition in FUZZ-IEEE 2020
This is the overview for Fuzzy Markup Laungage (FML)-based machine learning competition for human and smart machine co-learning on game of Go at IEEE WCCI 2020. This page provides the datasets and the sample codes for the competition. The related information is also listed. 

Please visite the competition page => http://oase.nutn.edu.tw/wcci2020-fmlcompetition/

## Goal 
The goal of this competition is to design accurate and interpretable fuzzy rule-based regression models using FML. 

## Competition Data
The data in this competition are derived from the data on the game of Go (https://deepmind.com/alphago-master-series). There are 60 game data available. Each game data include the prediction by Darkforest AI bot and that by EFL OpenGo AI bot. The competition data can be found in the director **"[Comp2020Data](Comp2020Data)"** in this page. Each data file is named "C2020_GameDataG?.csv" (? represents the game number.) This year, we remove the 25th, 48th, and 60th games because the predictions of the winning rates by Darkforest AI bot and EFL OpenGo AI bot are inconsistent. We use **44 game results from the 1st game to 45th game as the training data**. We use **13 game results from the 46th game to 59th game as the test data**. 
For simplicity, we combined the game results and generated two files: **[C2020_TrainData.csv](Comp2020Data/C2020_TrainData.csv)** and **[C2020_TestData.csv](Comp2020Data/C2020_TestData.csv)**. 

### Data description
Each row represents an instance which consists of 12 input attributes and 2 outputs. 
* The first six inputs are DBSN, DWSN, DBWR, DWWR, DBTMR, and DWTMR at time _t_-1. These values are generated by Darkforest AI bot. (The first letter of each abbreviation represents "D"arkforest.)
* The next six inputs are the same information at time _t_. 
* DBSN: The number of simulations for Black. 
* DWSN: The number of simulations for White. 
* DBWR: The win rate of Black. 
* DWWR: The win rate of White. 
* DBTMR: The top-move rate of Black. 
* DWTMR: The top-move rate of White.
* The two outputs are EBWR at time _t_ and DBWR at time _t_+1.
* EBWR: The win rate of Black predicted by EFL OpenGo AI bot at time _t_. (The first letter of each abbreviation represents "E"FL.)

The names of inputs and outputs in **[C2020_TrainData.csv](Comp2020Data/C2020_TrainData.csv)** and **[C2020_TestData.csv](Comp2020Data/C2020_TestData.csv)** are DBSN(_t_-1), DWSN(_t_-1), DBWR(_t_-1), DWWR(_t_-1), DBTMR(_t_-1), DWTMR(_t_-1), DBSN(_t_), DWSN(_t_), DBWR(_t_), DWWR(_t_), DBTMR(_t_), DWTMR(_t_), EBWR(_t_), and DBWR(_t_+1).

The ranges of all the variables are as follows (The same info can be listed in [data_range.csv](Comp2020Data/data_range.csv)):
| | DBSN | DWSN | DBWR | DWWR | DBTMR | DWTMR | EBWR |
|----|----|----|----|----|----|----|----|
| min | 0 | 0 | 0 | 0 | 0 | 0.4 | 0 |
| max | 22000 | 22000 | 1 | 1 | 1 | 1 | 1 |

## Competition Categories
There are two categories in this competition. 
### Category 1: Prediction of EBWR for the current move
In this category, the goal is **to design a fuzzy rule-based regressoin model which can accurately predict EBWR(_t_) using some or all of the input attributes**. The meaning of this category is as follows. Let assume two players A and B. Player A uses Darkforest AI bot to get the hint of the next moves, while Player B uses EFL OpenGo AI bot to do the same. Each player cannot know the suggestion by the opponent's AI bot. So, Player A uses the fuzzy rule-based regression model to guess the current situation which is predicted by the opponent's AI bot. If it is possible to know that, Player A can evaluate the current situation from multiple viewpoints (i.e., Darkforest and EFL OpenGo). 

### Category 2: Prediction of DBWR for the next move
In this category, the goal is **to design a fuzzy rule-based regressoin model which can accurately predict DBWR(_t_+1) using some or all of the input attributes**. The meaning of this category is as follows. Let assume that Player A wants to know the future situation to change the strategy at a proper moment. To do that, Player A uses the fuzzy rule-based regression model to predict the winning rate for the next move as well. 

## Rules
* Participants must use FML to design a fuzzy rule-based regression model.
* The proposed regression model is evaluated by the mean squread error (MSE) over all instances of the test data ([C2020_TestData.csv](Comp2020Data/C2020_TestData.csv)). 
* Only the training data is available for optimizing/learning the regression model and tuning the parameters of optimization/learning algorithms.
* All the input attributes in the training data are not necessarily used. It means that participants can freely choose a part of the input attributes.

## Evaluation
The participants must submit a zip file the following two files to the [competition website](http://oase.nutn.edu.tw/wcci2020-fmlcompetition/). 
* A short paper which includes the explanation on the own approach, the MSE for the training and test data, and discussions.
* A xml file of the fuzzy rule-based regression model based on FML. 
Any format is acceptable for the short paper. But [IEEE paper format](http://oase.nutn.edu.tw/wcci2020-fmlcompetition/files/conference-template-letter.docx) is highly recommended. The number of pages is not limited. Two to four pages are enough in IEEE paper format. If participants perform two categories, they can include the results of two categories in a single paper.  

The competition committee will evaluate the short paper and the xml file. The competition result will be public in IEEE WCCI 2020.

## Introduction to Fuzzy Markup Language
For more details about FML, please download the FML user guide (Version 0.1.1) from http://kws.nutn.edu.tw/fmldoc/

## Available Softwares
* VisualFMLTool: It can be executed on platforms containing the Java Runtime Environment. The Java Software Development Kit, including JRE, compiler and many other tools can be found at [here](http://java.sun.com/j2se/). The VisualFMLTool can download from [here](http://kws.nutn.edu.tw/fml/) and then to extract it. Then it is only needed to click the file VisualFMLTool.bat included in the zip to execute the tool.

* JFML: A Java library for FML programming that is very simple to use and compliant with IEEE 1855. JFML can download from [here](https://www.uco.es/JFML/). Additional information about the library can be found in the following papers. A Pyhon library is also available. 
  * J. M. Soto-Hidalgo, Jose M. Alonso, G. Acampora, and J. Alcala-Fdez, "[JFML: A Java library to design fuzzy logic systems according to the IEEE Std 1855-2016](https://ieeexplore.ieee.org/document/8476558)," _IEEE Access_, vol. 6, pp. 54952-54964, 2018.
  * J. M. Soto-Hidalgo, A. Vitiello, J. M. Alonso, G. Acampora, J. Alcala-Fdez, "[Design of fuzzy controllers for embedded systems with JFML](https://www.atlantis-press.com/journals/ijcis/125905646)," _International Journal of Computational Intelligence Systems_, vol. 12, no. 1, pp. 204-214, 2019.

## Sample Codes for This Competition
* A sample Eclipse project is available on "[jfml2020_sample](https://github.com/CI-labo-OPU/FML_Competition2020/tree/master/jfml2020_sample)" which is based on JFML. The sample codes can be used by IntelliJ IDEA as well. 

## Reference
* C. S. Lee, M. H. Wang, Y. L. Tsai, L. W. Ko, B. Y. Tsai, P. H. Hung, L. A. Lin, and N. Kubota, "<a href="https://link.springer.com/article/10.1007/s12652-019-01454-4" target="_blank">Intelligent agent for real-world applications on robotic edutainment and humanized co-learning</a>," Journal of Ambient Intelligence and Humanized Computing, 2019.
* C. S. Lee, M. H. Wang, L. W. Ko, Y. Hsiu Lee, H. Ohashi, N. Kubota, Y. Nojima, and S. F. Su, "Human intelligence meets smart machine: a special event at the IEEE International Conference on Systems, Man, and Cybernetics 2018," IEEE Systems, Man, and Cybernetics Magazine, 2019. (DOI: 10.1109/MSMC.2019.2948050)
* C. S. Lee, M. H. Wang, L. W. Ko, N. Kubota, L. A. Lin, S. Kitaoka, Y. T Wang, and S. F. Su, "<a href="https://ieeexplore.ieee.org/document/8344396" target="_blank">Human and smart machine co-learning: brain-computer interaction at the 2017 IEEE International Conference on Systems, Man, and Cybernetics</a>," IEEE Systems, Man, and Cybernetics Magazine, vol. 4, no. 2, pp. 6-13, Apr. 2018.
* C. S. Lee, M. H. Wang, S. C. Yang, P. H. Hung, S. W. Lin, N. Shuo, N. Kubota, C. H. Chou, P. C. Chou, and C. H. Kao, "<a href="http://www.worldscientific.com/doi/abs/10.1142/S0218488517500295" target="_blank">FML-based dynamic assessment agent for human-machine cooperative system on game of Go</a>," International Journal of Uncertainty, Fuzziness and Knowledge-Based Systems, vol. 25, no. 5, pp. 677-705, 2017. <a href = "https://arxiv.org/abs/1707.04828" target="_blank">arXiv</a>
* G. Acampora, "Fuzzy Markup Language: A XML based language for enabling full interoperability in fuzzy systems design,” in G. Acampora, V. Loia, C. S. Lee, and M. H. Wang (editors)," <a href="https://www.springer.com/us/book/9783642354878" target="_blank">On the Power of Fuzzy Markup Language</a>, Springer-Verlag, Germany, Jan. 2013, pp. 17–33.
* IEEE Standards Association, IEEE Standard for Fuzzy Markup Language, Std. 1855-2016, May 2016. [Online] Available: <a href="https://www.springer.com/us/book/9783642354878" target="_blank">https://ieeexplore.ieee.org/document/7479441</a>.
* G. Acampora, B. N. Di Stefano, A. Vitiello, "<a href="https://ieeexplore.ieee.org/stamp/stamp.jsp?arnumber=7587505" target="_blank">IEEE 1855TM: The first IEEE standard sponsored by IEEE Computational Intelligence Society</a>," IEEE Computational Intelligence Magazine, vol. 11, no. 4, pp. 4–6, 2016.
* J. M. Soto-Hidalgo, J. M. Alonso, and J. Alcalá-Fdez, "Java Fuzzy Markup Language," Jan. 2019. [Oneline] Available: <a href="http://www.uco.es/JFML/" target="_blank">http://www.uco.es/JFML/</a>.
* Y. Tian and Y. Zhu, "Better computer Go player with neural network and long-term prediction," 2016 International Conference on Learning Representations (ICLR 2016), San Juan, Puerto Rico, May 2–4, 2016. <a href="https://arxiv.org/pdf/1511.06410.pdf" target="_blank">https://arxiv.org/pdf/1511.06410.pdf</a>
* Y. Tian and L. Zitnick, "Facebook Open Sources ELF OpengGo," May 2018, [Online] Available: <a href="https://research.fb.com/facebook-open-sources-elf-opengo/" target="_blank">https://research.fb.com/facebook-open-sources-elf-opengo/</a>.
* D. Silver, A. Huang, C. J. Maddison, A. Guez, L. Sifre, G. van den Driessche, J. Schrittwieser, I. Antonoglou, V. Panneershelvam, M. Lanctot, S. Dieleman, D. Grewe, J. Nham, N. Kalchbrenner, I. Sutskever, T. Lillicrap, M. Leach, K. Kavukcuoglu, T. Graepel and D. Hassabis, "<a href="https://www.nature.com/articles/nature16961" target="_blank">Mastering the game of Go with deep neural networks and tree search</a>," Nature, no. 529, pp. 484–489, 2016.
* D. Silver, J. Schrittwieser, K. Simonyan, I. Antonoglou, A. Huang, A. Guez, T. Hubert, L. Baker, M. Lai, A. Bolton, Y. Chen, T. Lillicrap, F. Hui, L. Sifre, G. v. d.  Driessche, T. Graepel, and D. Hassabis, "<a href="https://www.nature.com/articles/nature24270" target="_blank">Mastering the game of Go without human knowledge</a>," Nature, vol. 550, pp. 35–359, 2017.
* Deepmind, "AlphaGo Master series: 60 online games,” Jan. 2019. [Online] Available: <a href="https://deepmind.com/research/alphago/match-archive/master/" target="_blank">https://deepmind.com/research/alphago/match-archive/master/</a>.
* C. S. Lee, M. H. Wang, and S. T. Lan, "<a href="https://ieeexplore.ieee.org/document/6979249" target="_blank">Adaptive personalized diet linguistic recommendation mechanism based on type-2 fuzzy sets and genetic fuzzy markup language</a>," IEEE Transactions on Fuzzy Systems, vol. 23, no. 5, pp. 1777-1802, Oct. 2015.
* C. S. Lee, M. H. Wang, H. Hagas, Z. W. Chen, S. T. Lan, S. E. Kuo, H. C. Kuo, and H. H. Cheng, "<a href="http://www.worldscientific.com/doi/abs/10.1142/S0218488512400235" target="_blank">A novel genetic fuzzy markup language and its application to healthy diet assessment</a>," International Journal of Uncertainty, Fuzziness, and Knowledge-Based Systems, vol. 20, no. 2, pp. 247-278, Oct. 2012.
* C. S. Lee, M. H. Wang, L. C. Chen, Y. Nojima, T. X. Huang, J. Woo, N. Kubota, E. Sato-Shimokawara, T. Yamaguchi, "A GFML-based robot agent for human and machine cooperative learning on game of Go," in Proceeding of 2019 IEEE Congress on Evolutionary Computation (IEEE CEC 2019), Wellington, New Zealand, Jun. 10-13, 2019, pp. 793-799.

