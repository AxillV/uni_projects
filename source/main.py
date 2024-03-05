import fun
from config import INPUT_SIZE
import os
import sys

if __name__ == "__main__":
    inverted_index = fun.InvertedIndex("docs/")
    inverted_index.create_index()

    if len(sys.argv) == 7:
        vsm = fun.VectorSpaceModel(inverted_index,
                                   sys.argv[1],
                                   sys.argv[2],
                                   sys.argv[3],
                                   sys.argv[4],
                                   sys.argv[5],
                                    sys.argv[6])
    else:
        print("No command line arguments given. Using default values.")
        vsm = fun.VectorSpaceModel(inverted_index, 'n', 'f', 'c', 'n', 'f', 'x')

    vsm.create_document_term_matrix()

    queries_file = open(os.path.join("data", "Queries.txt"))
    relevant_file = open(os.path.join("data", "Relevant.txt"))

    # Write tf, idf and normalization factors to file
    with open(os.path.join("output"), "a") as output_file:
        output_file.write(vsm.doc_tf +
                          vsm.doc_idf +
                          vsm.doc_normalization +
                          ' ' +
                          vsm.query_tf +
                          vsm.query_idf +
                          vsm.query_normalization +
                          '\n')
        output_file.close()

    for i in range(INPUT_SIZE):
        query = queries_file.readline().replace("-", "")
        relevant = relevant_file.readline().split()
        relevant = ["0" * (5 - len(doc_id)) + doc_id
                    for doc_id in relevant]

        print(str(i + 1) + "." + query)
        fun.query_metrics(vsm, query, relevant)
        print("=========================================")
