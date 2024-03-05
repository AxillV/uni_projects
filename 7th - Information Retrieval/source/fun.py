"""Module containing everything needed for the implentation project
of course CEID1037 - Information Retrieval 2023-2024

Typical usage example:
    inv_index = InvertedIndex("docs/")
    inv_index.create_index()

    vsm = VectorSpaceModel(inv_index, 'n', 'f', 'c', 'n', 'f', 'x')
    vsm.create_document_term_matrix()

    query = ("What are the effects of calcium on the physical properties of "
             "mucus from CF patients")
    query = query.upper().split()
    relevant = ("00139 00151 00166 00311 00370 00392 00439 00440 00441 00454 "
    "00461 00502 00503 00505 00520 00522 00526 00527 00533 00593 00619 00737 "
    "00742 00789 00827 00835 00861 00875 00891 00921 00922 01175 01185 01222")
    relevant = relevant.split()

    similarity = vsm.query(query)
    pairs = zip(vsm.inverted_index.document_list, similarity)

    for i, doc in enumerate(sorted_pairs):
        if doc[0] in relevant:
            print("%s (%f) in position %d" % (doc[0], doc[1], len(sorted_pairs)-i))
"""
from config import PRINT_OUTPUTS, EXPORT_OUTPUTS, LIMIT_RESULTS

from math import log10, sqrt
import os
from typing import Literal


class InvertedIndex:
    """Class for creating and accessing an inverted index

    Allows creating an inverted index in bulk via the createIndex() method, which
    utilizes the documentDirectory given during initialization. Uses defaultdict
    with default Nonetype instead of normal dictionaries for cleaner code.

    Properties (read-only):
        index: Dictionary containing the inverted index. The index is structured as follows:
            Each value is itself a dictionary, with the key being the document
            name and the value being the list of positions in the document where
            the word occurs.
        document_list: List containing the names of all documents.
        document_directory: Path to the directory containing the documents.
    """
    def __init__(self, document_directory: str):
        """Initializes the InvertedIndex object

        Args:
            document_directory: Path to the directory containing the documents,
            can include or omit the trailing slash.
        """
        self._index: dict[str, dict[str, list[int]]] = {}
        self._document_list: list[str] = []
        self._document_directory: str = document_directory

    @property
    def index(self) -> dict[str, dict[str, list[int]]]:
        return self._index

    @property
    def document_list(self) -> list[str]:
        return self._document_list

    @property
    def document_directory(self) -> str:
        return self._document_directory

    def _add_occurrence(self, word: str, document_name: str, position_in_doc: int):
        """Adds a word to the inverted index

        Args:
            word: The word to add to the index
            document_name: The name of the document where the word occurs
            position_in_doc: The position of the word in the document
        """
        # Create data structures if they do not exist
        if word not in self._index:
            self._index[word] = {}

        if document_name not in self._index[word]:
            self._index[word][document_name] = []

        self._index[word][document_name].append(position_in_doc)

    def create_index(self):
        """Method to automatically construct the inverted index from the documents
        in the documentDirectory given during initialization.
        """
        # Go through all documents, add them to the document list and add all words to the index
        # TODO: order is not at fault, sorted gives same results
        file_names = sorted(os.listdir(self._document_directory))
        for file_name in file_names:
            doc_dir = self._document_directory
            file = open(os.path.join(doc_dir, file_name))
            self._document_list.append(file_name)
        
            position_in_doc = 0
            for word in file:
                # assume 1 word/line
                word_to_add = word.split()        # Split from newline char
                if not word_to_add:        # Skip if empty row
                    position_in_doc += 1
                    continue

                self._add_occurrence(word_to_add[0], file_name, position_in_doc)

                position_in_doc += 1


