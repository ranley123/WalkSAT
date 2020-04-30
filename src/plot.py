import csv
import matplotlib.pyplot as plt
from numpy import *
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
    y_list = []

    with open(filename, newline='') as csvfile:
        reader = csv.reader(csvfile, delimiter=',')
        for row in reader:
            x = []
            y = []
            # words = row[0].split(",")
            y = [int(x) for x in row]
            if len(y) == 0:
                break
            y_list.append(y)
            # print(row)

    return y_list


def read_multiple_search_file(filename):
    cur_num_var = 0
    x = []
    y_walk = []
    y_novelty = []

    num_var_list = []
    x_list = []
    y_walk_list = []
    y_novelty_list = []
    

    with open(filename, newline='') as csvfile:
        reader = csv.reader(csvfile, delimiter=',')
        for row in reader:
            num_var = int(row[0])
            num_clause = int(row[1])
            walk_flips = int(row[2])
            novelty_flips = int(row[3])

            if num_var != cur_num_var:
                if cur_num_var != 0:
                    num_var_list.append(cur_num_var)
                    x_list.append(x)
                    y_novelty_list.append(y_novelty)
                    y_walk_list.append(y_walk)
                    
                cur_num_var = num_var
                x = []
                y_novelty = []
                y_walk = []
            
            x.append(num_clause)
            y_walk.append(walk_flips)
            y_novelty.append(novelty_flips)
        num_var_list.append(cur_num_var)
        x_list.append(x)
        y_novelty_list.append(y_novelty)
        y_walk_list.append(y_walk)

    return num_var_list, x_list, y_walk_list, y_novelty_list




def plot_flip_freq():
    x1, x2, y = read_flip_file("flip.csv")
    
    # plt.xlabel('number of vars')
    # plt.ylabel('flip frequency')
    # plt.plot(x1, y)

    plt.xlabel('number of clauses')
    plt.ylabel('flip frequency')
    plt.plot(x2, y)
    plt.show()

def plot_p_value():
    y_list = read_p_value('../p-eval.csv')
    plt.xlabel('p value')
    plt.ylabel('flip times')

    x = linspace(0.1, 0.5, 5)

    # print(len(x))
    # print(len(y))
    for y in y_list:
        plt.plot(x, y)  # plotting t, a separately
    plt.axis((0.1, 0.5, 0, 3000))
    plt.show()
    # get_best_p(y_list)


def plot_multiple_search_flips():
    num_var_list, x_list, y_walk_list, y_novelty_list = read_multiple_search_file("../eval.csv")
    # print(y_novelty_list)
    for i in range(len(num_var_list)):
        x = x_list[i]
        plt.plot(x, y_walk_list[i], 'r', label="walkSAT")
        plt.plot(x, y_novelty_list[i], 'b', label="novelty")
        plt.title("m = " + str(num_var_list[i]))
        plt.legend(loc="upper left")
        plt.ylabel('flip times')
        plt.xlabel('number of clauses')
        plt.show()

def plot_multiple_search_running_time():
    num_var_list, x_list, y_walk_list, y_novelty_list = read_multiple_search_file("../runningtime.csv")
    # print(y_novelty_list)
    for i in range(len(num_var_list)):
        x = x_list[i]
        plt.plot(x, y_walk_list[i], 'r', label="walkSAT")
        plt.plot(x, y_novelty_list[i], 'b', label="novelty")
        plt.title("m = " + str(num_var_list[i]))
        plt.legend(loc="upper left")
        plt.ylabel('running time')
        plt.xlabel('number of clauses')
        plt.show()


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
    plot_flip_freq()
    # plot_p_value()
    # plot_multiple_search_flips()
    # plot_multiple_search_running_time()
