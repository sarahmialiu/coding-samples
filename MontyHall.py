#SARAH LIU

# The Monty Hall problem is a famous puzzle loosely based around the concept of a game show.
# A gameshow host presents contestants with three doors, one of which has a prize behind it.
# Contestants are asked to pick a door, then the host opens a different door that does not have the prize behind it.
# The following code is meant to experimentally demonstrate how choosing to switch between the remaining doors should result in a higher win rate.

import random

def monty(num_trials, swap):
    random.seed(721)
    win_counter = 0

    for i in range(num_trials):
        car_number = random.randint(0, 2)
        player_guess = random.randint(0, 2)
        false_door = random.randint(0, 2)
        while false_door == car_number or false_door == player_guess:
            false_door = random.randint(0, 2)

        if swap == False and car_number == player_guess:
            win_counter = win_counter + 1

        elif swap == True:
            if (player_guess != 0 and false_door != 0):
                player_guess = 0
            elif (player_guess != 1 and false_door != 1):
                player_guess = 1
            else:
                player_guess = 2

            if player_guess == car_number:
                win_counter = win_counter + 1

    print(float(win_counter / num_trials))

print("Approximate win rate (%) when choosing to swap doors: ")
monty(1000, True)

print("Approximate win rate (%) when choosing not to swap doors: ")
monty(1000, False)
