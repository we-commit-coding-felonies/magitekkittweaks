import shutil

# generates properly named duplicates
# 80 textures for 1 item, how fun
for i in range(10):
    for j in range(2):
        for k in range(2):
            for l in range(2):
                name = f'./mode{i}_offensive{j}_liquid{k}_woft{l}.png'
                base = "./base/disabled.png"
                if i != 0 and j == 0:
                    if k == 0:
                        base = "./base/no_offense_water.png"
                    else:
                        base = "./base/no_offense_lava.png"
                
                shutil.copy(base, name)