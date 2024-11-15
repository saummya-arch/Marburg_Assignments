import numpy as np
import pandas as pd
from scipy.spatial import distance_matrix
import networkx as nx
import gudhi as gd
import matplotlib.pyplot as plt
import os 




data = np.loadtxt(r'A:\Nishu\Marburg_Assignments\Topological Methods of Data Analytics\Topology_Assignment4\Programming Challenge.txt')

dist_matrix = distance_matrix(data, data)



def get_min_radius_for_connectivity(dist_matrix, points_data):
    num_points = dist_matrix.shape[0]
    unique_distances = np.sort(dist_matrix.flatten())
    
    for radius in unique_distances:
        # Create a graph where edges exist if distance <= radius
        connectivity_graph = nx.Graph()
        
        for i in range(num_points):
            for j in range(i + 1, num_points):
                if dist_matrix[i, j] <= radius:
                    connectivity_graph.add_edge(i, j)
        
        # Check if the graph is connected
        if connectivity_graph.number_of_nodes() > 0 and nx.is_connected(connectivity_graph):

            # Plot the graph using matplotlib
            plt.figure(figsize=(10, 8))
            # Project points_data to 2D by taking only the first two columns (XY plane)
            pos = {i: points_data[i][:2] for i in range(num_points)}
            nx.draw(connectivity_graph, pos, with_labels=False, node_size=30, edge_color='skyblue', node_color='blue')
            plt.title(f"Vietoris-Rips Complex for r = {radius}")
            plt.xlabel("X")
            plt.ylabel("Y")
            plt.show()
            
            return radius
    return None

min_radius = get_min_radius_for_connectivity(dist_matrix, data)
print(f"Minimum radius for connected VR complex: {min_radius}")


# Step 3: Calculate Betti numbers for the VR complex at min_radius
def calculate_betti_numbers(points_data, radius):
    rips_complex_structure = gd.RipsComplex(points=points_data, max_edge_length=radius)
    simplex_structure = rips_complex_structure.create_simplex_tree(max_dimension=3)
    # Compute persistence before getting Betti numbers
    simplex_structure.compute_persistence()
    betti_nums = simplex_structure.betti_numbers()
    return betti_nums




betti_numbers_result = calculate_betti_numbers(data, min_radius)
print(f"Betti numbers for radius = {min_radius}: {betti_numbers_result}")

