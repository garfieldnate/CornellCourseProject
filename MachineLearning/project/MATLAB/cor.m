function [index] = cor

x = load('X_train.csv');
y = load('y_train.csv');
newy = importdata('y16.mat');
R = zeros(1, 2511);
P = zeros(1, 2511);
origin = ones(1, 2511);
xvar = var(x);
index = find(xvar < 0.015);
origin(index) = 0;

for i = 1:2511
    [rl, pvalue] = corrcoef(x(:,i), newy);
    R(i) = rl(1,2);
    P(i) = pvalue(1,2);
end

index = find(P >= 0.05);
origin(index) = 0;
index = find(abs(R) <= 0.15);
origin(index) = 0;
index = find(origin ~= 0);

figure;
plot([1:2511], R, 'r', [1:2511], P, 'b');