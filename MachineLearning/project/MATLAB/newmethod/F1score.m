function F1score

true_1 = importdata('true_1.mat');
true_2 = importdata('true_2.mat');
true_3 = importdata('true_3.mat');
true_4 = importdata('true_4.mat');
true_5 = importdata('true_5.mat');

predict_1 = importdata('predict_1.mat');
predict_2 = importdata('predict_2.mat');
predict_3 = importdata('predict_3.mat');
predict_4 = importdata('predict_4.mat');
predict_5 = importdata('predict_5.mat');

correct_1 = importdata('correct_1.mat');
correct_2 = importdata('correct_2.mat');
correct_3 = importdata('correct_3.mat');
correct_4 = importdata('correct_4.mat');
correct_5 = importdata('correct_5.mat');

true = true_1 + true_2 + true_3 + true_4 + true_5;
correct = correct_1 + correct_2 + correct_3 + correct_4 + correct_5;
predict = predict_1 + predict_2 + predict_3 + predict_4 + predict_5;

recall = correct ./ true;
precision = correct ./ predict;
F1score = 2 * (recall .* precision)./(recall + precision);
save('F1score', 'F1score');
