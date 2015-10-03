function angle_predictknn
xdata = load('X_train.csv');
ydata = importdata('y16.mat');
xfeature = importdata('y16_feature.mat');
newxdata = xdata(:, xfeature);

b = randperm(1953);
xtrain = newxdata(b(1:1560), :);
ytrain = ydata(b(1:1560));
xtest = newxdata(b(1561:1953), :);
ytest = ydata(b(1561:1953));
xnorm = (xtrain - min(min(xtrain))) / (max(max(xtrain)) - min(min(xtrain)));

% weight = zeros(16, 1);
% for j = 1:1953
%     weight(ydata(j)) = weight(ydata(j)) + 1;
% end
% weight = weight / 1953;
% disp(weight);

Mdlk = fitcknn(xnorm, ytrain, 'NumNeighbors', 8, 'Distance', 'cosine');
result = predict(Mdlk, xtest);
accuracy = 0;
for i = 1:393
    if result(i) == ytest(i)
        accuracy = accuracy + 1;
    end
end

disp(accuracy / 393);