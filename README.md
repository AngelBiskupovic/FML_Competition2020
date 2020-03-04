# FML_Competition2020
This is the data repository for Fuzzy Markup Laungage (FML)-based machine learning competition for human and smart machine co-learning on game of Go at IEEE WCCI 2020.

Please visite the competition page => http://oase.nutn.edu.tw/wcci2020-fmlcompetition/

## Goal 
The goal of this competition is to design accurate and interpretable fuzzy rule-based regression models using FML. 

## Competition Data
The data in this competition are derived from the data on the game of Go (https://deepmind.com/alphago-master-series). There are 60 game data available. Each game data include the prediction by Darkforest AI bot and that by EFL OpenGo AI bot. The competition data can be found in the director **"[Comp2020Data](Comp2020Data)"** in this page. Each data file is named "C2020_GameDataG?.csv" (? represents the game number.) This year, we remove the 25th, 48th, and 60th games because the predictions of the winning rates by Darkforest AI bot and EFL OpenGo AI bot are inconsistent. We use **44 game results from the 1st game to 45th game as the training data**. We use **13 game results from the 46th game to 59th game as the test data**. 
For simplicity, we combined the game results and generated two files: **[C2020_TrainData.csv](Comp2020Data/C2020_TrainData.csv)** and **[C2020_TestData.csv](Comp2020Data/C2020_TrainData.csv)**. 
