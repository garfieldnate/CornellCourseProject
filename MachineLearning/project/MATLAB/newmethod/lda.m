function lda

xtrain = importdata('xtrain_5.mat');
ytrain = importdata('ytrain_5_5.mat');
xtest = importdata('xtest_5.mat');
ytest = importdata('ytest_5_5.mat');

%compute mean vector
number = zeros(17, 1);
u = zeros(17, 648);
for j = 1:17
    number(ytrain(j)) = number(ytrain(j)) + 1;
    u(ytrain(j), :) = u(ytrain(j), :) + xtrain(j, :);
end
for i = 1:17
    u(i, :) = u(i, :) / number(i);
end

%accuracy
accuracy = 0;
result = zeros(4, 1);
%predict result equals true label
correct = zeros(17, 1);
true = zeros(17, 1);
predict = zeros(17, 1);
for j = 1:4
    h = zeros(17, 1);
    for i = 1:17
        h(i) = log(number(i) / 17) - 0.5 * (norm(xtest(j, :) - u(i, :)) ^ 2);
    end
    [m, index] = max(h);
    result(j) = index;
    if index == ytest(j)
        accuracy = accuracy + 1;
        correct(index) = correct(index) + 1;
    end
    true(ytest(j)) = true(ytest(j)) + 1;
    predict(index) = predict(index) + 1;
end
%accuracy = accuracy / 4;
disp(accuracy);

precision = zeros(17, 1);
recall = zeros(17,1);
f1score = zeros(17,1);

for i = 1:17
     %recall
     recall(i) = correct(i) / true(i);
     %precision
     precision(i) = correct(i) / predict(i);
     %f1score
     f1score(i) = 2 * recall(i) * precision(i) / (recall(i) + precision(i));
end
save('true_5', 'true');
save('predict_5', 'predict');
save('correct_5', 'correct');
%penalty
p = 0;
for j = 1:4
    i_true = 0;
    j_true = 0;
    i_test = 0;
    j_test = 0;
    pos = [1 : 4; 5 : 8; 9 : 12; 13 :16]; 
    if(ytest(j) == 17)
        i_true = 2.5;
        j_true = 2.5;
    else
    [i_true, j_true] = find ( pos == ytest(j) );
    end
    if(result(j) == 17)
       i_test = 2.5;
       j_test = 2.5;
    else
    [i_test, j_test] = find ( pos == result(j) );
    end
    dis = [i_true, j_true; i_test, j_test];
    p = p + pdist(dis,'euclidean');
end
disp(p);