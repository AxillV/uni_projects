function Y = ttt_1084567(X1, X2, varargin)
% TTT_1084567 Compute the outter or inner tensor-tensor multiplication
%
% Y = ttm_1084567(X1, X2) returns the outter product
% 
% Y = ttm_1084567(X1, X2, 'all') returns the inner product
%
% the sizes of the two tensors should be identical
%

%%%
% Checks
%%%
if ismatrix(X1)
    error('Input X1 was not a tensor')
end

if ismatrix(X2)
    error('Input X2 was not a tensor')
end

if nargin == 2 && any(size(X1) ~= size(X2))
    error('Dimensions in inputs are not identical')
end

if nargin == 3 && varargin{1} ~= "all"
    error("Only full inner product is availabe, with the use of 'all'")
end

%%%
% Full inner product
%%%
if nargin == 3
    Y = X1(:)' * X2(:);
end

%%%
% Outter product
%%%
% calculate the outter product of the unfolded tensor, then refold back
if nargin == 2
    Y = reshape(X1(:) * X2(:)', [size(X1) size(X2)]);
end

end