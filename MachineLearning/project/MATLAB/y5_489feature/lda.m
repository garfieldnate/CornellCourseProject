function lda

xtrain = importdata('xtrain_5_489.mat');
ytrain = importdata('ytrain_5.mat');
xtest = importdata('xtest_5_489.mat');
ytest = importdata('ytest_5.mat');

%compute mean vector
number = zeros(5, 1);
u = zeros(5, 489);
for j = 1:1560
    number(ytrain(j)) = number(ytrain(j)) + 1;
    u(ytrain(j), :) = u(ytrain(j), :) + xtrain(j, :);
end
for i = 1:5
    u(i, :) = u(i, :) / number(i);
end

%accuracy
accuracy = 0;
result = zeros(393, 1);
%predict result equals true label
correct = zeros(5, 1);
true = zeros(5, 1);
predict = zeros(5, 1);
for j = 1:393
    h = zeros(5, 1);
    for i = 1:5
        h(i) = log(number(i) / 1560) - 0.5 * (norm(xtest(j, :) - u(i, :)) ^ 2);
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
accuracy = accuracy / 393;
disp(accuracy);

precision = zeros(5, 1);
recall = zeros(5,1);
f1score = zeros(5,1);

for i = 1:5
     %recall
     recall(i) = correct(i) / true(i);
     %precision
     precision(i) = correct(i) / predict(i);
     %f1score
     f1score(i) = 2 * recall(i) * precision(i) / (recall(i) + precision(i));
end
save('F1score', 'f1score');
%penalty
p = 0;
for j = 1:393
    i_true = 0;
    j_true = 0;
    i_test = 0;
    j_test = 0;
    pos = [1 2; 4 3]; 
    if(ytest(j) == 5)
        i_true = 1.5;
        j_true = 1.5;
    else
    [i_true, j_true] = find ( pos == ytest(j) );
    end
    if(result(j) == 5)
       i_test = 1.5;
       j_test = 1.5;
    else
    [i_test, j_test] = find ( pos == result(j) );
    end
    dis = [i_true, j_true; i_test, j_test];
    p = p + pdist(dis,'euclidean');
end
disp(p / (393 - sum(correct)));