class VectorSpaceModel:
    """Class for creating and querying a vector space model

    Allows creating a document term matrix in bulk via the create_document_term_matrix()
    method, which utilizes an inverted index object. The document term matrix is
    stored as a list of lists, where each list corresponds to a document and each
    element in the list corresponds to the weight of a word in the document. The
    weights are calculated using the tf-idf formula, with the tf and idf factors
    being determined by the parameters given during initialization.

    Properties (read-only):
        document_term_matrix: The document term matrix, as a list of lists.
        inverted_index: The inverted index object used to create the document term matrix
        doc_tf: The tf factor used for the document term matrix
        doc_idf: The idf factor used for the document term matrix
        doc_normalization: The normalization factor used for the document term matrix
        query_tf: The tf factor used for queries
        query_idf: The idf factor used for queries
        query_normalization: The normalization factor used for queries
    """
    def __init__(
        self,
        inverted_index: InvertedIndex,
        doc_tf: Literal['b', 't', 'n'],
        doc_idf: Literal['x', 'f', 'p'],
        doc_normalization: Literal['x', 'c'],
        query_tf: Literal['b', 't', 'n'],
        query_idf: Literal['x', 'f', 'p'],
        query_normalization: Literal['x', 'c']  
    ):
        self._document_term_matrix: list[list[float]] = []
        self._inverted_index = inverted_index
        self._doc_tf: Literal['b', 't', 'n'] = doc_tf
        self._doc_idf: Literal['x', 'f', 'p'] = doc_idf
        self._doc_normalization: Literal['x', 'c'] = doc_normalization
        self._query_tf: Literal['b', 't', 'n'] = query_tf
        self._query_idf: Literal['x', 'f', 'p'] = query_idf
        self._query_normalization: Literal['x', 'c'] = query_normalization
        
        # Check if combinations are valid
        valid_factors = [['n','f','x'],
                         ['n','f','c'],
                         ['t','f','c'],
                         ['t','x','c']]
        
        suggested_combinations = [['t','f','c','n','f','x'],
                                     ['t','x','c','n','f','x'],
                                     ['n','f','c','n','f','x']]
        
        doc_factors = [doc_tf, doc_idf, doc_normalization]
        query_factors = [query_tf, query_idf, query_normalization]
        if (doc_factors not in valid_factors or
            query_factors not in valid_factors):
            print("TF-IDF combinations are not valid, check the documentation!")
            return None
        
        if doc_factors + query_factors not in suggested_combinations:
            print("Warning: document + query factor combination is not suggested!")

        valid_doc_combinations = [['n','f','c'],
                                  ['t','f','c'],
                                  ['t','x','c']]

        valid_query_combinations = [['n','f','x'],
                                  ['t','f','c'],
                                  ['t','x','c']]

    @property
    def document_term_matrix(self) -> list[list[float]]:
        return self._document_term_matrix

    @property
    def inverted_index(self) -> InvertedIndex:
        return self._inverted_index

    @property
    def doc_tf(self) -> Literal['b', 't', 'n']:
        return self._doc_tf

    @property
    def doc_idf(self) -> Literal['x', 'f', 'p']:
        return self._doc_idf

    @property
    def doc_normalization(self) -> Literal['x', 'c']:
        return self._doc_normalization

    @property
    def query_tf(self) -> Literal['b', 't', 'n']:
        return self._query_tf

    @property
    def query_idf(self) -> Literal['x', 'f', 'p']:
        return self._query_idf

    @property
    def query_normalization(self) -> Literal['x', 'c']:
        return self._query_normalization

    def _t_x_c_calculcation(self, word_counts: list[int]) -> list[float]:
        """TF-IDF calculation method using the formula:
        w = tf * idf / sqrt(tf^2 * idf^2), where:
        tf = tf
        idf = 1

        Args:
            word_counts: A list of the number of times each word occurs in the document

        Returns:
            A list of the weights of each word in the document
        """
        index = self._inverted_index.index
        words = index.keys()
        weights_sum_square = 0     # for normalization

        # For normalization (idf = 1)
        for i in range(len(words)):
            weights_sum_square += word_counts[i] * word_counts[i]

        # Normalization calculation
        # TF = tf
        norm_factor = 1/sqrt(weights_sum_square)
        word_counts = [word_count * norm_factor
                       for word_count in word_counts]

        return word_counts

    def _t_f_c_calculcation(self, word_counts: list[int]) -> list[float]:
        """TF-IDF calculation method using the formula:
        w = tf * idf / sqrt(tf^2 * idf^2), where:
        tf = tf
        idf = log10(total_documents/documents_containting_word)

        Args:
            word_counts: A list of the number of times each word occurs in the document

        Returns:
            A list of the weights of each word in the document
        """
        index = self._inverted_index.index
        words = index.keys()
        total_documents = len(self._inverted_index.document_list)
        weights_sum_square = 0     # for normalization

        # IDF calculation (can be applied since tf=tf)
        for i, word in enumerate(words):
            documents_containting_word = len(index[word])
            word_counts[i] *= log10(total_documents/documents_containting_word)
            weights_sum_square += word_counts[i] * word_counts[i]     # for norm

        # Normalization calculation
        # TF = tf
        norm_factor = 1/sqrt(weights_sum_square)
        word_counts = [word_count * norm_factor
                       for word_count in word_counts]

        return word_counts

    def _n_f_x_calculcation(self, word_counts: list[int]) -> list[float]:
        """TF-IDF calculation method using the formula:
        w = tf * idf, where:
        tf = 0.5 + 0.5 * (tf / max_tf) [max_tf = largest tf in document]
        idf = log10(total_documents/documents_containting_word)

        Args:
            word_counts: A list of the number of times each word occurs in the document

        Returns:
            A list of the weights of each word in the document
        """
        index = self._inverted_index.index
        words = index.keys()
        total_documents = len(self._inverted_index.document_list)
        max_word_count = 0     # for tf calculation
        idf_factors = []

        # IDF calculation (saves to array for later use)
        for i, word in enumerate(words):
            documents_containting_word = len(index[word])

            # for finding largest tf
            if word_counts[i] > max_word_count:
                max_word_count = word_counts[i]

            idf_factors.append(log10(total_documents/documents_containting_word))

        # TF calculation
        for i in range(len(word_counts)):
            word_counts[i] = 0.5 + 0.5 * (word_counts[i] / max_word_count)     # tf
            word_counts[i] = word_counts[i] * idf_factors[i]        # idf

        return word_counts

    def _n_f_c_calculation(self, word_counts: list[int]) -> list[float]:
        """TF-IDF calculation method using the formula:
        w = tf * idf / sqrt(tf^2 * idf^2), where:
        tf = 0.5 + 0.5 * (tf / max_tf) [max_tf = largest tf in document]
        idf = log10(total_documents/documents_containting_word)

        Args:
            word_counts: A list of the number of times each word occurs in the document

        Returns:
            A list of the weights of each word in the document
        """
        index = self._inverted_index.index
        words = index.keys()
        total_documents = len(self._inverted_index.document_list)
        max_word_count = 0     # for tf calculation
        weights_sum_square = 0      # for norm
        idf_factors = []

        # IDF & norm calculation (saves to array for later use)
        for i, word in enumerate(words):
            documents_containting_word = len(index[word])

            # for finding largest tf
            if word_counts[i] > max_word_count:
                max_word_count = word_counts[i]

            idf_factors.append(log10(total_documents/documents_containting_word))

        # TF calculation and sum of squares for norm
        for i in range(len(word_counts)):
            word_counts[i] = 0.5 + 0.5 * (word_counts[i] / max_word_count)     # tf
            word_counts[i] = word_counts[i] * idf_factors[i]        # idf
            weights_sum_square += word_counts[i] * word_counts[i]       # norm

        # normalization
        norm_factor = 1/sqrt(weights_sum_square)
        word_counts = [word_count * norm_factor
                       for word_count in word_counts]

        return word_counts

    def _t_f_x_calculation(self, word_counts: list[int]) -> list[float]:

        index = self._inverted_index.index
        words = index.keys()
        total_documents = len(self._inverted_index.document_list)
        max_word_count = 0     # for tf calculation
        idf_factors = []

        

    def _calculate_tf_idf(
            self,
            word_counts: list[int],
            tf_mode: Literal['b', 't', 'n'],
            idf_mode: Literal['x', 'f', 'p'],
            normalization_mode: Literal['x', 'c']) -> list[float]:
        """Wrapper method for the different tf-idf calculation methods
        Args:
            word_counts: A list of the number of times each word occurs in the document
            tf_mode: The tf factor to use
            idf_mode: The idf factor to use
            normalization_mode: The normalization factor to use

        Returns:
            A list of the weights of each word in the document
        """

        match [tf_mode, idf_mode, normalization_mode]:
            case ['t', 'x', 'c']:
                return self._t_x_c_calculcation(word_counts)
            case ['t', 'f', 'c']:
                return self._t_f_c_calculcation(word_counts)
            case ['n', 'f', 'x']:
                return self._n_f_x_calculcation(word_counts)
            case ['n', 'f', 'c']:
                return self._n_f_c_calculation(word_counts)
            case ['t', 'f', 'x']:
                return self._t_f_x_calculation(word_counts)
            case _:
                 return -1 
  

    def create_document_term_matrix(self):
        """Method for creating the document term matrix
        Uses the inverted index object given during initialization to create the
        document term matrix.
        """
        tf_mode = self._doc_tf
        idf_mode = self.doc_idf
        normalization_mode = self.doc_normalization
        index = self._inverted_index.index
        words = index.keys()
        documents = self._inverted_index.document_list

        # For each document, create its term_array and calculate it's weight
        for i, document in enumerate(documents):
            if PRINT_OUTPUTS:
                if i % 100 == 0:
                    print("Processing document " + str(i + 1) + " of " + str(len(documents)))

            word_counts = [len(index[word][document])
                           if document in index[word] else 0
                           for word in words]
            tf_idf = self._calculate_tf_idf(word_counts,
                                            tf_mode,
                                            idf_mode,
                                            normalization_mode)
            self._document_term_matrix.append(tf_idf)

    def _inner_product(
            self,
            list_1: list[float],
            list_2: list[float]) -> float:
        """Method for calculating the inner product of two lists
        Args:
            list_1: The first list
            list_2: The second list

        Returns:
            The inner product of the two lists
        """

        result = 0
        for i in range(len(list_1)):
            result += list_1[i] * list_2[i]

        return result

    def query(self, text: list[str]) -> list[tuple[str, float]]:
        """Method for querying the document term matrix
        Args:
            text: The query text, as a list of words

        Returns:
            A list of tuples, where each tuple contains the name of a document and
        """
        tf_mode = self._query_tf
        idf_mode = self._query_idf
        normalization_mode = self._query_normalization

        # create empty dict from corpus words and add occurances from query
        word_counts = {}

        for key in self._inverted_index.index.keys():
            word_counts[key] = 0

        for word in text:
            if word in word_counts:     # Skip if queried word is not in corpus
                word_counts[word] += 1

        tf_idf = self._calculate_tf_idf(list(word_counts.values()),
                                        tf_mode,
                                        idf_mode,
                                        normalization_mode)

        similarity = [self._inner_product(tf_idf, document)
                      for document in self._document_term_matrix]

        pairs = zip(self.inverted_index.document_list, similarity)
        sorted_pairs = sorted(pairs, key=lambda x: x[1], reverse=True)

        return sorted_pairs[0:LIMIT_RESULTS-1]


