#include <opencv2/nonfree/nonfree.hpp>
#include <opencv2/opencv.hpp>
#include <opencv2/nonfree/features2d.hpp>
#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <string>
#include <fstream>
#include <vector>


using namespace cv;
using namespace std;
const int DIM = 128;
const int SURF_PARAM = 400;
const string FEATURE_DETECTOR_TYPE = "SURF";      // SIFT, SURF
const string DESCRIPTOR_EXTRACTOR_TYPE = "SURF";  // SIFT, SURF
const int CLASS_COUNT = 10;                       // クラス数
const int VISUAL_WORDS_COUNT = 2000;              // BOW特徴ベクトルの次元 (RGB:1成分あたり)


int main(int argc, char *argv[]){

//	string group_id = argv[1];
	string file_name = argv[1];
//	string dic = "/home/sit-user-15/rails_test/bircle_server3/recognizer/dictionary_2.xml";
//	string rec = "/home/sit-user-15/rails_test/bircle_server3/recognizer/svm_2.xml";
	//cout << file_name << endl;
	Mat img = imread(file_name, 0);
	if (img.empty()) {
		cerr << "Error: Could not open one of the images." << endl;
	}


	// SURFeatureDetector, Extractorの設定
	Ptr<FeatureDetector> detector;
	initModule_nonfree();
	detector = FeatureDetector::create(FEATURE_DETECTOR_TYPE);
	detector->set("hessianThreshold", 100);     // 
	detector->set("nOctaves", 4);				// 
	detector->set("nOctaveLayers", 2);          // 
	detector->set("extended", true);            // 

	// BOW特徴抽出器パラメータ設定
	Ptr<DescriptorExtractor> extractor;
	Ptr<DescriptorMatcher> matcher;
	extractor = DescriptorExtractor::create(DESCRIPTOR_EXTRACTOR_TYPE);
	matcher = DescriptorMatcher::create("FlannBased");
	int clusterCount = 100;//クラスタkの数
	TermCriteria tc(CV_TERMCRIT_ITER, 10, 0.001);
	int attempts = 3;
	int flags = KMEANS_PP_CENTERS;

	BOWKMeansTrainer bowtrainer(clusterCount, tc, attempts, flags);
	BOWImgDescriptorExtractor bowDE(extractor, matcher);

	//辞書の読み込み
	Mat dictionary;
	FileStorage cvfs("/home/sit-user-15/rails_test/bircle_server3/recognizer/dictionary_2.xml", CV_STORAGE_READ);
	FileNode node(cvfs.fs, NULL);
	read(node["dictionary"], dictionary);
	bowDE.setVocabulary("/home/sit-user-15/rails_test/bircle_server3/recognizer/svm_2.xml");
	//svmの設定
	SVM svm;
	svm.load(rec);
	//inputと比較

	Mat input;
	vector<KeyPoint> input_keypoint;
	detector->detect(img, input_keypoint);
	bowDE.compute(img, input_keypoint, input);
	float res = svm.predict(input);
	cout << res << endl;

	return 0;

}