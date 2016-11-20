# Master's Thesis

The multi-objective X-armed bandit problem is a relatively new area of interest in the field of Artificial Intelligence. The field searches for algorithms that find solutions to multi-objective optimization problems that are susceptible to noise. The solution space to search in is a topological space, with an infinite set of arms to play, and due to the problems being multi-objective there is not one single optimal solution, but a set of optimal solutions. These problems occur a lot in our everyday lives, with a high interest for solutions in the fields of engineering, and finance.

This master's thesis proposes 3 algorithms to solve the X-armed bandit problem. The first one uses a tree data structure to represent the solution space in which the algorithm searches for solutions. To rank solutions, and distinguish between good, and bad solutions the Pareto dominance relation was used. The thesis introduced a naive version of a new algorithm. It decomposes the problem into smaller sub-problems and optimizes them simultaneously. Each sub-problem has a weight vector, and one solution associated to it. Using the weight vector, and scalarization techniques the algorithm is able to give a fitness value to the encountered solutions, and use this to find better solutions. The third algorithm improved the previous one by combining the tree data structure with the decomposition strategy. Each sub-problem helps building a global tree where each node represents a solution. Creating children for a node depends on the fitness value of the solution, which is calculated using the weight vector associated to the sub-problem, and a scalarization technique.

Numerous experiments have been performed, and the results have shown the strengths and weaknesses of each algorithm. All algorithms have been compared with each other, with the non-dominated sorting genetic algorithm II (NSGAII), and with the multi-objective evolutionary algorithm based on decomposition (MOEA/D). The algorithm using Pareto dominance, and the improved algorithm both seem to be able to handle a lot of different kind of problems, and return a large set of solutions, close to the optimal set. They outperform the NSGA-II, and the MOEA/D algorithm on problems with 2, and 3 objectives to optimize.
