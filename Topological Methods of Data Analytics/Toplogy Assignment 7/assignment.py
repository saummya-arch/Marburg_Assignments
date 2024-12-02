from itertools import combinations
import numpy as np

def parse_simplicial_complex(description):
    """Parse a simplicial complex from a set-theoretic description."""
    return [set(f) for f in description]

def euler_characteristic(simplicial_complex):
    """Compute the Euler characteristic χ(K)."""
    simplex_counts = [0] * (max(len(f) for f in simplicial_complex) + 1)
    for simplex in simplicial_complex:
        simplex_counts[len(simplex) - 1] += 1
    return sum((-1)**i * count for i, count in enumerate(simplex_counts))

def boundary_matrix(simplices, dim):
    """Construct the boundary matrix for dimension dim."""
    lower_dim_simplices = [s for s in simplices if len(s) == dim]
    higher_dim_simplices = [s for s in simplices if len(s) == dim + 1]

    matrix = np.zeros((len(lower_dim_simplices), len(higher_dim_simplices)), dtype=int)
    for j, higher in enumerate(higher_dim_simplices):
        for i, lower in enumerate(lower_dim_simplices):
            if lower.issubset(higher):
                orientation = (-1)**list(higher).index(next(iter(lower)))
                matrix[i, j] = orientation
    return matrix

def betti_numbers(simplicial_complex, field=np.float64):
    """Compute Betti numbers β_i(K, F)."""
    max_dim = max(len(f) for f in simplicial_complex)
    simplices = [set(f) for f in simplicial_complex]

    betti = []
    for dim in range(max_dim):
        boundary = boundary_matrix(simplices, dim + 1)
        kernel_rank = np.linalg.matrix_rank(boundary, tol=1e-9)
        if dim > 0:
            prev_boundary = boundary_matrix(simplices, dim)
            image_rank = np.linalg.matrix_rank(prev_boundary, tol=1e-9)
        else:
            image_rank = 0
        betti.append(kernel_rank - image_rank)
    return betti

# Example Input
simplicial_complex = [
    {0}, {1}, {2}, 
    {0, 1}, {1, 2}, {0, 2}, 
    {0, 1, 2}
]

# Parse the input
K = parse_simplicial_complex(simplicial_complex)

# Compute Euler Characteristic
chi = euler_characteristic(K)

# Compute Betti Numbers
betti_nums = betti_numbers(K)

# Output Results
print(f"Simplicial Complex: {K}")
print(f"Euler Characteristic χ(K): {chi}")
print(f"Betti Numbers β(K): {betti_nums}")






def sanity_check(K):
    """Verify that the alternating sum of Betti numbers matches the Euler characteristic."""
    betti = betti_numbers(K)
    euler = euler_characteristic(K)
    return sum((-1)**i * b for i, b in enumerate(betti)) == euler

# Test Function
def test_case(input_K, expected_betti, expected_chi):
    """Execute a test case, validate results, and return if it passes."""
    K = parse_simplicial_complex(input_K)
    betti = betti_numbers(K)
    chi = euler_characteristic(K)
    sanity = sanity_check(K)

    print(f"Simplicial Complex: {K}")
    print(f"Betti Numbers: {betti} (Expected: {expected_betti})")
    print(f"Euler Characteristic: {chi} (Expected: {expected_chi})")
    print(f"Sanity Check Passed: {sanity}")
    
    # Verify the results
    betti_pass = betti == expected_betti
    chi_pass = chi == expected_chi
    sanity_pass = sanity
    return betti_pass and chi_pass and sanity_pass


