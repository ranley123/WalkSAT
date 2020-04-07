import csv
import matplotlib.pyplot as plt
from mpl_toolkits import mplot3d


def read_flip_file(filename):
    x1_flip = []
    x2_flip = []
    y_flip = []

    with open(filename, newline='') as csvfile:
        reader = csv.reader(csvfile, delimiter=' ', quotechar='|')
        for row in reader:
            # print(row)

            words = row[0].split(",")
            num_vars = int(words[0])
            num_clauses = int(words[1])
            flip_freq = int(words[2])
            
            x1_flip.append(num_vars)
            x2_flip.append(num_clauses)
            y_flip.append(flip_freq)
    
    return x1_flip, x2_flip, y_flip



def plot_flip_freq():
    fig = plt.figure()
    ax = plt.axes(projection='3d')

    x1, x2, y = read_flip_file("flip.csv")
    # print('x1: ', x1)
    # print('y: ', y)
    plt.xlabel('number of vars')
    plt.ylabel('average flip frequency')
    ax.plot3D(x1, x2, y, 'gray')
    plt.show()
    plt.savefig('flip.png')
    

if __name__ == "__main__":
    plot_flip_freq()