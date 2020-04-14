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

def read_p_value(filename):
    x = []
    y = []
    x_list = []
    y_list = []

    with open(filename, newline='') as csvfile:
        reader = csv.reader(csvfile, delimiter=',')
        for row in reader:
            x = []
            y = []
            # words = row[0].split(",")
            y = row
            if len(y) == 0:
                break
            for i in range(len(y)):
                x.append(round(0.1 * (i + 1), 2))
            x_list.append(x)
            y_list.append(y)
            # print(row)

    return x_list, y_list


def plot_flip_freq():
    fig = plt.figure()
    ax = plt.axes(projection='3d')

    x1, x2, y = read_flip_file("flip.csv")
    # print('x1: ', x1)
    # print('y: ', y)
    ax.set_xlabel('number of vars')
    ax.set_ylabel('number of clauses')
    ax.set_zlabel('average flip frequency')
    ax.plot3D(x1, x2, y, 'gray')
    plt.show()
    plt.savefig('flip.png')
    
def plot_p_value():
    x_list, y_list = read_p_value('../p-eval.csv')
    plt.xlabel('p value')
    plt.ylabel('flip times')

    for i in range(len(x_list)):
        x = x_list[i]
        y = y_list[i]
        plt.plot(x, y, label='line')
    plt.show()
    # get_best_p(y_list)

def get_best_p(y_list):
    p_map = {}
    for y in y_list:
        min_index = find_min_pos(y)
        p = round((min_index + 1) * (0.1), 2)
        if p in p_map:
            p_map[p] += 1
        else:
            p_map[p] = 0
    p_map = {k: v for k, v in sorted(p_map.items(), key=lambda item: item[1])}
    print(p_map)

def find_min_pos(arr):
    min_index = 0
    minimum = arr[0]
    for i in range(len(arr)):
        if arr[i] > minimum:
            min_index = i
            minimum = arr[i]
    return min_index

if __name__ == "__main__":
    # plot_flip_freq()
    plot_p_value()