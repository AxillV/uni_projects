function Y = ttm_1084567(X, M, N)
% TTM_1084567 Compute the N-mode tensor-matrix multiplication
%
% Y = ttm_1084567(X, M, N) returns tensor Y
% 
% size(X, N) should be equal to size(M, 2)
%

%%%
% Checks
%%%
if ismatrix(X)
    error('Input was not a tensor')
end

if ~ismatrix(M)
    error('Input was not a matrix')
end

if size(X, N) ~= size(M, 2)
    error('Dimensions in inputs are not compatible')
end

% The resulting dimensions to reshape back into
res_dims = size(X);
res_dims(N) = size(M, 1); % dimension N will the size of the first dimension of M

% Move permutate tensor so that dimension-N is now the first dimension
perm_order = unique([N, 1:ndims(X)], 'stable');
Y = permute(X, perm_order);

% Unfold the tensor and multiply dimension-N with the matrix from the left
% (Equivalent to permutating dimension-N to the last dimension and
% multiplying from the right).
Y = M * Y(:,:);
Y = reshape(Y, res_dims);

end