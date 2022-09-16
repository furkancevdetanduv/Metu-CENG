import os

with open('transfer_file_TCP.txt', 'r') as file1:
    with open('server/transfer_file_TCP.txt', 'r') as file2:
        same = set(file1).intersection(file2)

#print(same)
filename = "comparison.txt"
with open(filename, "w") as f:
	for line in same:
		f.write(line)

print(os.path.getsize(filename))
	