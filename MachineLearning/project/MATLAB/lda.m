function lda

xfeature = importdata('y16_var00175_feature.mat');
xdata = load('X_train.csv');
ydata = importdata('y16.mat');
newxdata = xdata(:, xfeature);

b = randperm(1953);
xtrain = newxdata(b(1:1560), :);
ytrain = ydata(b(1:1560), :);
xtest = newxdata(b(1561:1953), :);
ytest = ydata(b(1561:1953));

%compute mean vector
number = zeros(16, 1);
u = zeros(16, 609);
for j = 1:1560
    number(ytrain(j)) = number(ytrain(j)) + 1;
    u(ytrain(j), :) = u(ytrain(j), :) + xtrain(j, :);
end
for i = 1:16
    u(i, :) = u(i, :) / number(i);
end

%accuracy
accuracy = 0;
result = zeros(393, 1);
%predict result equals true label
correct = zeros(16, 1);
true = zeros(16, 1);
predict = zeros(16, 1);
for j = 1:393
    h = zeros(16, 1);
    for i = 1:16
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

precision = zeros(16, 1);
recall = zeros(16,1);
f1score = zeros(16,1);

for i = 1:16
     %recall
     recall(i) = correct(i) / true(i);
     %precision
     precision(i) = correct(i) / predict(i);
     %f1score
     f1score(i) = 2 * recall(i) * precision(i) / (recall(i) + precision(i));
end
save('recall', 'recall');
save('precision', 'precision');
save('F1score', 'f1score');
plot([1:16], recall,'r', [1:16], precision,'b', [1:16], f1score, 'k');
     %penalty

p = 0;
for j = 1:393
    weight = true / 393;
    pos = [1 : 4; 5 : 8; 9 : 12; 13 :16];     
    [i_true, j_true] = find ( pos == ytest(j) );
    [i_test, j_test] = find ( pos == result(j) );
    dis = [i_true, j_true; i_test, j_test];
    p = p + weight ( ytest(j) ) *  pdist(dis,'euclidean');
end
disp(p);