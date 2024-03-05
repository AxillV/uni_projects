import csv
import os
import matplotlib.pyplot as plt
from config import PRINT_OUTPUTS, EXPORT_OUTPUTS

def read_csv(file_path):
    """
    Reads data from a CSV file.

    Args:
        file_path (str): Path to the CSV file.

    Returns:
        dict: A dictionary containing metric names as keys and lists of metric values as values.
    """
    data = {
        'Precision': [],
        'Recall': [],
        'F1-score': [],
        'True Positives': [],
        'False Positives': [],
        'False Negatives': [],
        'AP': [],
        'RR': []
    }

    try:
        with open(file_path, newline='') as csvfile:
            reader = csv.DictReader(csvfile)
            for row in reader:
                for key, value in row.items():
                    data[key].append(float(value))
    except FileNotFoundError:
        print(f"Error: File not found at {file_path}")
    except Exception as e:
        print(f"Error reading file {file_path}: {e}")

    return data

def plot_metric(metric, search_engine1, search_engine2):
    """
    Plots a specific metric for two search engines.

    Args:
        metric (str): The metric to plot.
        search_engine1 (dict): Data for search engine 1.
        search_engine2 (dict): Data for search engine 2.
    """
    # Create the metrics_visualization folder if it doesn't exist
    folder_path = 'metrics_visualization_100'
    os.makedirs(folder_path, exist_ok=True)

    plt.figure(figsize=(18, 12))  # Set the size of the figure
    x_ticks = list(range(0, len(search_engine1[metric]), 2))
    plt.plot(search_engine1[metric], label='Search Engine VCM (nfc nfx)', color='darkcyan')
    plt.plot(search_engine2[metric], label='Search Engine ColBert', color='darkorange')
    plt.xlabel('Query Number')
    plt.ylabel(metric)
    plt.title(f'{metric} - VCM (nfc nfx) vs ColBert')
    plt.grid(alpha=0.3) 
    plt.legend()
    plt.xticks(x_ticks)
    plt.ylim(0)  # y-axis to start from zero
    plt.xlim(0)  # x-axis to start from zero
    if EXPORT_OUTPUTS:
        plt.savefig(os.path.join(folder_path, f'{metric}_comparison_100_nfc_nfx.png'), dpi=300)
    if PRINT_OUTPUTS:
        plt.show()

def plot_precision_recall_curve(precision, recall, engine_name):
    """
    Plots the precision-recall curve.

    Args:
        precision (list): Precision values.
        recall (list): Recall values.
        engine_name (str): Name of the search engine.
    """
    plt.figure(figsize=(8, 8))
    plt.plot(recall, precision, color='darkblue', lw=2, label='Precision-Recall curve')
    plt.xlabel('Recall')
    plt.ylabel('Precision')
    plt.title(f'Precision-Recall Curve - {engine_name}')
    plt.legend()
    plt.grid(alpha=0.3)
    if EXPORT_OUTPUTS:
        plt.savefig(os.path.join('metrics_visualization', f'precision_recall_curve_{engine_name.lower()}.png'), dpi=300)
    if PRINT_OUTPUTS:
        plt.show()

if __name__ == "__main__":

    file_path1 = './ouput_nfc_nfx.csv'
    file_path2 = './query_metrics_output_100.csv'

    search_engine1_data = read_csv(file_path1)
    search_engine2_data = read_csv(file_path2)

    metrics = [
        'Precision',
        'Recall',
        'F1-score',
        'True Positives',
        'False Positives',
        'False Negatives',
        'AP',
        'RR'
    ]

    for metric in metrics:
      plot_metric(metric, search_engine1_data, search_engine2_data)


    # Calculate and print MAP and MRR
    for i, (search_data, engine_name) in enumerate(zip([search_engine1_data, search_engine2_data], ['VCM (nfc nfx)', 'ColBert'])):
        precision = sum(search_data['Precision']) / len(search_data['Precision'])
        recall = sum(search_data['Recall']) / len(search_data['Recall'])
        f1_score = sum(search_data['F1-score']) / len(search_data['F1-score'])
        true_positives = sum(search_data['True Positives'])/ len(search_data['True Positives'])
        false_positives = sum(search_data['False Positives'])/ len(search_data['False Positives'])
        average_precision = sum(search_data['AP']) / len(search_data['AP'])
        reciprocal_rank = sum(search_data['RR']) / len(search_data['RR'])

        print(f"\nMetrics for Search Engine 100 Queries {engine_name}:")
        print(f"Precision: {precision:.4f}")
        print(f"Recall: {recall:.4f}")
        print(f"F1-score: {f1_score:.4f}")
        print(f"True Positives: {true_positives}")
        print(f"False Positives: {false_positives}")
        print(f"Mean Average Precision (MAP): {average_precision:.4f}")
        print(f"Mean Reciprocal Rank (MRR): {reciprocal_rank:.4f}")
