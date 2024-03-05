import numpy as np
def read_file_expert_score(file_path):
    """Reads a file containing expert scores and extracts relevant information.

    Args:
        file_path (str): Path to the input file.

    Returns:
        list: List of dictionaries containing query information and expert scores.
    """
    def calculate_digit_sum(score):
        """Calculates the sum of digits for the given score."""
        return sum(map(int, str(score)))

    with open(file_path, 'r') as file:
        lines = file.readlines()

    queries = []
    current_query = None
    reading_rd = False
    rd_values = []

    for line in lines:
        tokens = line.strip().split()

        if not tokens or all(token.isspace() for token in tokens):
            # Empty line or line with only spaces, tabs, or newlines
            if current_query and reading_rd:
                current_query['Relevance Scores'].extend(rd_values)
                queries.append(current_query)
                current_query = None
                reading_rd = False
                rd_values = []
        elif tokens[0] == "QN":
            current_query = {'Query Number': int(tokens[1]), 'Relevance Scores': []}
        elif tokens[0] == "RD" or reading_rd:
            rd_values.extend(map(int, tokens[1:]))
            reading_rd = True

    if current_query:
        queries.append(current_query)

    # Calculate the sum of digits for each score
    for query in queries:
        for i in range(1, len(query['Relevance Scores']), 2):
            query['Relevance Scores'][i] = calculate_digit_sum(query['Relevance Scores'][i])
    return queries


class YourClassName:
    @staticmethod
    def calculate_ndcg(results_dict, expert_scores):
        """Calculate Normalized Discounted Cumulative Gain (NDCG) for the given results and binary expert scores.

        Args:
            results_dict (dict): A dictionary containing query indices and corresponding retrieved documents.
            expert_scores (list): List of dictionaries containing query information and binary expert scores (0 or 1).

        Returns:
            dict: A dictionary with query indices as keys and their corresponding NDCG scores as values.
        """
        # Dictionary to store NDCG scores for each query
        query_metrics = {}

        for query_info in expert_scores:
            query_index = query_info['Query Number']
            retrieved_docs = results_dict.get(query_index, [])

            # Extract binary expert relevance scores
            expert_relevance_scores = query_info.get('Relevance Scores', [])

            # Calculate DCG and IDCG for binary relevance scores
            dcg = sum((2 ** rel_score - 1) / np.log2(i + 2) for i, (_, doc_id, _, _) in enumerate(retrieved_docs) if doc_id in expert_relevance_scores)
            idcg = sum((2 ** 1 - 1) / np.log2(i + 2) for i in range(len(retrieved_docs)))

            # Calculate NDCG
            ndcg = dcg / idcg if idcg > 0 else 0

            query_metrics[query_index] = ndcg

        return query_metrics


# Example usage
if __name__ == "__main__":
    file_path = './data/cfquery_detailed'  # Replace with the actual file path
    queries_expert_score = read_file_expert_score(file_path)

    ndcg_results = YourClassName.calculate_ndcg(retrieved_docs, queries_expert_score)
    print(ndcg_results)

  