# Test Case 1: Complete 5-Simplex
input_K1 = [
    {0}, {1}, {2}, {3}, {4}, {5},
    {0, 1}, {0, 2}, {0, 3}, {0, 4}, {0, 5}, {1, 2}, {1, 3}, {1, 4}, {1, 5}, {2, 3}, {2, 4}, {2, 5}, {3, 4}, {3, 5}, {4, 5},
    {0, 1, 2}, {0, 1, 3}, {0, 1, 4}, {0, 1, 5}, {0, 2, 3}, {0, 2, 4}, {0, 2, 5}, {0, 3, 4}, {0, 3, 5}, {0, 4, 5},
    {1, 2, 3}, {1, 2, 4}, {1, 2, 5}, {1, 3, 4}, {1, 3, 5}, {1, 4, 5}, {2, 3, 4}, {2, 3, 5}, {2, 4, 5}, {3, 4, 5},
    {0, 1, 2, 3}, {0, 1, 2, 4}, {0, 1, 2, 5}, {0, 1, 3, 4}, {0, 1, 3, 5}, {0, 1, 4, 5}, {0, 2, 3, 4}, {0, 2, 3, 5}, {0, 2, 4, 5}, {0, 3, 4, 5},
    {1, 2, 3, 4}, {1, 2, 3, 5}, {1, 2, 4, 5}, {1, 3, 4, 5}, {2, 3, 4, 5},
    {0, 1, 2, 3, 4}, {0, 1, 2, 3, 5}, {0, 1, 2, 4, 5}, {0, 1, 3, 4, 5}, {0, 2, 3, 4, 5}, {1, 2, 3, 4, 5}
]
expected_betti_1 = [1, 0, 0, 0, 0, 0]  # Complete 5-simplex
expected_chi_1 = 1

# Test Case 2: Arbitrary Complex
input_K2 = [
    {0}, {1}, {2}, {3}, {4}, {5},
    {0, 1}, {0, 2}, {0, 3}, {0, 4}, {0, 5}, {1, 2}, {1, 3}, {1, 4}, {1, 5},
    {2, 3}, {2, 4}, {2, 5}, {3, 4}, {3, 5}, {4, 5},
    {0, 1, 4}, {0, 2, 5}, {1, 2, 5}, {0, 1, 3}, {0, 2, 3}, {1, 2, 4},
    {0, 4, 5}, {1, 3, 5}, {2, 3, 4}, {3, 4, 5}
]
expected_betti_2 = [1, 4, 0]  # Arbitrary example
expected_chi_2 = -2

# Test Case 3: Tetrahedron
input_K3 = [
    {0}, {1}, {2}, {3},
    {0, 1}, {0, 2}, {0, 3}, {1, 2}, {1, 3}, {2, 3},
    {0, 1, 2}, {0, 1, 3}, {0, 2, 3}, {1, 2, 3}
]
expected_betti_3 = [1, 0, 0]  # Single 3-simplex
expected_chi_3 = 1

# Test Case 4: Disconnected Points
input_K4 = [{0}, {1}, {2}, {3}, {4}]
expected_betti_4 = [5]  # 5 connected components
expected_chi_4 = 5

# Test Case 5: Single Loop
input_K5 = [
    {0}, {1}, {2},
    {0, 1}, {1, 2}, {2, 0}
]
expected_betti_5 = [1, 1]  # One connected component, one loop
expected_chi_5 = 0

# Test Case 6: Two Loops
input_K6 = [
    {0}, {1}, {2}, {3},
    {0, 1}, {1, 2}, {2, 0},  # First loop
    {2, 3}, {3, 0}           # Second loop
]
expected_betti_6 = [1, 2]  # One connected component, two loops
expected_chi_6 = -1

# Run all test cases
results = []
results.append(test_case(input_K1, expected_betti_1, expected_chi_1))
results.append(test_case(input_K2, expected_betti_2, expected_chi_2))
results.append(test_case(input_K3, expected_betti_3, expected_chi_3))
results.append(test_case(input_K4, expected_betti_4, expected_chi_4))
results.append(test_case(input_K5, expected_betti_5, expected_chi_5))
results.append(test_case(input_K6, expected_betti_6, expected_chi_6))

# Final Results
print("\nSummary of Test Results:")
for i, result in enumerate(results, 1):
    print(f"Test Case {i}: {'Passed' if result else 'Failed'}")
