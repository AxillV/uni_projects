clear; clc;

% Create random tridiagonal matrix A and b=1:n
n = 15;
A = rand(n);
A=diag(diag(A,-1), -1) + diag(diag(A)) + diag(diag(A, 1), 1);
b = [1:n; 2:n+1];

% Convert to band storage
Aband = num2band(A, 1, 1);

% Apply cyclic reduction to Aband to find 
[D, bnew] = band_cyclic_reduction_tridiag(Aband, b);

% Solve with cyclic reduction return values and via MATLAB's \
disp("Solve with cyclic reduction")
diag(D)\bnew'

disp("Normal solve")
A\b'

% Test for diagonally dominant matrix
n=5
A=randi(100,n)-50;
A=diag(diag(A,-1), -1) + diag(diag(A)) + diag(diag(A, 1), 1);
while nnz(sum(abs(A),2)-2*diag(abs(A))>0)
  A=randi(100,n)-50;
  A=diag(diag(A,-1), -1) + diag(diag(A)) + diag(diag(A, 1), 1);
end
Aband = num2band(A, 1, 1);
b = [1:n; 2:n+1];
[D, bnew] = band_cyclic_reduction_tridiag(Aband, b);

display(D)