def query_ranking(
        vsm: VectorSpaceModel,
        query: list[str],
        relevant: list[str]):
    similarity = vsm.query(query)

    for i, doc in enumerate(similarity):
        if doc[0] in relevant:
            if PRINT_OUTPUTS:
                print("%s (%f) in position %d" % (doc[0], doc[1], i + 1))

            if EXPORT_OUTPUTS:
                with open(os.path.join("output"), "a") as output_file:
                    output_file.write("%s (%f) in position %d" % (doc[0], doc[1], i + 1))
                output_file.close()


def query_metrics(
        vsm: VectorSpaceModel,
        query: list[str],
        relevant: list[str]):
    similarity = vsm.query(query)

    # score variables
    average_precision = 0
    true_positive = 0
    false_negative = 0
    false_positive = 0

    # setup variables
    first_relevant_seen = False
    relevant_seen = 0


    for i, doc in enumerate(similarity):
        if doc[0] in relevant:
            # setup variables
            if not first_relevant_seen:
                first_relevant_seen = True
                reciprocal_rank = 1 / (i + 1)

            relevant_seen += 1

            # score variables
            average_precision += relevant_seen / (i+1)
            true_positive += 1  # Document is relevant and retrieved
        else:
            false_positive += 1  # Document is retrieved but not relevant
    
    # Calculate False Negative
    false_negative = len(relevant) - true_positive

    # score calculations
    average_precision /= relevant_seen
    precision = relevant_seen / LIMIT_RESULTS
    recall = relevant_seen / len(relevant)
    f1_score = 2 * (precision * recall) / (precision + recall)

    if PRINT_OUTPUTS:
        print("Precision=%.4f" % precision)
        print("Recall=%.4f" % recall)
        print("F1 Score=%.4f" % f1_score)
        print("True Positive=%d" % true_positive)
        print("False Positive=%d" % false_positive)
        print("False Negative=%d" % false_negative)
        print("Average Precision=%.4f" % average_precision)
        print("Reciprocal Rank=%.4f" % reciprocal_rank)

    if EXPORT_OUTPUTS:
        with open(os.path.join("output"), "a") as output_file:
            output_file.write(str(precision) +
                              ',' +
                              str(recall) +
                              ',' +
                              str(f1_score) +
                              ',' +
                              str(true_positive) +
                              ',' +
                              str(false_positive) +
                              ',' +
                              str(false_negative) +
                              ',' +
                              str(average_precision) +
                              ',' +
                              str(reciprocal_rank) +
                              '\n')
        output_file.close()


if __name__ == "__main__":
    print("INCORRECT USAGE, RUN MAIN USING main.py FROM ROOT DIRECTORY")